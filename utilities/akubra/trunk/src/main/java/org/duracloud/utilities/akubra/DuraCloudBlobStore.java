package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.Map;

import javax.transaction.Transaction;

import org.duracloud.client.ContentStore;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.impl.AbstractBlobStore;
import org.fedoracommons.akubra.impl.StreamManager;

/**
 * DuraCloud-backed Akubra implementation.
 * <p>
 * This is a <em>non-transactional</em> store that provides read/write access
 * to content stored within a given space in a given DuraCloud content store.
 * <p>
 * <h2>Blob Ids</h2>
 * This store supports DuraCloud content URLs as blob ids.  These are of the
 * form http://<em>host:port</em>/durastore/<em>spaceID</em>/<em>contentID</em>
 * <p>
 * <h2>Id Generation</h2>
 * This store does not support id generation.
 * <p>
 * <h2>Canonical Ids</h2>
 * This store does not attempt to canonicalize ids; calls to
 * {@link Blob#getCanonicalId} will always return null.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobStore extends AbstractBlobStore {

    private final StreamManager streamManager = new StreamManager();

    private final ContentStore contentStore;

    private final String spaceId;

    /**
     * Creates an instance with the given id, DuraCloud content store and space.
     *
     * @param id the unique identifier of this blobstore.
     * @param contentStore the DuraCloud content store.
     * @param spaceId the space within the content store; will be created
     *        if it doesn't yet exist.
     */
    public DuraCloudBlobStore(URI id,
                              ContentStore contentStore,
                              String spaceId) {
        super(id);
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        // TODO: Check whether the space exists; if it doesn't, create it
    }

    //@Override
    public BlobStoreConnection openConnection(Transaction tx,
                                              Map<String, String> hints)
            throws UnsupportedOperationException, IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new DuraCloudBlobStoreConnection(this, streamManager,
                                                contentStore, spaceId);
    }

}
