package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Space;
import org.easymock.classextension.EasyMock;
import org.fedoracommons.akubra.BlobStore;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.impl.StreamManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * Unit tests for DuraCloudBlobStoreConnection.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobStoreConnectionTest {

    static final String baseURL = "http://test.org/durastore";
    static final String spaceId = "test-space";
    static final String uriPrefix = baseURL + "/" + spaceId + "/";
    static final String contentId = "testContent";
    static final URI blobId = URI.create(uriPrefix + contentId);

    private ContentStore contentStore;

    @BeforeMethod
    public void initContentStore() {
        contentStore = EasyMock.createMock(ContentStore.class);
    }

    @Test
    public void getBlob() throws IOException {
        BlobStoreConnection connection = getConnection(false);
        connection.getBlob(blobId, null);
        EasyMock.verify(contentStore);
    }

    @Test
    public void listBlobIdsWithPrefix() throws IOException {
        BlobStoreConnection connection = getConnection(false);
        connection.listBlobIds(uriPrefix);
        EasyMock.verify(contentStore);
    }

    @Test
    public void listBlobIdsWithoutPrefix() throws IOException {
        BlobStoreConnection connection = getConnection(false);
        connection.listBlobIds(null);
        EasyMock.verify(contentStore);
    }

    @Test(expectedExceptions=IOException.class)
    public void listBlobIdsFailure() throws IOException {
        BlobStoreConnection connection = getConnection(true);
        connection.listBlobIds(null);
        EasyMock.verify(contentStore);
    }

    @Test
    public void sync() throws IOException {
        BlobStoreConnection connection = getConnection(false);
        connection.sync();
    }

    private DuraCloudBlobStoreConnection getConnection(
            boolean exceptionOnGetSpace)
            throws IOException {
        BlobStore blobStore = EasyMock.createMock(BlobStore.class);
        EasyMock.expect(contentStore.getBaseURL()).andReturn(baseURL).anyTimes();
        try {
            if (exceptionOnGetSpace) {
                EasyMock.expect(contentStore.getSpace(spaceId)).andThrow(
                        new ContentStoreException(""));
            } else {
                Space space = new Space();
                EasyMock.expect(contentStore.getSpace(spaceId)).andReturn(
                        space).anyTimes();
            }
        } catch (ContentStoreException e) {
            fail();
        }
        EasyMock.replay(contentStore);
        return new DuraCloudBlobStoreConnection(blobStore,
                                                new StreamManager(),
                                                contentStore,
                                                spaceId);
    }

}
