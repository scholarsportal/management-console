package org.duracloud.utilities.akubraclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;

import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Content;
import org.fedoracommons.akubra.Blob;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.DuplicateBlobException;
import org.fedoracommons.akubra.MissingBlobException;
import org.fedoracommons.akubra.UnsupportedIdException;
import org.fedoracommons.akubra.impl.AbstractBlob;
import org.fedoracommons.akubra.impl.StreamManager;

/**
 * DuraCloud-backed Blob implementation.
 *
 * @author Chris Wilper
 */
class DuraCloudBlob
        extends AbstractBlob
        implements Blob {

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
        } catch (ContentStoreException e) {
            // Blob.delete is idempotent; it shouldn't fail if the content
            // doesn't exist.
            if (!is404(e)) {
                IOException ioe = new IOException("Error deleting blob: " + id);
                ioe.initCause(e);
                throw ioe;
            }
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
        } catch (ContentStoreException e) {
            if (is404(e)) {
                throw new MissingBlobException(id);
            } else {
                IOException ioe = new IOException("Error getting input stream "
                        + "for blob: " + id);
                ioe.initCause(e);
                throw ioe;
            }
        }
    }

    //@Override
    public OutputStream openOutputStream(long arg0, boolean arg1)
            throws IOException, DuplicateBlobException {
        ensureOpen();
        // TODO: implement
        return null;
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
    }

    /**
     * Assuming the blobId is valid, returns the suffix that is the content id.
     */
    private String getContentId(URI blobId) {
        return blobId.toString().substring(0,
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
        } catch (ContentStoreException e) {
            if (is404(e)) {
                return null;
            }
            IOException ioe = new IOException("Error getting metadata for "
                    + "blob: " + id);
            ioe.initCause(e);
            throw ioe;
        }
    }

    /**
     * Returns true if the exception appears to have resulted from an HTTP 404
     * (Not Found) response.
     */
    private boolean is404(ContentStoreException e) {
        // ISSUE: This technique is brittle, but it's the best we can do for
        // now; ContentStore does not currently provide a way of determining
        // this kind of error without resorting to string matching.
        return e.getMessage().indexOf("Response code was 404") != -1;
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