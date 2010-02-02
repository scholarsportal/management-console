package org.duracloud.utilities.akubra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.net.URI;

import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.AbstractBlob;
import org.akubraproject.impl.StreamManager;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.duracloud.storage.error.InvalidIdException;
import org.duracloud.storage.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DuraCloud-backed Blob implementation.
 *
 * @author Chris Wilper
 */
class DuraCloudBlob
        extends AbstractBlob {

    private static final Logger logger = LoggerFactory.getLogger(DuraCloudBlob.class);

    private final StreamManager streamManager;

    private final ContentStore contentStore;

    private final String spaceId;

    private final String contentId;

    private final boolean readAfterWrite;

    DuraCloudBlob(BlobStoreConnection owner,
                  URI blobId,
                  StreamManager streamManager,
                  ContentStore contentStore,
                  String spaceId,
                  boolean readAfterWrite)
              throws UnsupportedIdException {
        super(owner, blobId);
        this.streamManager = streamManager;
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        validateId(blobId);
        this.contentId = getContentId(blobId);
        this.readAfterWrite = readAfterWrite;
    }

    //@Override
    public void delete() throws IOException {
        ensureOpen();
        try {
            contentStore.deleteContent(spaceId, contentId);
            if (readAfterWrite) {
                // wait until !this.exists() or timeout
                checkTillTrueOrTimeout(new Checker() {
                    public String getOperation() {
                        return "Delete of " + id;
                    }
                    public boolean check() throws Exception {
                        return !exists();
                    }
                });
            }
        } catch (NotFoundException e) {
            // Blob.delete is idempotent; it shouldn't fail if the content
            // doesn't exist.
        } catch (ContentStoreException e) {
            IOException ioe = new IOException("Error deleting blob: " + id);
            ioe.initCause(e);
            throw ioe;
        }
    }

    //@Override
    public boolean exists() throws IOException {
        ensureOpen();
        return getMetadata() != null;
    }

    //@Override
    public long getSize() throws IOException, MissingBlobException {
        ensureOpen();
        Map<String, String> md = getMetadata();
        if (md == null) {
            throw new MissingBlobException(id);
        }
        String length = md.get("Content-Length");
        if (length == null) {
            return -1;
        }
        return Long.parseLong(length);
    }

    //@Override
    public Blob moveTo(URI blobId, Map<String, String> hints)
            throws DuplicateBlobException, IOException, MissingBlobException,
            NullPointerException, IllegalArgumentException {
        ensureOpen();

        // ContentStore has no atomic move function, so we copy-then-delete.
        Blob dest = owner.getBlob(blobId, hints);
        InputStream in = openInputStream();
        OutputStream out = dest.openOutputStream(-1, false);
        try {
            IOUtils.copyLarge(in, out);
            delete();
        } finally {
            IOUtils.closeQuietly(in);
            out.close();
        }
        return dest;
    }

    //@Override
    public InputStream openInputStream() throws IOException,
            MissingBlobException {
        ensureOpen();
        try {
            Content content = contentStore.getContent(spaceId, contentId);
            return streamManager.manageInputStream(owner, content.getStream());
        } catch (NotFoundException e) {
            throw new MissingBlobException(id);
        } catch (ContentStoreException e) {
            IOException ioe = new IOException("Error getting input stream "
                    + "for blob: " + id);
            ioe.initCause(e);
            throw ioe;
        }
    }

    //@Override
    public OutputStream openOutputStream(final long estimatedSize,
                                         boolean overwrite)
            throws IOException, DuplicateBlobException {
        ensureOpen();

        final Map<String, String> origMeta = getMetadata();
        if (!overwrite && origMeta != null) {
            throw new DuplicateBlobException(id);
        }

        // if needed, set up a listener to wait until the content write
        // is visible to subsequent readers.
        ContentWriteListener listener = null;
        if (readAfterWrite) {
            final DuraCloudBlob blob = this;
            listener = new ContentWriteListener() {
                public void contentWritten() {
                    if (origMeta == null) {
                        // wait until blob.exists() or timeout
                        blob.checkTillTrueOrTimeout(new Checker() {
                            public String getOperation() {
                                return "Create of " + blob.id;
                            }
                            public boolean check() throws Exception {
                                return blob.exists();
                            }
                        });
                    } else {
                        // wait until modify date differs or timeout
                        final String origModDate = origMeta.get(ContentStore.CONTENT_MODIFIED);
                        blob.checkTillTrueOrTimeout(new Checker() {
                            public String getOperation() {
                                return "Replace of " + blob.id;
                            }
                            public boolean check() throws Exception {
                                Map<String, String> meta = getMetadata();
                                if (meta != null) {
                                    String modDate = meta.get(ContentStore.CONTENT_MODIFIED);
                                    return modDate != null
                                            && !modDate.equals(origModDate);
                                } else {
                                    return false;
                                }
                            }
                        });
                    }
                }
            };
        }

        // Since contentStore expects an InputStream from which it can
        // read the bytes to be read, but the Blob interface needs to
        // return an OutputStream to which bytes can be written, we
        // need to set up a pipe and invoke addContent in a separate
        // thread.
        //
        // ExAwareOutputStream is used in conjunction with this pipe
        // in order to facilitate exception-passing; if an exception occurs
        // within contentStore.addContent, the original thread that called
        // openOutputStream will receive that exception the next time it
        // attempts a write or close on the returned output stream.

        PipedOutputStream pipedOut = new PipedOutputStream();
        final ExAwareOutputStream eaOut = new ExAwareOutputStream(pipedOut,
                                                                  listener);
        final PipedInputStream pipedIn = new PipedInputStream(pipedOut);

        Runnable invoker = new Runnable() {
            public void run() {
                try {
                    contentStore.addContent(spaceId,
                                            contentId,
                                            pipedIn,
                                            -1,
                                            "application/octet-stream",
                                            null);
                } catch (ContentStoreException e) {
                    IOException ioe = new IOException("Error writing to store");
                    ioe.initCause(e);
                    eaOut.setException(ioe);
                } finally {
                    IOUtils.closeQuietly(pipedIn);
                }
            }
        };
        new Thread(invoker).start();

        return eaOut;
    }

    private void checkTillTrueOrTimeout(Checker checker) {
        try {
            // check/sleep.2sec/check/sleep.6sec/check/sleep 1.8sec/check/warn
            long sleptMillis = 0;
            long sleepMillis = 200;
            while (!checker.check() && sleptMillis < 2000) {
                Thread.sleep(sleepMillis);
                sleptMillis += sleepMillis;
                sleepMillis = sleepMillis * 3;
            }
            if (sleptMillis >= 2000 && !checker.check()) {
                logger.warn(checker.getOperation() + " committed, but may not "
                        + "yet be visible (read-after-write polling timed out "
                        + "after " + sleptMillis + "ms)");
            } else {
                logger.debug("{} checked successfully after {}ms",
                        checker.getOperation(), sleptMillis);
            }
        } catch (Exception e) {
            logger.warn(checker.getOperation() + " committed, but may not yet "
                    + "be visible (read-after-write polling failed)", e);
        }
    }

    /**
     * Ensures the blobId is not null and begins with the expected prefix
     * based on the content store URL and space id.
     */
    private void validateId(URI blobId)
            throws UnsupportedIdException {
        if (blobId == null) {
            throw new UnsupportedOperationException("Id cannot be null");
        }
        String uriPrefix = getURIPrefix(contentStore, spaceId);
        if (!blobId.toString().startsWith(uriPrefix)) {
            throw new UnsupportedIdException(blobId,
                    "Unsupported blob id: " + blobId.toString()
                    + " (ids for this store should begin "
                    + "with " + uriPrefix + ")");
        }
        try {
            IdUtil.validateContentId(getContentId(blobId));
        } catch (InvalidIdException e) {
            throw new UnsupportedIdException(blobId, "Unsupported blob id: "
                    + blobId.toString() + "(" + e.getMessage() + ")", e);
        }
    }

    /**
     * Assuming the blobId is valid, returns the suffix that is the content id.
     */
    private String getContentId(URI blobId) {
        return blobId.toString().substring(
                getURIPrefix(contentStore, spaceId).length());
    }

    /**
     * Gets the DuraCloud metadata for this content, returning null if the
     * content doesn't exist.
     *
     * @throw IOException if the metadata cannot be read for a reason other
     *        than the content not existing.
     */
    private Map<String, String> getMetadata() throws IOException {
        try {
            return contentStore.getContentMetadata(spaceId, contentId);
        } catch (NotFoundException e) {
            return null;
        } catch (ContentStoreException e) {
            IOException ioe = new IOException("Error getting metadata for "
                    + "blob: " + id);
            ioe.initCause(e);
            throw ioe;
        }
    }

    /**
     * Gets the expected prefix of all blob ids in the given content store
     * and space.
     */
    static String getURIPrefix(ContentStore contentStore,
                               String spaceId) {
        return contentStore.getBaseURL() + "/" + spaceId + "/";
    }

    interface Checker {
        String getOperation();
        boolean check() throws Exception;
    }

}
