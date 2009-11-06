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


public class DuraCloudBlob
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

    public void delete() throws IOException {
        // TODO: implement
    }

    public boolean exists() throws IOException {
        // TODO: implement
        return false;
    }

    public long getSize() throws IOException, MissingBlobException {
        // TODO: implement
        return 0;
    }

    public Blob moveTo(URI arg0, Map<String, String> arg1)
            throws DuplicateBlobException, IOException, MissingBlobException,
            NullPointerException, IllegalArgumentException {
        // TODO: implement
        return null;
    }

    public InputStream openInputStream() throws IOException,
            MissingBlobException {
        // TODO: implement
        return null;
    }

    public OutputStream openOutputStream(long arg0, boolean arg1)
            throws IOException, DuplicateBlobException {
        // TODO: implement
        return null;
    }

}
