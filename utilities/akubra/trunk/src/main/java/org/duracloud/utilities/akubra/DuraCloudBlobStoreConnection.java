package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.Iterator;
import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;

/**
 * DuraCloud-backed BlobStoreConnection implementation.
 *
 * @author Chris Wilper
 */
class DuraCloudBlobStoreConnection
        extends AbstractBlobStoreConnection {

    private final ContentStore contentStore;

    private final String spaceId;

    DuraCloudBlobStoreConnection(BlobStore blobStore,
                                 StreamManager streamManager,
                                 ContentStore contentStore,
                                 String spaceId) {
        super(blobStore, streamManager);
        this.contentStore = contentStore;
        this.spaceId = spaceId;
    }

    //@Override
    public Blob getBlob(URI blobId, Map<String, String> hints)
            throws IOException,
            UnsupportedIdException, UnsupportedOperationException {
        ensureOpen();
        return new DuraCloudBlob(this, blobId, streamManager, contentStore,
                                 spaceId);
    }

    //@Override
    public Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        ensureOpen();
        try {
            // ISSUE: Space.getContentIds() appears to be memory-bound; this
            //        will cause OOM exceptions when trying to iterate over
            //        large spaces.
            return new DuraCloudBlobIdIterator(
                    DuraCloudBlob.getURIPrefix(contentStore, spaceId),
                    contentStore.getSpace(spaceId, null, 0, null).getContentIds().iterator(),
                    filterPrefix);
        } catch (ContentStoreException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    //@Override
    public void sync() throws IOException, UnsupportedOperationException {
        ensureOpen();
        // No-op; ContentStore does not expose a sync function,
        // so we optimistically assume that data is flushed to stable
        // storage right away.
    }

}
