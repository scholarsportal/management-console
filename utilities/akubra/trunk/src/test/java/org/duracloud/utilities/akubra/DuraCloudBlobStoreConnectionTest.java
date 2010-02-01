package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Iterator;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.StreamManager;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.easymock.classextension.EasyMock;
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
        BlobStoreConnection connection = getConnection(uriPrefix, false);
        connection.getBlob(blobId, null);
        EasyMock.verify(contentStore);
    }

    @Test
    public void listBlobIdsWithPrefix() throws IOException {
        BlobStoreConnection connection = getConnection(uriPrefix, false);
        connection.listBlobIds(uriPrefix);
        EasyMock.verify(contentStore);
    }

    @Test
    public void listBlobIdsWithImpossiblePrefix() throws IOException {
        BlobStoreConnection connection = getConnection("impossible", false);
        connection.listBlobIds("impossible");
        EasyMock.verify(contentStore);
    }

    @Test
    public void listBlobIdsWithoutPrefix() throws IOException {
        BlobStoreConnection connection = getConnection(null, false);
        connection.listBlobIds(null);
        EasyMock.verify(contentStore);
    }

    @Test(expectedExceptions=IOException.class)
    public void listBlobIdsFailure() throws IOException {
        BlobStoreConnection connection = getConnection(null, true);
        connection.listBlobIds(null);
        EasyMock.verify(contentStore);
    }

    @Test
    public void sync() throws IOException {
        BlobStoreConnection connection = getConnection(null, false);
        connection.sync();
    }

    private DuraCloudBlobStoreConnection getConnection(String prefix,
            boolean exceptionOnGetSpaceContents)
            throws IOException {
        BlobStore blobStore = EasyMock.createMock(BlobStore.class);
        EasyMock.expect(contentStore.getBaseURL()).andReturn(baseURL).anyTimes();
        try {
            if (exceptionOnGetSpaceContents) {
                EasyMock.expect(contentStore.getSpaceContents(spaceId, null)).andThrow(
                        new ContentStoreException(""));
            } else {
                Iterator<String> iter = new ArrayList<String>().iterator();
                String cPrefix = null;
                if (prefix != null && prefix.startsWith(uriPrefix)) {
                    cPrefix = prefix.substring(uriPrefix.length());
                }
                EasyMock.expect(contentStore.getSpaceContents(spaceId,
                        cPrefix)).andReturn(iter).anyTimes();
            }
        } catch (ContentStoreException e) {
            fail();
        }
        EasyMock.replay(contentStore);
        return new DuraCloudBlobStoreConnection(blobStore,
                                                new StreamManager(),
                                                contentStore,
                                                spaceId,
                                                false);
    }

}
