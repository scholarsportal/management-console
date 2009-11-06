package org.duracloud.utilities.akubraclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;

import java.util.Map;

import org.duracloud.client.ContentStore;
import org.fedoracommons.akubra.Blob;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.DuplicateBlobException;
import org.fedoracommons.akubra.MissingBlobException;
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

    DuraCloudBlob(BlobStoreConnection connection, URI blobId,
                  StreamManager streamManager, ContentStore contentStore,
                  String spaceId) {
        super(connection, blobId);
        this.streamManager = streamManager;
        this.contentStore = contentStore;
        this.spaceId = spaceId;
    }

    //@Override
    public void delete() throws IOException {
        ensureOpen();
        // TODO: implement
    }

    //@Override
    public boolean exists() throws IOException {
        ensureOpen();
        // TODO: implement
        return false;
    }

    //@Override
    public long getSize() throws IOException, MissingBlobException {
        ensureOpen();
        // TODO: implement
        return 0;
    }

    //@Override
    public Blob moveTo(URI arg0, Map<String, String> arg1)
            throws DuplicateBlobException, IOException, MissingBlobException,
            NullPointerException, IllegalArgumentException {
        ensureOpen();
        // TODO: implement
        return null;
    }

    //@Override
    public InputStream openInputStream() throws IOException,
            MissingBlobException {
        ensureOpen();
        // TODO: implement
        return null;
    }

    //@Override
    public OutputStream openOutputStream(long arg0, boolean arg1)
            throws IOException, DuplicateBlobException {
        ensureOpen();
        // TODO: implement
        return null;
    }

}
