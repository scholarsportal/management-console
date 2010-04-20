package org.duracloud.client;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.error.ContentStoreException;
import org.duracloud.unittestdb.util.StorageAccountTestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Andrew Woods
 *         Date: Apr 20, 2010
 */
public class TestAnonymousAccess extends ClientTestBase {

    private static RestHttpHelper restHelper = getAuthorizedRestHelper();

    private final static String spacePrefix = "test-store-anon-";
    private final static String contentPrefix = "test-content-";
    private static String spaceId;
    private static String contentId;

    private static ContentStore store;


    // FIXME: @BeforeClass
    public static void beforeClass() throws Exception {
        StorageAccountTestUtil acctUtil = new StorageAccountTestUtil();
        acctUtil.initializeDurastore(getHost(), getPort(), getContext());

        ContentStoreManager storeManager = new ContentStoreManagerImpl(getHost(),
                                                                       getPort(),
                                                                       getContext());
        store = storeManager.getPrimaryContentStore();

        createSpace();
        createContent();
    }

    private static void createSpace() throws Exception {
        ClientTestBase.HttpCaller caller = new ClientTestBase.HttpCaller() {
            protected RestHttpHelper.HttpResponse call() throws Exception {
                String url = getSpaceUrl();
                String content = null;
                Map<String, String> headers = new HashMap<String, String>();
                headers.put(ContentStore.SPACE_ACCESS,
                            ContentStore.AccessType.OPEN.name());
                return restHelper.put(url, content, headers);
            }
        };
        caller.makeCall(201);
    }

    private static void createContent() throws Exception {
        HttpCaller caller = new HttpCaller() {
            protected RestHttpHelper.HttpResponse call() throws Exception {
                String url = getContentUrl();
                Map<String, String> headers = null;
                return restHelper.put(url, "hello", headers);
            }
        };
        caller.makeCall(201);
    }

    // FIXME: @AfterClass
    public static void afterClass() throws Exception {
        // delete test space
        HttpCaller caller = new HttpCaller() {
            protected RestHttpHelper.HttpResponse call() throws Exception {
                return restHelper.delete(getSpaceUrl());
            }
        };
        caller.makeCall(200);
    }

    @Test
    public void testPlaceHolder() {
        // TODO: remove this when the other tests work.
    }

    // FIXME: @Test
    public void testGetSpaces() {
        boolean allowed = true;
        try {
            store.getSpaces();
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(allowed);
    }

    // FIXME: @Test
    public void testGetSpace() {
        boolean allowed = true;
        try {
            store.getSpace(getSpaceId(), null, 0, null);
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(allowed);
    }

    // FIXME: @Test
    public void testGetSpaceMetadata() {
        boolean allowed = true;
        try {
            store.getSpaceMetadata(getSpaceId());
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(allowed);
    }

    // FIXME: @Test
    public void testCreateSpace() {
        boolean allowed = true;
        try {
            store.createSpace("should-not-work", null);
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    // FIXME: @Test
    public void testSetSpaceMetadata() {
        boolean allowed = true;
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("name-x", "value-x");

        try {
            store.setSpaceMetadata(getSpaceId(), metadata);
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    // FIXME: @Test
    public void testDeleteSpace() {
        boolean allowed = true;
        try {
            store.deleteSpace(getSpaceId());
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    // FIXME: @Test
    public void testGetContent() {
        boolean allowed = true;
        try {
            store.getContent(getSpaceId(), getContentId());
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(allowed);
    }

    // FIXME: @Test
    public void testGetContentMetadata() {
        boolean allowed = true;
        try {
            store.getContentMetadata(getSpaceId(), getContentId());
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(allowed);
    }

    // FIXME: @Test
    public void testStoreContent() {
        boolean allowed = true;
        String data = "hello";
        InputStream content = new ByteArrayInputStream(data.getBytes());
        try {
            store.addContent(getSpaceId(),
                             "should-not-work",
                             content,
                             data.length(),
                             null,
                             null,
                             null);
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    // FIXME: @Test
    public void testSetContentMetadata() {
        boolean allowed = true;
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("name-x", "value-x");
        try {
            store.setContentMetadata(getSpaceId(), getContentId(), metadata);
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    // FIXME: @Test
    public void testDeleteContent() {
        boolean allowed = true;
        try {
            store.deleteContent(getSpaceId(), getContentId());
        } catch (ContentStoreException e) {
            allowed = false;
        }
        Assert.assertTrue(!allowed);
    }

    private static String getSpaceId() {
        if (null == spaceId) {
            Random r = new Random();
            spaceId = spacePrefix + r.nextInt(10000);
        }
        return spaceId;
    }

    private static String getContentId() {
        if (null == contentId) {
            Random r = new Random();
            contentId = contentPrefix + r.nextInt(10000);
        }
        return contentId;
    }

    private static String getSpaceUrl() throws Exception {
        return getBaseUrl() + "/" + getSpaceId();
    }

    private static String getContentUrl() throws Exception {
        return getSpaceUrl() + "/" + getContentId();
    }

}
