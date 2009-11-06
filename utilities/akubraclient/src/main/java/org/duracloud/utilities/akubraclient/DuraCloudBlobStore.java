package org.duracloud.utilities.akubraclient;

import java.io.IOException;

import java.net.URI;

import java.util.Map;

import javax.transaction.Transaction;

import org.duracloud.client.ContentStore;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.impl.AbstractBlobStore;
import org.fedoracommons.akubra.impl.StreamManager;

public class DuraCloudBlobStore extends AbstractBlobStore {

    private final StreamManager streamManager = new StreamManager();

    private final ContentStore cStore;

    private final String spaceId;

    public DuraCloudBlobStore(URI id,
                              ContentStore cStore,
                              String spaceId) {
        super(id);
        this.cStore = cStore;
        this.spaceId = spaceId;
    }

    public BlobStoreConnection openConnection(Transaction tx,
                                              Map<String, String> hints)
            throws UnsupportedOperationException, IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new DuraCloudBlobStoreConnection(this, streamManager, cStore,
                                                spaceId);
    }

}
