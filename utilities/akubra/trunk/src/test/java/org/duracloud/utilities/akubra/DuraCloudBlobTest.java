package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.easymock.EasyMock;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.MissingBlobException;
import org.fedoracommons.akubra.UnsupportedIdException;
import org.fedoracommons.akubra.impl.StreamManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for DuraCloudBlob.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobTest {

    static final String baseURL = "http://test.org/durastore";
    static final String spaceId = "test-space";
    static final String uriPrefix = baseURL + "/" + spaceId + "/";
    static final String contentId = "testContent";
    static final URI blobId = URI.create(uriPrefix + contentId);

    private ContentStore contentStore;

    @BeforeMethod
    public void initContentStore() {
        contentStore = EasyMock.createMock(ContentStore.class);
        EasyMock.expect(contentStore.getBaseURL()).andReturn(baseURL).times(1,
                Integer.MAX_VALUE);
    }

    @Test
    public void deleteExisting() throws IOException, ContentStoreException {
        contentStore.deleteContent(spaceId, contentId);
        EasyMock.expectLastCall();
        EasyMock.replay(contentStore);
        getBlob(blobId).delete();
        EasyMock.verify(contentStore);
    }

    @Test
    public void deleteNonExisting() throws IOException, ContentStoreException {
        contentStore.deleteContent(spaceId, contentId);
        EasyMock.expectLastCall().andThrow(
                new ContentStoreException("Response code was 404"));
        EasyMock.replay(contentStore);
        getBlob(blobId).delete();
        EasyMock.verify(contentStore);
    }

    @Test(expectedExceptions=IOException.class)
    public void deleteFailure() throws IOException, ContentStoreException {
        contentStore.deleteContent(spaceId, contentId);
        EasyMock.expectLastCall().andThrow(new ContentStoreException(""));
        EasyMock.replay(contentStore);
        getBlob(blobId).delete();
        EasyMock.verify(contentStore);
    }

    @Test
    public void existsTrue() throws IOException, ContentStoreException {
        EasyMock.expect(contentStore.getContentMetadata(spaceId, contentId))
                .andReturn(new HashMap<String, String>());
        EasyMock.replay(contentStore);
        assertTrue(getBlob(blobId).exists());
        EasyMock.verify(contentStore);
    }

    @Test
    public void existsFalse() throws IOException, ContentStoreException {
        EasyMock.expect(contentStore.getContentMetadata(spaceId, contentId))
                .andReturn(null);
        EasyMock.replay(contentStore);
        assertFalse(getBlob(blobId).exists());
        EasyMock.verify(contentStore);
    }

    @Test
    public void getSizeKnown() throws IOException, ContentStoreException {
        Map<String, String> md = new HashMap<String, String>();
        md.put("Content-Length", "1024");
        EasyMock.expect(contentStore.getContentMetadata(spaceId, contentId))
                .andReturn(md);
        EasyMock.replay(contentStore);
        assertEquals(getBlob(blobId).getSize(), 1024);
        EasyMock.verify(contentStore);
    }

    @Test
    public void getSizeUnknown() throws IOException, ContentStoreException {
        EasyMock.expect(contentStore.getContentMetadata(spaceId, contentId))
                .andReturn(new HashMap<String, String>());
        EasyMock.replay(contentStore);
        assertEquals(getBlob(blobId).getSize(), -1);
        EasyMock.verify(contentStore);
    }

    @Test(expectedExceptions=MissingBlobException.class)
    public void getSizeNonExisting() throws IOException, ContentStoreException {
        EasyMock.expect(contentStore.getContentMetadata(spaceId, contentId))
                .andThrow(new ContentStoreException("Response code was 404"));
        EasyMock.replay(contentStore);
        getBlob(blobId).getSize();
    }

    private DuraCloudBlob getBlob(URI blobId)
            throws UnsupportedIdException {
        BlobStoreConnection connection = EasyMock.createMock(
                BlobStoreConnection.class);
        return new DuraCloudBlob(connection,
                                 blobId,
                                 new StreamManager(),
                                 contentStore,
                                 spaceId);
    }

}
