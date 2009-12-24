package org.duracloud.s3storage;

import junit.framework.Assert;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.apache.log4j.Logger;
import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;
import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.contains;
import static org.duracloud.storage.util.StorageProviderUtil.count;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Tests the S3 Storage Provider. This test is run via the command line in order
 * to allow passing in S3 credentials.
 *
 * @author Bill Branan
 */
public class S3StorageProviderTest {

    protected static final Logger log = Logger.getLogger(S3StorageProviderTest.class);

    private S3StorageProvider s3Provider;
    private final List<String> spaceIds = new ArrayList<String>();

    private static final String SPACE_META_NAME = "custom-space-metadata";
    private static final String SPACE_META_VALUE = "Testing Space";
    private static final String CONTENT_META_NAME = "custom-content-metadata";
    private static final String CONTENT_META_VALUE = "Testing Content";
    private static final String CONTENT_MIME_NAME = StorageProvider.METADATA_CONTENT_MIMETYPE;
    private static final String CONTENT_MIME_VALUE = "text/plain";
    private static final String CONTENT_DATA = "Test Content";

    @Before
    public void setUp() throws Exception {
        Credential s3Credential = getCredential();
        Assert.assertNotNull(s3Credential);

        String username = s3Credential.getUsername();
        String password = s3Credential.getPassword();
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);

        s3Provider = new S3StorageProvider(username, password);
    }

    @After
    public void tearDown() {
        clean();
        s3Provider = null;
    }

    private void clean() {
        for (String spaceId : spaceIds) {
            try {
                s3Provider.deleteSpace(spaceId);
            } catch (Exception e) {
                // do nothing.
            }
        }
    }

    private Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        return dbUtil.findCredentialForProvider(StorageProviderType.AMAZON_S3);
    }

    private String getNewSpaceId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String spaceId = "duracloud-test-space." + random;
        spaceIds.add(spaceId);
        return spaceId;
    }

    private String getNewContentId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String contentId = "duracloud-test-content." + random;
        return contentId;
    }

    @Test
    public void testS3StorageProvider() throws Exception {
        String spaceId = getNewSpaceId();

        // test createSpace()
        log.debug("Test createSpace()");
        s3Provider.createSpace(spaceId);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");


        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(SPACE_META_NAME, SPACE_META_VALUE);
        s3Provider.setSpaceMetadata(spaceId, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Map<String, String> sMetadata = s3Provider.getSpaceMetadata(spaceId);

        assertTrue(sMetadata.containsKey(StorageProvider.METADATA_SPACE_CREATED));
        assertTrue(sMetadata.containsKey(StorageProvider.METADATA_SPACE_COUNT));
        assertTrue(sMetadata.containsKey(StorageProvider.METADATA_SPACE_ACCESS));
        assertNotNull(sMetadata.get(StorageProvider.METADATA_SPACE_CREATED));
        assertNotNull(sMetadata.get(StorageProvider.METADATA_SPACE_COUNT));
        assertNotNull(sMetadata.get(StorageProvider.METADATA_SPACE_ACCESS));

        assertTrue(sMetadata.containsKey(SPACE_META_NAME));
        assertEquals(SPACE_META_VALUE, sMetadata.get(SPACE_META_NAME));

        // test getSpaces()
        log.debug("Test getSpaces()");
        Iterator<String> spaces = s3Provider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces,
                            spaceId)); // This will only work when spaceId fits
        // the S3 bucket naming conventions

        // Check space access
        log.debug("Check space access");
        String bucketName = s3Provider.getBucketName(spaceId);
        String spaceUrl = "http://" + bucketName + ".s3.amazonaws.com";
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse spaceResponse = restHelper.get(spaceUrl);
        // Expect a 403 forbidden error because the Space Access is closed by default
        assertEquals(403, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(OPEN)");
        s3Provider.setSpaceAccess(spaceId, AccessType.OPEN);

        // test getSpaceAccess()
        log.debug("Test getSpaceAccess()");
        AccessType access = s3Provider.getSpaceAccess(spaceId);
        assertEquals(access, AccessType.OPEN);

        // Check space access
        log.debug("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(200, spaceResponse.getStatusCode());

        // test addContent()
        log.debug("Test addContent()");
        String contentId = getNewContentId();
        addContent(spaceId, contentId);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Map<String, String> cMetadata = s3Provider.getContentMetadata(spaceId,
                                                                      contentId);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_MIME_VALUE, cMetadata.get(CONTENT_MIME_NAME));
        assertEquals(CONTENT_MIME_VALUE,
                     cMetadata.get(S3Object.METADATA_HEADER_CONTENT_TYPE));
        assertNotNull(cMetadata.get(StorageProvider.METADATA_CONTENT_SIZE));
        assertNotNull(cMetadata.get(StorageProvider.METADATA_CONTENT_CHECKSUM));
        // Make sure date is in RFC-822 format
        String lastModified = cMetadata.get(StorageProvider.METADATA_CONTENT_MODIFIED);
        StorageProvider.RFC822_DATE_FORMAT.parse(lastModified);

        // Check content access
        log.debug("Check content access");
        spaceResponse = restHelper.get(spaceUrl + "/" + contentId);
        assertEquals(200, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(CLOSED)");
        s3Provider.setSpaceAccess(spaceId, AccessType.CLOSED);

        // Check space access
        log.debug("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(403, spaceResponse.getStatusCode());

        // Check content access
        log.debug("Check content access");
        spaceResponse = restHelper.get(spaceUrl + "/" + contentId);
        assertEquals(403, spaceResponse.getStatusCode());

        // add additional content for getContents tests
        String testContent2 = "test-content-2";
        addContent(spaceId, testContent2);
        String testContent3 = "test-content-3";
        addContent(spaceId, testContent3);

        // test getSpaceContents()
        log.debug("Test getSpaceContents()");
        Iterator<String> spaceContents =
            s3Provider.getSpaceContents(spaceId, null);
        assertNotNull(spaceContents);
        assertEquals(3, count(spaceContents));
        // Ensure that space metadata is not included in contents list
        spaceContents = s3Provider.getSpaceContents(spaceId, null);
        String spaceMetaSuffix = S3StorageProvider.SPACE_METADATA_SUFFIX;
        assertFalse(contains(spaceContents, bucketName + spaceMetaSuffix));

        // test getSpaceContentsChunked() maxLimit
        log.debug("Test getSpaceContentsChunked() maxLimit");
        List<String> spaceContentList =
            s3Provider.getSpaceContentsChunked(spaceId, null, 2, null);
        assertNotNull(spaceContentList);
        assertEquals(2, spaceContentList.size());
        String lastItem = spaceContentList.get(spaceContentList.size()-1);
        spaceContentList =
            s3Provider.getSpaceContentsChunked(spaceId, null, 2, lastItem);
        assertNotNull(spaceContentList);
        assertEquals(1, spaceContentList.size());

        // test getSpaceContentsChunked() prefix
        log.debug("Test getSpaceContentsChunked() prefix");
        spaceContentList =
            s3Provider.getSpaceContentsChunked(spaceId, "test", 10, null);
        assertEquals(2, spaceContentList.size());

        // test getContent()
        log.debug("Test getContent()");
        InputStream is = s3Provider.getContent(spaceId, contentId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(CONTENT_DATA));

        // test invalid content
        log.debug("Test getContent() with invalid content ID");
        log.debug("-- Begin expected error log -- ");
        try {
            is = s3Provider.getContent(spaceId, "non-existant-content");
            fail("Exception expected");
        } catch (Exception e) {
        }
        log.debug("-- End expected error log --");

        // test setContentMetadata()
        log.debug("Test setContentMetadata()");
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(CONTENT_META_NAME, CONTENT_META_VALUE);
        s3Provider.setContentMetadata(spaceId, contentId, contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        cMetadata = s3Provider.getContentMetadata(spaceId, contentId);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_META_VALUE, cMetadata.get(CONTENT_META_NAME));
        // Mime type was not included when setting content metadata
        // so it should have been reset to a default value
        assertEquals(StorageProvider.DEFAULT_MIMETYPE, cMetadata.get(
            CONTENT_MIME_NAME));
        assertEquals(StorageProvider.DEFAULT_MIMETYPE,
                     cMetadata.get(S3Object.METADATA_HEADER_CONTENT_TYPE));

        // test deleteContent()
        log.debug("Test deleteContent()");
        s3Provider.deleteContent(spaceId, contentId);
        spaceContents = s3Provider.getSpaceContents(spaceId, null);
        assertFalse(contains(spaceContents, contentId));

        // test deleteSpace()
        log.debug("Test deleteSpace()");
        s3Provider.deleteSpace(spaceId);
        spaces = s3Provider.getSpaces();
        assertFalse(contains(spaces, spaceId));
    }

    private void addContent(String spaceId, String contentId) {
        byte[] content = CONTENT_DATA.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        String checksum = s3Provider.addContent(spaceId,
                                                contentId,
                                                CONTENT_MIME_VALUE,
                                                contentSize,
                                                contentStream);
        compareChecksum(s3Provider, spaceId, contentId, checksum);
    }

    @Test
    public void testSetContentMetadata() {
        String name00 = "request-id";
        String name01 = "content-checksum";
        String name02 = "content-md5";
        String name03 = "Content-Type";
        String name04 = "content-mimetype";
        String name05 = "ETag";
        String name06 = "Date";
        String name07 = "id-2";
        String name08 = "Content-Length";
        String name09 = "content-size";
        String name10 = "content-modified";
        String name11 = "Last-Modified";
        String name12 = "tags";
        String name13 = "Server";

        String value00 = "BA66CF8BF16D69CE";
        String value01 = "c56f855f5dec9276733ff3e2c66ec7df";
        String value02 = "c56f855f5dec9276733ff3e2c66ec7df";
        String value03 = "text/html";
        String value04 = "text/html";
        String value05 = "\"c56f855f5dec9276733ff3e2c66ec7df\"";
        String value06 = "Wed, 25 Nov 2009 16:47:07 GMT";
        String value07 = "GssZwY9fOk4ncr6CZneS1ndkLkfLD7y2vfK12O6PjjeFkQV56Z40WU7eOIPmjdaA";
        String value08 = "59142";
        String value09 = "59142";
        String value10 = "Wed, 25 Nov 2009 11:09:39 EST";
        String value11 = "Wed, 25 Nov 2009 11:09:39 EST";
        String value12 = "a1";
        String value13 = "Apache-Coyote/1.1";

        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(name00, value00);
        contentMetadata.put(name01, value01);
        contentMetadata.put(name02, value02);
        contentMetadata.put(name03, value03);
        contentMetadata.put(name04, value04);
        contentMetadata.put(name05, value05);
        contentMetadata.put(name06, value06);
        contentMetadata.put(name07, value07);
        contentMetadata.put(name08, value08);
        contentMetadata.put(name09, value09);
        contentMetadata.put(name10, value10);
        contentMetadata.put(name11, value11);
        contentMetadata.put(name12, value12);
        contentMetadata.put(name13, value13);

        // Set up the space and content
        String spaceId = getNewSpaceId();
        String contentId = getNewContentId();

        s3Provider.createSpace(spaceId);
        addContent(spaceId, contentId);

        // This is the method under test.
        s3Provider.setContentMetadata(spaceId, contentId, contentMetadata);

        Map<String, String> metadata = s3Provider.getContentMetadata(spaceId,
                                                                     contentId);
        Assert.assertNotNull(metadata);
        Assert.assertTrue(metadata.containsKey(name00));
        Assert.assertTrue(metadata.containsKey(name01));
        // Assert.assertTrue(metadata.containsKey(name02)); // not expected
        Assert.assertTrue(metadata.containsKey(name03));
        Assert.assertTrue(metadata.containsKey(name04));
        Assert.assertTrue(metadata.containsKey(name05));
        Assert.assertTrue(metadata.containsKey(name06));
        Assert.assertTrue(metadata.containsKey(name07));
        Assert.assertTrue(metadata.containsKey(name08));
        Assert.assertTrue(metadata.containsKey(name09));
        Assert.assertTrue(metadata.containsKey(name10));
        Assert.assertTrue(metadata.containsKey(name11));
        Assert.assertTrue(metadata.containsKey(name12));
        // Assert.assertTrue(metadata.containsKey(name13)); // not expected

        Assert.assertNotNull(metadata.get(name00));
        Assert.assertNotNull(metadata.get(name01));
        // Assert.assertNotNull(metadata.get(name02)); // not expected
        Assert.assertNotNull(metadata.get(name03));
        Assert.assertNotNull(metadata.get(name04));
        Assert.assertNotNull(metadata.get(name05));
        Assert.assertNotNull(metadata.get(name06));
        Assert.assertNotNull(metadata.get(name07));
        Assert.assertNotNull(metadata.get(name08));
        Assert.assertNotNull(metadata.get(name09));
        Assert.assertNotNull(metadata.get(name10));
        Assert.assertNotNull(metadata.get(name11));
        Assert.assertNotNull(metadata.get(name12));
        // Assert.assertNotNull(metadata.get(name13)); // not expected
    }

}
