package org.duracloud.client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.IOUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.storage.util.test.StorageAccountTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    protected void setUp() throws Exception {
        String url = "http://" + host + ":" + getPort() + "/" + context + "/stores";
        if(accountXml == null) {
            accountXml = StorageAccountTestUtil.buildTestAccountXml();
        }
        RestHttpHelper restHelper = new RestHttpHelper();
        restHelper.post(url, accountXml, null);

        storeManager = new ContentStoreManager(host, getPort(), context);
        assertNotNull(storeManager);

        store = storeManager.getPrimaryContentStore();
    }

    private static String getPort() throws Exception {
        if(port == null) {
            port = System.getProperty("tomcat.port.default");
            if(port == null) {
                port = defaultPort;
            }
        }
        return port;
    }

    @Override
    @After
    protected void tearDown() throws Exception {
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
        spaceMetadata.put(ContentStoreImpl.SPACE_ACCESS,
                          AccessType.OPEN.name());
        spaceMetadata.put(metaName, metaValue);
        store.createSpace(spaceId, spaceMetadata);

        // Check space
        List<Space> spaces = store.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.size() >= 1);
        Space spaceFromList = null;
        for(Space space : spaces) {
            if(space.getId().equals(spaceId)) {
                spaceFromList = space;
                break;
            }
        }
        assertNotNull(spaceFromList);

        Space space = store.getSpace(spaceId);
        assertNotNull(space);
        assertNotNull(space.getId());
        assertEquals(spaceId, space.getId());
        assertNotNull(space.getContentIds());
        assertTrue(space.equals(spaceFromList));

        // Check space metadata
        Map<String, String> responseMetadata = space.getMetadata();
        assertNotNull(responseMetadata);
        assertEquals(AccessType.OPEN.name(),
                     responseMetadata.get(ContentStoreImpl.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStoreImpl.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStoreImpl.SPACE_CREATED));
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(AccessType.OPEN, store.getSpaceAccess(spaceId));

        // Set space metadata
        metaValue = "Testing Metadata Value";
        spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(ContentStoreImpl.SPACE_ACCESS,
                          AccessType.CLOSED.name());
        spaceMetadata.put(metaName, metaValue);
        store.setSpaceMetadata(spaceId, spaceMetadata);

        // Check space metadata
        responseMetadata = store.getSpaceMetadata(spaceId);
        assertNotNull(responseMetadata);
        assertEquals(AccessType.CLOSED.name(),
                     responseMetadata.get(ContentStoreImpl.SPACE_ACCESS));
        assertNotNull(responseMetadata.get(ContentStoreImpl.SPACE_COUNT));
        assertNotNull(responseMetadata.get(ContentStoreImpl.SPACE_CREATED));
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
                     responseMetadata.get(ContentStoreImpl.CONTENT_CHECKSUM));
        assertEquals(contentMimeType,
                     responseMetadata.get(ContentStoreImpl.CONTENT_MIMETYPE));
        assertEquals(String.valueOf(content.length()),
                     responseMetadata.get(ContentStoreImpl.CONTENT_SIZE));
        assertNotNull(responseMetadata.get(ContentStoreImpl.CONTENT_MODIFIED));

        // Set content metadata
        metaValue = "New Metadata Value";
        contentMetadata = new HashMap<String, String>();
        contentMetadata.put(metaName, metaValue);
        store.setContentMetadata(spaceId, contentId, contentMetadata);

        // Check content metadata
        responseMetadata = store.getContentMetadata(spaceId, contentId);
        assertEquals(metaValue, responseMetadata.get(metaName));
        assertEquals(checksum,
                     responseMetadata.get(ContentStoreImpl.CONTENT_CHECKSUM));
        assertEquals(contentMimeType,
                     responseMetadata.get(ContentStoreImpl.CONTENT_MIMETYPE));
        assertEquals(String.valueOf(content.length()),
                     responseMetadata.get(ContentStoreImpl.CONTENT_SIZE));
        assertNotNull(responseMetadata.get(ContentStoreImpl.CONTENT_MODIFIED));

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

}