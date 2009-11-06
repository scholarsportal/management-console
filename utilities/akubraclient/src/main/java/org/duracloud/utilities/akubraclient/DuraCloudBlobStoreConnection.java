package org.duracloud.utilities.akubraclient;

import java.io.IOException;

import java.net.URI;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.fedoracommons.akubra.Blob;
import org.fedoracommons.akubra.BlobStore;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.UnsupportedIdException;
import org.fedoracommons.akubra.impl.AbstractBlobStoreConnection;
import org.fedoracommons.akubra.impl.StreamManager;


public class DuraCloudBlobStoreConnection
        extends AbstractBlobStoreConnection
        implements BlobStoreConnection {

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

    public Blob getBlob(URI blobId, Map<String, String> hints)
            throws IOException,
            UnsupportedIdException, UnsupportedOperationException {
        return new DuraCloudBlob(this, blobId, streamManager, contentStore,
                                 spaceId);
    }

    public Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
        // ISSUE: Space.getContentIds() appears to be memory-bound; this
        //        will cause OOM exceptions when trying to iterate over
        //        large spaces.
        try {
            List<String> ids = contentStore.getSpace(spaceId).getContentIds();
            // TODO: turn this list of strings into an iterator of ids, and
            //       filter by filterPrefix if non-null
        return null;
        } catch (ContentStoreException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    public void sync() throws IOException, UnsupportedOperationException {
        // No-op; ContentStore does not expose a sync function,
        // so we optimistically assume that data is flushed to stable
        // storage right away.
    }

}
