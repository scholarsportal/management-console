package org.duracloud.client;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.IOUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
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
    public void testSpaces() throws Exception {
        String metaName = "test-metadata";
        String metaValue = "test-value";

        // Create space
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(ContentStore.SPACE_ACCESS,
                          AccessType.OPEN.name());
        spaceMetadata.put(metaName, metaValue);
        store.createSpace(spaceId, spaceMetadata);

        // Check space
        List<String> spaces = store.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.size() >= 1);
        assertTrue(spaces.contains(spaceId));

        Space space = store.getSpace(spaceId);
        assertNotNull(space);
        assertNotNull(space.getId());
        assertEquals(spaceId, space.getId());
        assertNotNull(space.getContentIds());

        // Check space metadata
        Map<String, String> responseMetadata = space.getMetadata();
        assertNotNull(responseMetadata);
        assertEquals(AccessType.OPEN.name(),
                     responseMetadata.get(ContentStore.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_CREATED));
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(AccessType.OPEN, store.getSpaceAccess(spaceId));

        // Set space metadata
        metaValue = "Testing Metadata Value";
        spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(ContentStore.SPACE_ACCESS,
                          AccessType.CLOSED.name());
        spaceMetadata.put(metaName, metaValue);
        store.setSpaceMetadata(spaceId, spaceMetadata);

        // Check space metadata
        responseMetadata = store.getSpaceMetadata(spaceId);
        assertNotNull(responseMetadata);
        assertEquals(AccessType.CLOSED.name(),
                     responseMetadata.get(ContentStore.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStore.SPACE_CREATED));
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(AccessType.CLOSED, store.getSpaceAccess(spaceId));

        // Set space access
        store.setSpaceAccess(spaceId, AccessType.OPEN);
        assertEquals(AccessType.OPEN, store.getSpaceAccess(spaceId));

        // Delete space
        store.deleteSpace(spaceId);
        try {
            store.getSpace(spaceId);
            fail("Exception should be thrown attempting to retrieve deleted space");
        } catch(ContentStoreException cse) {
            assertNotNull(cse.getMessage());
        }
    }

    @Test
    public void testInvalidSpace() throws Exception {
        String invalidSpaceId = "invalid-space-id";
        Map emptyMap = new HashMap<String, String>();

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
            store.getSpace(invalidSpaceId);
            fail("Exception expected on getSpace(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.getSpaceAccess(invalidSpaceId);
            fail("Exception expected on getSpaceAccess(invalidSpaceId)");
        } catch(ContentStoreException expected) {
        }

        try {
            store.setSpaceAccess(invalidSpaceId, AccessType.OPEN);
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
        Map emptyMap = new HashMap<String, String>();

        // Create space
        store.createSpace(spaceId, null);
        Space space = store.getSpace(spaceId);
        assertNotNull(space);

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