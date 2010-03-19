package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
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

    private final boolean readAfterWrite;

    DuraCloudBlobStoreConnection(BlobStore blobStore,
                                 StreamManager streamManager,
                                 ContentStore contentStore,
                                 String spaceId,
                                 boolean readAfterWrite) {
        super(blobStore, streamManager);
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        this.readAfterWrite = readAfterWrite;
    }

    //@Override
    public Blob getBlob(URI blobId, Map<String, String> hints)
            throws IOException,
            UnsupportedIdException, UnsupportedOperationException {
        ensureOpen();
        return new DuraCloudBlob(this, blobId, hints, streamManager,
                                 contentStore, spaceId, readAfterWrite);
    }

    @Override
    public Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        ensureOpen();
        try {
            String uPrefix = DuraCloudBlob.getURIPrefix(contentStore, spaceId);
            String cPrefix = null; // iterate all unless filterPrefix is defined
            if (filterPrefix != null) {
                if (filterPrefix.startsWith(uPrefix)) {
                    // translate filterPrefix to content id prefix
                    cPrefix = filterPrefix.substring(uPrefix.length());
                } else {
                    // no possible matches
                    return new ArrayList<URI>().iterator();
                }
            }
            return new DuraCloudBlobIdIterator(uPrefix,
                    contentStore.getSpaceContents(spaceId, cPrefix));
        } catch (ContentStoreException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void sync() throws IOException, UnsupportedOperationException {
        ensureOpen();
        // No-op; ContentStore does not expose a sync function,
        // so we optimistically assume that data is flushed to stable
        // storage right away.
    }

}
