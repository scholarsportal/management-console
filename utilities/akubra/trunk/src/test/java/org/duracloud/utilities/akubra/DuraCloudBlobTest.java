package org.duracloud.utilities.akubra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.StreamManager;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.easymock.EasyMock;
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

    static final String badContentId = "bad?Content";
    static final String existingContentId = "existingContent";
    static final String sizeUnknownContentId = "sizeUnknownContent";
    static final String nonExistingContentId = "nonExistingContent";
    static final String faultContentId = "faultContent";

    static final URI badBlobId = URI.create(uriPrefix + badContentId);
    static final URI existingBlobId = URI.create(uriPrefix + existingContentId);
    static final URI sizeUnknownBlobId = URI.create(uriPrefix + sizeUnknownContentId);
    static final URI nonExistingBlobId = URI.create(uriPrefix + nonExistingContentId);
    static final URI faultBlobId = URI.create(uriPrefix + faultContentId);

    private ContentStore contentStore;
    private BlobStoreConnection connection;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init() throws ContentStoreException {

        // Mock ContentStore for all tests

        contentStore = EasyMock.createNiceMock(ContentStore.class);
        EasyMock.expect(contentStore.getBaseURL()).andReturn(baseURL).anyTimes();

        // existing content, size known
        Content emptyContent = new Content();
        emptyContent.setStream(new ByteArrayInputStream(new byte[0]));
        EasyMock.expect(contentStore.getContent(spaceId, existingContentId))
                .andReturn(emptyContent).anyTimes();

        Map<String, String> md = new HashMap<String, String>();
        md.put("Content-Length", "1024");
        EasyMock.expect(contentStore.getContentMetadata(spaceId, existingContentId))
                .andReturn(md).anyTimes();

        // existing content, size unknown
        EasyMock.expect(contentStore.getContentMetadata(spaceId, sizeUnknownContentId))
                .andReturn(new HashMap<String, String>()).anyTimes();

        // non-existing content
        NotFoundException notFound = new NotFoundException("");
        EasyMock.expect(contentStore.getContent(spaceId, nonExistingContentId))
                .andThrow(notFound).anyTimes();
        contentStore.deleteContent(spaceId, nonExistingContentId);
        EasyMock.expectLastCall().andThrow(notFound).anyTimes();
        EasyMock.expect(contentStore.getContentMetadata(spaceId,
                nonExistingContentId)).andThrow(notFound).anyTimes();

        // fault content, for triggering non-404 ContentStoreExceptions
        ContentStoreException fault = new ContentStoreException("");
        EasyMock.expect(contentStore.getContent(spaceId, faultContentId))
                .andThrow(fault).anyTimes();
        contentStore.deleteContent(spaceId, faultContentId);
        EasyMock.expectLastCall().andThrow(fault).anyTimes();
        EasyMock.expect(contentStore.getContentMetadata(spaceId,
                faultContentId)).andThrow(fault).anyTimes();
        EasyMock.expect(contentStore.addContent(
                        EasyMock.eq(spaceId),
   	                    EasyMock.eq(faultContentId),
    	                (InputStream) EasyMock.anyObject(),
     	                EasyMock.anyLong(),
      	                EasyMock.eq("application/octet-stream"),
                        (Map<String, String>) EasyMock.isNull()))
                .andThrow(fault).anyTimes();

        // Mock BlobStoreConnection for all tests

        connection = new DuraCloudBlobStoreConnection(
                EasyMock.createMock(BlobStore.class),
                new StreamManager(),
                contentStore,
                spaceId);
    }

    @Test(expectedExceptions=UnsupportedIdException.class)
    public void unsupportedIdBadPrefix() throws UnsupportedIdException {
        EasyMock.replay(contentStore);
        getBlob(URI.create("urn:test:wrong-prefix:"));
    }

    @Test(expectedExceptions=UnsupportedIdException.class)
    public void unsupportedIdBadContentId() throws UnsupportedIdException {
        EasyMock.replay(contentStore);
        getBlob(badBlobId);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void nullId() throws UnsupportedIdException {
        EasyMock.replay(contentStore);
        getBlob(null);
    }

    @Test
    public void deleteExisting() throws IOException, ContentStoreException {
        contentStore.deleteContent(spaceId, existingContentId);
        EasyMock.expectLastCall();
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).delete();
        EasyMock.verify(contentStore);
    }

    @Test
    public void deleteNonExisting() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(nonExistingBlobId).delete();
    }

    @Test(expectedExceptions=IOException.class)
    public void deletefault() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(faultBlobId).delete();
    }

    @Test
    public void existsTrue() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        assertTrue(getBlob(existingBlobId).exists());
    }

    @Test
    public void existsFalse() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        assertFalse(getBlob(nonExistingBlobId).exists());
    }

    @Test(expectedExceptions=IOException.class)
    public void existsFault() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        assertFalse(getBlob(faultBlobId).exists());
    }

    @Test
    public void getSizeKnown() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        assertEquals(getBlob(existingBlobId).getSize(), 1024);
    }

    @Test
    public void getSizeUnknown() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        assertEquals(getBlob(sizeUnknownBlobId).getSize(), -1);
    }

    @Test(expectedExceptions=MissingBlobException.class)
    public void getSizeNonExisting() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(nonExistingBlobId).getSize();
    }

    @Test(expectedExceptions=MissingBlobException.class)
    public void moveNonExisting() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(nonExistingBlobId).moveTo(existingBlobId, null);
    }

    @Test(expectedExceptions=DuplicateBlobException.class)
    public void moveToExisting() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).moveTo(existingBlobId, null);
    }

    @Test
    public void moveToNonExisting() throws IOException, ContentStoreException {
        contentStore.deleteContent(spaceId, existingContentId);
        EasyMock.expectLastCall();
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).moveTo(nonExistingBlobId, null);
    }

    @Test
    public void openInputStreamExisting() throws IOException {
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).openInputStream();
    }

    @Test(expectedExceptions=MissingBlobException.class)
    public void openInputStreamNonExisting() throws IOException {
        EasyMock.replay(contentStore);
        getBlob(nonExistingBlobId).openInputStream();
    }

    @Test(expectedExceptions=IOException.class)
    public void openInputStreamFault() throws IOException, ContentStoreException {
        EasyMock.replay(contentStore);
        getBlob(faultBlobId).openInputStream();
    }

    @Test
    public void openOutputStreamNonExisting()
            throws DuplicateBlobException, UnsupportedIdException, IOException {
        EasyMock.replay(contentStore);
        getBlob(nonExistingBlobId).openOutputStream(-1, true);
    }

    @Test
    public void openOutputStreamExistingOverwrite()
            throws DuplicateBlobException, UnsupportedIdException, IOException {
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).openOutputStream(-1, true);
    }

    @Test(expectedExceptions=DuplicateBlobException.class)
    public void openOutputStreamExistingNoOverwrite()
            throws DuplicateBlobException, UnsupportedIdException, IOException {
        EasyMock.replay(contentStore);
        getBlob(existingBlobId).openOutputStream(-1, false);
    }

    @Test(expectedExceptions=IOException.class)
    public void openOutputStreamFault()
            throws DuplicateBlobException, UnsupportedIdException, IOException {
        EasyMock.replay(contentStore);
        OutputStream out = getBlob(faultBlobId).openOutputStream(-1, true);
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
        out.close();
    }

    private DuraCloudBlob getBlob(URI blobId)
            throws UnsupportedIdException {
        return new DuraCloudBlob(connection,
                                 blobId,
                                 new StreamManager(),
                                 contentStore,
                                 spaceId);
    }

}
