package org.duracloud.chunk.writer;

import org.duracloud.chunk.ChunkTestsConfig;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duracloud.storage.domain.test.db.util.StorageAccountTestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * @author Andrew Woods
 *         Date: Feb 7, 2010
 */
public class TestDuracloudContentWriter {

    private DuracloudContentWriter writer;

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
        spaceId = "contentwriter-test-space-" + random;
    }

    @Before
    public void setUp() throws Exception {
        String url =
            "http://" + host + ":" + getPort() + "/" + context + "/stores";
        if (accountXml == null) {
            accountXml = StorageAccountTestUtil.buildTestAccountXml();
        }
        RestHttpHelper restHelper = new RestHttpHelper();
        restHelper.post(url, accountXml, null);

        storeManager = new ContentStoreManagerImpl(host, getPort(), context);
        Assert.assertNotNull(storeManager);

        store = storeManager.getPrimaryContentStore();
        writer = new DuracloudContentWriter(store);
    }

    private static String getPort() throws Exception {
        if (port == null) {
            ChunkTestsConfig config = new ChunkTestsConfig();
            port = config.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }
        return port;
    }

    @After
    public void tearDown() throws Exception {
        try {
            store.deleteSpace(spaceId);
        } catch (ContentStoreException e) {
            // do nothing.
        }

        store = null;
        writer = null;
    }

    @Test
    public void testWrite() throws Exception {
        long contentSize = 4000;
        InputStream contentStream = createContentStream(contentSize);

        String contentId = "test-contentId";
        String contentMimetype = "text/plain";


        long maxChunkSize = 1024;
        int numChunks = (int) (contentSize / maxChunkSize + 1);
        ChunkableContent chunkable = new ChunkableContent(contentId,
                                                          contentMimetype,
                                                          contentStream,
                                                          contentSize,
                                                          maxChunkSize);
        writer.write(spaceId, chunkable);

        verifyContentAdded(contentId, contentSize, numChunks);
    }

    private void verifyContentAdded(String contentId,
                                    long contentSize,
                                    int numChunks) throws Exception {
        int tries = 0;
        Iterator<String> contents = getSpaceContents(contentId);
        while (null == contents && tries++ < 10) {
            Thread.sleep(1000);
            contents = getSpaceContents(contentId);
        }

        Assert.assertNotNull(contents);
        long totalSize = 0;
        int itemCount = 0;
        while (contents.hasNext()) {
            String id = contents.next();
            Assert.assertTrue(id.startsWith(contentId));

            Content content = getContent(id);
            tries = 0;
            while (null == content && tries++ < 10) {
                Thread.sleep(1000);
                content = getContent(id);
            }
            Assert.assertNotNull(content);
            itemCount++;

            Map<String, String> metadata = content.getMetadata();
            Assert.assertNotNull(metadata);

            String size = metadata.get("content-size");
            Assert.assertNotNull(size);
            totalSize += Long.parseLong(size);
        }

        Assert.assertEquals(numChunks, itemCount);
        Assert.assertEquals(contentSize, totalSize);
    }

    private Content getContent(String id) {
        try {
            return store.getContent(spaceId, id);
        } catch (ContentStoreException e) {
            return null;
        }
    }

    private Iterator<String> getSpaceContents(String contentId) {
        try {
            return store.getSpaceContents(spaceId, contentId);
        } catch (ContentStoreException e) {
            return null;
        }
    }

    private InputStream createContentStream(long size) {
        Assert.assertTrue("let's keep it reasonable", size < 10001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (long i = 0; i < size; ++i) {
            if (i % 101 == 0) {
                out.write('\n');
            } else {
                out.write('a');
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}