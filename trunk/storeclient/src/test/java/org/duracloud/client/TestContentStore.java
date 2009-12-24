package org.duracloud.client;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.IOUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.domain.Content;
import org.duracloud.storage.domain.test.db.util.StorageAccountTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Runtime test of DuraCloud java client.
 *
 * @author Bill Branan
 */
public class TestContentStore
        extends TestCase {

    protected static final Logger log =
        Logger.getLogger(TestContentStore.class);

    private static String host = "localhost";

    private static String port = null;

    private static final String defaultPort = "8080";

    private static String context = "durastore";

    private static String accountXml = null;

    private ContentStoreManager storeManager;

    private ContentStore store;

    private static String spaceId;

    static {
        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "space" + random;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        String url = "http://" + host + ":" + getPort() + "/" + context + "/stores";
        if(accountXml == null) {
            accountXml = StorageAccountTestUtil.buildTestAccountXml();
        }
        RestHttpHelper restHelper = new RestHttpHelper();
        restHelper.post(url, accountXml, null);

        storeManager = new ContentStoreManagerImpl(host, getPort(), context);
        assertNotNull(storeManager);

        store = storeManager.getPrimaryContentStore();
    }

    private static String getPort() throws Exception {
        if (port == null) {
            StoreClientConfig config = new StoreClientConfig();
            port = config.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }

        return port;
    }

    @Override
    @After
    public void tearDown() throws Exception {
        // Make sure the space is deleted
        try {
            store.deleteSpace(spaceId);
        } catch(ContentStoreException cse) {
            // Ignore, the space was likely already removed
        }
    }

    @Test
    public void testContentStoreManager() throws Exception {
        Map<String, ContentStore> contentStoreMap =
            storeManager.getContentStores();
        assertNotNull(contentStoreMap);
        assertFalse(contentStoreMap.isEmpty());
        ContentStore primaryStore = store;
        assertNotNull(primaryStore);
        Iterator<ContentStore> contentStores =
            contentStoreMap.values().iterator();
        boolean primaryInList = false;
        while(contentStores.hasNext()) {
            ContentStore store = contentStores.next();
            assertNotNull(store.getStoreId());
            assertNotNull(store.getStorageProviderType());
            if(store.getStoreId().equals(primaryStore.getStoreId())) {
                primaryInList = true;
                assertEquals(store.getStorageProviderType(),
                             primaryStore.getStorageProviderType());
            }

            ContentStore storeById =
                storeManager.getContentStore(store.getStoreId());
            assertNotNull(storeById);
            assertEquals(store.getStoreId(), storeById.getStoreId());
            assertEquals(store.getStorageProviderType(),
                         storeById.getStorageProviderType());
        }
        assertTrue(primaryInList);
    }

    @Test
    public void testSpaceMetadata() throws Exception {
        String metaName = "test-metadata";
        String metaValue = "test-value";

        // Create space
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(ContentStore.SPACE_ACCESS,
                          ContentStore.AccessType.OPEN.name());
        spaceMetadata.put(metaName, metaValue);
        store.createSpace(spaceId, spaceMetadata);

        // Check space
        List<String> spaces = store.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.size() >= 1);
        assertTrue(spaces.contains(spaceId));

        Map<String, String> responseMetadata = store.getSpaceMetadata(spaceId);
        assertNotNull(responseMetadata);

        // Check space metadata
        assertNotNull(responseMetadata);
        assertEquals(ContentStore.AccessType.OPEN.name(),
                     responseMetadata.get(ContentStore.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_CREATED));
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(ContentStore.AccessType.OPEN, store.getSpaceAccess(spaceId));

        // Set space metadata
        metaValue = "Testing Metadata Value";
        spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(ContentStore.SPACE_ACCESS,
                          ContentStore.AccessType.CLOSED.name());
        spaceMetadata.put(metaName, metaValue);
        store.setSpaceMetadata(spaceId, spaceMetadata);

        // Check space metadata
        responseMetadata = store.getSpaceMetadata(spaceId);
        assertNotNull(responseMetadata);
        assertEquals(ContentStore.AccessType.CLOSED.name(),
                     responseMetadata.get(ContentStore.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_CREATED));
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(ContentStore.AccessType.CLOSED,
                     store.getSpaceAccess(spaceId));

        // Set space access
        store.setSpaceAccess(spaceId, ContentStore.AccessType.OPEN);
        assertEquals(ContentStore.AccessType.OPEN, store.getSpaceAccess(spaceId));

        // Delete space
        store.deleteSpace(spaceId);
        try {
            store.getSpaceMetadata(spaceId);
            fail("Exception should be thrown attempting to retrieve deleted space");
        } catch(ContentStoreException cse) {
            assertNotNull(cse.getMessage());
        }
    }

    @Test
    public void testInvalidSpace() throws Exception {
        String invalidSpaceId = "invalid-space-id";
        Map<String, String> emptyMap = new HashMap<String, String>();

        // Ensure invalid space is not in spaces listing
        List<String> spaces = store.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.size() >= 1);
        assertFalse(spaces.contains(invalidSpaceId));

        try {
            store.deleteSpace(invalidSpaceId);
            fail("Exception expected on deleteSpace(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getSpaceMetadata(invalidSpaceId);
            fail("Exception expected on getSpace(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getSpaceAccess(invalidSpaceId);
            fail("Exception expected on getSpaceAccess(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.setSpaceAccess(invalidSpaceId, ContentStore.AccessType.OPEN);
            fail("Exception expected on setSpaceAccess(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getSpaceMetadata(invalidSpaceId);
            fail("Exception expected on getSpaceMetadata(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.setSpaceMetadata(invalidSpaceId, emptyMap);
            fail("Exception expected on setSpaceMetadata(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            String contentId = "test-content";
            String content = "This is the information stored as content";
            InputStream contentStream = IOUtil.writeStringToStream(content);
            String contentMimeType = "text/plain";
            store.addContent(invalidSpaceId, contentId, contentStream,
                             content.length(), contentMimeType, emptyMap);
            fail("Exception expected on addContent(invalidSpaceId, ...)");
        } catch(ContentStoreException expected) {
        }

        try {
            String contentId = "test-content";
            store.getContent(invalidSpaceId, contentId);
            fail("Exception expected on getContent(invalidSpaceId, ...)");
        } catch(ContentStoreException expected) {
        }

        try {
            String contentId = "test-content";
            store.deleteContent(invalidSpaceId, contentId);
            fail("Exception expected on deleteContent(invalidSpaceId, ...)");
        } catch(ContentStoreException expected) {
        }

        try {
            String contentId = "test-content";
            store.getContentMetadata(invalidSpaceId, contentId);
            fail("Exception expected on getContentMetadata(invalidSpaceId, ...)");
        } catch(ContentStoreException expected) {
        }

        try {
            String contentId = "test-content";
            store.setContentMetadata(invalidSpaceId, contentId, emptyMap);
            fail("Exception expected on setContentMetadata(invalidSpaceId, ...)");
        } catch(ContentStoreException expected) {
        }
    }

    @Test
    public void testSpaceContents() throws Exception {
        String mime = "text/plain";

        // Add content
        store.createSpace(spaceId, null);
        String c1 = "test-content-1";
        InputStream stream = IOUtil.writeStringToStream(c1);
        store.addContent(spaceId, c1, stream, c1.length(), mime, null);
        String c2 = "test-content-2";
        stream = IOUtil.writeStringToStream(c2);
        store.addContent(spaceId, c2, stream, c2.length(), mime, null);
        String c3 = "content-3";
        stream = IOUtil.writeStringToStream(c3);
        store.addContent(spaceId, c3, stream, c3.length(), mime, null);

        // Get all content
        Iterator<String> contentIds = store.getSpaceContents(spaceId);
        int count = 0;
        while(contentIds.hasNext()) {
            String contentId = contentIds.next();
            assertTrue(contentId.equals(c1) ||
                       contentId.equals(c2) ||
                       contentId.equals(c3));
            count++;
        }
        assertEquals(3, count);

        // Get content with prefix
        contentIds = store.getSpaceContents(spaceId, "test");
        count = 0;
        while(contentIds.hasNext()) {
            String contentId = contentIds.next();
            assertTrue(contentId.equals(c1) ||
                       contentId.equals(c2));
            count++;
        }
        assertEquals(2, count);

        // Chunk content list
        List<String> idList =
            store.getSpace(spaceId, null, 2, null).getContentIds();
        assertEquals(2, idList.size());
        String lastItem = idList.get(idList.size()-1);
        idList = store.getSpace(spaceId, null, 2, lastItem).getContentIds();
        assertEquals(1, idList.size());
    }

    @Test
    public void testContent() throws Exception {
        String contentId = "test-content";
        String content = "This is the information stored as content";
        InputStream contentStream = IOUtil.writeStringToStream(content);
        String contentMimeType = "text/plain";
        String metaName = "test-content-metadata";
        String metaValue = "Testing Content Metadata";
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(metaName, metaValue);

        // Add content
        store.createSpace(spaceId, null);
        String checksum = store.addContent(spaceId,
                                           contentId,
                                           contentStream,
                                           content.length(),
                                           contentMimeType,
                                           contentMetadata);
        // Check content checksum
        assertNotNull(checksum);
        ChecksumUtil checksumUtil = new ChecksumUtil(ChecksumUtil.Algorithm.MD5);
        contentStream = IOUtil.writeStringToStream(content);
        assertEquals(checksum, checksumUtil.generateChecksum(contentStream));

        // Check content
        Content responseContent = store.getContent(spaceId, contentId);
        assertNotNull(responseContent);
        assertEquals(content,
                     IOUtil.readStringFromStream(responseContent.getStream()));

        // Check content metadata
        Map<String, String> responseMetadata = responseContent.getMetadata();
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(checksum,
                     responseMetadata.get(ContentStore.CONTENT_CHECKSUM));
        assertEquals(contentMimeType,
                     responseMetadata.get(ContentStore.CONTENT_MIMETYPE));
        assertEquals(String.valueOf(content.length()),
                     responseMetadata.get(ContentStore.CONTENT_SIZE));
        assertNotNull(responseMetadata.get(ContentStore.CONTENT_MODIFIED));

        // Set content metadata
        metaValue = "New Metadata Value";
        contentMetadata = new HashMap<String, String>();
        contentMetadata.put(metaName, metaValue);
        store.setContentMetadata(spaceId, contentId, contentMetadata);

        // Check content metadata
        responseMetadata = store.getContentMetadata(spaceId, contentId);
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(checksum,
                     responseMetadata.get(ContentStore.CONTENT_CHECKSUM));
        assertEquals(contentMimeType,
                     responseMetadata.get(ContentStore.CONTENT_MIMETYPE));
        assertEquals(String.valueOf(content.length()),
                     responseMetadata.get(ContentStore.CONTENT_SIZE));
        assertNotNull(responseMetadata.get(ContentStore.CONTENT_MODIFIED));

        // Delete content
        store.deleteContent(spaceId, contentId);
        try {
            store.getContent(spaceId, contentId);
            fail("Exception should be thrown attempting to retrieve deleted content");
        } catch(ContentStoreException cse) {
            assertNotNull(cse.getMessage());
        }
        store.deleteSpace(spaceId);
    }

    @Test
    public void testInvalidContent() throws Exception {
        String invalidContentId = "invalid-content-id";
        Map<String, String> emptyMap = new HashMap<String, String>();

        // Create space
        store.createSpace(spaceId, null);
        Map<String, String> spaceMetadata = store.getSpaceMetadata(spaceId);
        assertNotNull(spaceMetadata);

        try {
            store.deleteContent(spaceId, invalidContentId);
            fail("Exception expected on deleteContent(spaceId, invalidContentId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getContent(spaceId, invalidContentId);
            fail("Exception expected on getContent(spaceId, invalidContentId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getContentMetadata(spaceId, invalidContentId);
            fail("Exception expected on getContentMetadata(spaceId, invalidContentId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.setContentMetadata(spaceId, invalidContentId, emptyMap);
            fail("Exception expected on setContentMetadata(spaceId, invalidContentId, ...)");
        } catch(ContentStoreException expected) {
        }
    }

}