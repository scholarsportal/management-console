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

/**
 * DuraCloud-backed Blob implementation.
 *
 * @author Chris Wilper
 */
class DuraCloudBlob
        extends AbstractBlob {

    private final StreamManager streamManager;

    private final ContentStore contentStore;

    private final String spaceId;

    private final String contentId;

    DuraCloudBlob(BlobStoreConnection owner,
                  URI blobId,
                  StreamManager streamManager,
                  ContentStore contentStore,
                  String spaceId)
              throws UnsupportedIdException {
        super(owner, blobId);
        this.streamManager = streamManager;
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        validateId(blobId);
        this.contentId = getContentId(blobId);
    }

    //@Override
    public void delete() throws IOException {
        ensureOpen();
        try {
            contentStore.deleteContent(spaceId, contentId);
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
        IOUtils.copyLarge(openInputStream(), dest.openOutputStream(-1, false));
        delete();
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

        if (!overwrite && exists()) {
            throw new DuplicateBlobException(id);
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
        final ExAwareOutputStream eaOut = new ExAwareOutputStream(pipedOut);
        final PipedInputStream pipedIn = new PipedInputStream(pipedOut);

        Runnable invoker = new Runnable() {
            public void run() {
                try {
                    contentStore.addContent(spaceId,
                                            contentId,
                                            pipedIn,
                                            estimatedSize,
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

    /**
     * Ensures the blobId is not null and begins with the expected prefix
     * based on the content store URL and space id.
     */
    private void validateId(URI blobId)
            throws UnsupportedIdException {
        if (blobId == null) {
            throw new NullPointerException("Id cannot be null");
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

}
