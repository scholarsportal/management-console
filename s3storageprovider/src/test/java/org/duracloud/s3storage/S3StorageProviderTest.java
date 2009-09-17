package org.duracloud.s3storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;
import org.jets3t.service.model.S3Object;

import junit.framework.Assert;

import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.contains;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests the S3 Storage Provider. This test is run via the command line in order
 * to allow passing in S3 credentials.
 *
 * @author Bill Branan
 */
public class S3StorageProviderTest {

    protected static final Logger log =
            Logger.getLogger(S3StorageProviderTest.class);

    private S3StorageProvider s3Provider;

    private static String SPACE_ID = null;
    private static final String CONTENT_ID = "duracloud-test-content";
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

        String random = String.valueOf(new Random().nextInt(99999));
        SPACE_ID = "duracloud-test-bucket." + random;
    }

    @After
    public void tearDown() {
        s3Provider = null;
    }

    private Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        return dbUtil.findCredentialForProvider(StorageProviderType.AMAZON_S3);
    }

    @Test
    public void testS3StorageProvider() throws Exception {
        // test createSpace()
        log.debug("Test createSpace()");
        s3Provider.createSpace(SPACE_ID);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");


        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(SPACE_META_NAME, SPACE_META_VALUE);
        s3Provider.setSpaceMetadata(SPACE_ID, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Map<String, String> sMetadata = s3Provider.getSpaceMetadata(SPACE_ID);

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
        assertTrue(contains(spaces, SPACE_ID)); // This will only work when SPACE_ID fits
                                                // the S3 bucket naming conventions

        // Check space access
        log.debug("Check space access");
        String bucketName = s3Provider.getBucketName(SPACE_ID);
        String spaceUrl = "http://" + bucketName + ".s3.amazonaws.com";
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse spaceResponse = restHelper.get(spaceUrl);
        // Expect a 403 forbidden error because the Space Access is closed by default
        assertEquals(403, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(OPEN)");
        s3Provider.setSpaceAccess(SPACE_ID, AccessType.OPEN);

        // test getSpaceAccess()
        log.debug("Test getSpaceAccess()");
        AccessType access = s3Provider.getSpaceAccess(SPACE_ID);
        assertEquals(access, AccessType.OPEN);

        // Check space access
        log.debug("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(200, spaceResponse.getStatusCode());

        // test addContent()
        log.debug("Test addContent()");
        byte[] content = CONTENT_DATA.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        String checksum = s3Provider.addContent(SPACE_ID,
                                                CONTENT_ID,
                                                CONTENT_MIME_VALUE,
                                                contentSize,
                                                contentStream);
        compareChecksum(s3Provider, SPACE_ID, CONTENT_ID, checksum);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Map<String, String> cMetadata =
                s3Provider.getContentMetadata(SPACE_ID, CONTENT_ID);
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
        spaceResponse = restHelper.get(spaceUrl + "/" + CONTENT_ID);
        assertEquals(200, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(CLOSED)");
        s3Provider.setSpaceAccess(SPACE_ID, AccessType.CLOSED);

        // Check space access
        log.debug("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(403, spaceResponse.getStatusCode());

        // Check content access
        log.debug("Check content access");
        spaceResponse = restHelper.get(spaceUrl + "/" + CONTENT_ID);
        assertEquals(403, spaceResponse.getStatusCode());

        // test getSpaceContents()
        log.debug("Test getSpaceContents()");
        Iterator<String> spaceContents = s3Provider.getSpaceContents(SPACE_ID);
        assertNotNull(spaceContents);
        assertTrue(contains(spaceContents, CONTENT_ID));
        // Ensure that space metadata is not included in contents list
        spaceContents = s3Provider.getSpaceContents(SPACE_ID);
        String spaceMetaSuffix = S3StorageProvider.SPACE_METADATA_SUFFIX;
        assertFalse(contains(spaceContents, bucketName + spaceMetaSuffix));

        // test getContent()
        log.debug("Test getContent()");
        InputStream is = s3Provider.getContent(SPACE_ID, CONTENT_ID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(CONTENT_DATA));

        // test invalid content
        log.debug("Test getContent() with invalid content ID");
        log.debug("-- Begin expected error log -- ");
        is = s3Provider.getContent(SPACE_ID, "non-existant-content");
        assertTrue(is == null);
        log.debug("-- End expected error log --");

        // test setContentMetadata()
        log.debug("Test setContentMetadata()");
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(CONTENT_META_NAME, CONTENT_META_VALUE);
        s3Provider.setContentMetadata(SPACE_ID, CONTENT_ID, contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        cMetadata = s3Provider.getContentMetadata(SPACE_ID, CONTENT_ID);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_META_VALUE, cMetadata.get(CONTENT_META_NAME));
        // Mime type was not included when setting content metadata
        // so it should have been reset to a default value
        assertEquals(StorageProvider.DEFAULT_MIMETYPE,
                     cMetadata.get(CONTENT_MIME_NAME));
        assertEquals(StorageProvider.DEFAULT_MIMETYPE,
                     cMetadata.get(S3Object.METADATA_HEADER_CONTENT_TYPE));

        // test deleteContent()
        log.debug("Test deleteContent()");
        s3Provider.deleteContent(SPACE_ID, CONTENT_ID);
        spaceContents = s3Provider.getSpaceContents(SPACE_ID);
        assertFalse(contains(spaceContents, CONTENT_ID));

        // test deleteSpace()
        log.debug("Test deleteSpace()");
        s3Provider.deleteSpace(SPACE_ID);
        spaces = s3Provider.getSpaces();
        assertFalse(contains(spaces, SPACE_ID));
    }

}
