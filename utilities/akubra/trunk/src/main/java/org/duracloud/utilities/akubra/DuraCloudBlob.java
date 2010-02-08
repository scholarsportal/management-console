package org.duracloud.utilities.akubra;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.net.URI;

import java.util.HashMap;
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

    static final String CONTENT_TYPE = "Content-Type";

    static final String CONTENT_LENGTH = "Content-Length";

    static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final Logger logger = LoggerFactory.getLogger(DuraCloudBlob.class);

    private final Map<String, String> blobHints;

    private final StreamManager streamManager;

    private final ContentStore contentStore;

    private final String spaceId;

    private final String contentId;

    private final boolean readAfterWrite;

    DuraCloudBlob(BlobStoreConnection owner,
                  URI blobId,
                  Map<String, String> hints,
                  StreamManager streamManager,
                  ContentStore contentStore,
                  String spaceId,
                  boolean readAfterWrite)
              throws UnsupportedIdException {
        super(owner, blobId);
        this.blobHints = hints;
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
        String length = md.get(ContentStore.CONTENT_SIZE);
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

        // if Content-Type or Content-Length are undefined in given hints,
        // attempt to use the values from this blob's hints, if defined.
        // otherwise, use the stored values for this blob
        if (hints == null) {
            hints = new HashMap<String, String>();
        }
        Map<String, String> md = null;
        if (!hints.containsKey(CONTENT_TYPE)) {
            if (blobHints != null && blobHints.containsKey(CONTENT_TYPE)) {
                hints.put(CONTENT_TYPE, blobHints.get(CONTENT_TYPE));
            } else {
                md = getMetadata();
                if (md != null && md.containsKey(ContentStore.CONTENT_MIMETYPE)) {
                    hints.put(CONTENT_TYPE, md.get(ContentStore.CONTENT_MIMETYPE));
                } else {
                    hints.put(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
                }
            }
        }
        if (!hints.containsKey(CONTENT_LENGTH)) {
            if (blobHints != null && blobHints.containsKey(CONTENT_LENGTH)) {
                hints.put(CONTENT_LENGTH, blobHints.get(CONTENT_LENGTH));
            } else {
                if (md == null) md = getMetadata();
                if (md != null && md.containsKey(ContentStore.CONTENT_SIZE)) {
                    hints.put(CONTENT_LENGTH, md.get(ContentStore.CONTENT_SIZE));
                } else {
                    hints.put(CONTENT_LENGTH, "-1");
                }
            }
        }

        // ContentStore has no atomic move function, so we copy-then-delete.
        InputStream in = openInputStream();
        Blob dest = owner.getBlob(blobId, hints);
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
        final ContentWriteListener listener;
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
        } else {
            listener = null;
        }

        final long length = contentLengthFromHints();

        if (length < 0) {
            // If Content-Length is not provided, we must compute it before
            // the stream is written to contentStore because contentStore will
            // attempt to compute the length in memory if we don't provide
            // it, which can result in memory exhaustion for large files.
            //
            // Here, we take the safe, but slow approach of writing to a
            // temporary local file in order to get the size, then sending
            // the content of that file to contentStore.
            logger.info("Content-Length unspecified; will compute it by "
                    + "writing to temp file before sending to DuraCloud");
            final File tempFile = File.createTempFile("duracloud", null);
            final OutputStream tempFileOut = new BufferedOutputStream(new FileOutputStream(tempFile));
            return new FilterOutputStream(tempFileOut) {
                boolean finished = false;
                @Override
                public void close() throws IOException {
                    if (finished) return;
                    super.close();
                    InputStream in = new FileInputStream(tempFile);
                    try {
                        contentStore.addContent(spaceId,
                                                contentId,
                                                in,
                                                tempFile.length(),
                                                contentTypeFromHints(),
                                                null);
                        if (listener != null) {
                            listener.contentWritten();
                        }
                    } catch (ContentStoreException e) {
                        IOException ioe = new IOException("Error writing to store");
                        ioe.initCause(e);
                        throw ioe;
                    } finally {
                        IOUtils.closeQuietly(in);
                        if (tempFile.delete()) finished = true;
                    }
                }
                @Override
                public void finalize() {
                    if (!finished) {
                        IOUtils.closeQuietly(tempFileOut);
                        tempFile.delete();
                    }
                }
            };
        } else {
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
                                                length,
                                                contentTypeFromHints(),
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
    }

    private String contentTypeFromHints() {
        if (blobHints != null && blobHints.containsKey(CONTENT_TYPE)) {
            return blobHints.get(CONTENT_TYPE).trim();
        } else {
            return DEFAULT_CONTENT_TYPE;
        }
    }

    private long contentLengthFromHints() {
        if (blobHints != null && blobHints.containsKey(CONTENT_LENGTH)) {
            try {
                return Long.parseLong(blobHints.get(CONTENT_LENGTH).trim());
            } catch (NumberFormatException e) {
                logger.warn("Malformed Content-Length hint; using -1");
            }
        }
        return -1;
    }

    // for testing: get the stored Content-Type if this blob exists, else null
    String getContentType() throws IOException {
        Map<String, String> md = getMetadata();
        if (md == null) return null;
        return md.get(ContentStore.CONTENT_MIMETYPE);
    }

    private void checkTillTrueOrTimeout(Checker checker) {
        try {
            // check/sleep.2sec/check/sleep.6sec/
            // check/sleep 1.8sec/check/sleep5.4/check/warn
            long sleptMillis = 0;
            long sleepMillis = 200;
            while (!checker.check() && sleptMillis < 5000) {
                Thread.sleep(sleepMillis);
                sleptMillis += sleepMillis;
                sleepMillis = sleepMillis * 3;
            }
            if (sleptMillis >= 5000 && !checker.check()) {
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
