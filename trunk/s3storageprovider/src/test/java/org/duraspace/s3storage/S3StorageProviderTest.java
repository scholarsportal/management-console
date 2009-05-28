
package org.duraspace.s3storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duraspace.storage.provider.StorageProvider;
import org.duraspace.storage.provider.StorageProvider.AccessType;

import junit.framework.Assert;

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

    private StorageProvider s3Provider;

    private String accessKeyId;

    private static String SPACE_ID = null;
    private static final String CONTENT_ID = "duracloud-test-content";
    private static final String SPACE_META_NAME = StorageProvider.METADATA_SPACE_NAME;
    private static final String SPACE_META_VALUE = "Testing Space";
    private static final String CONTENT_META_NAME = StorageProvider.METADATA_CONTENT_NAME;
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

        accessKeyId = username;
        s3Provider = new S3StorageProvider(username, password);

        String random = String.valueOf(new Random().nextInt(99999));
        SPACE_ID = "duracloud-test-bucket." + random;
    }

    @After
    public void tearDown() {
        s3Provider = null;
        accessKeyId = null;
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

        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Properties spaceMetadata = new Properties();
        spaceMetadata.put(SPACE_META_NAME, SPACE_META_VALUE);
        s3Provider.setSpaceMetadata(SPACE_ID, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Properties sMetadata = s3Provider.getSpaceMetadata(SPACE_ID);
        assertTrue(sMetadata.containsKey(SPACE_META_NAME));
        assertEquals(SPACE_META_VALUE, sMetadata.get(SPACE_META_NAME));

        // test getSpaces()
        log.debug("Test getSpaces()");
        List<String> spaces = s3Provider.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.contains(SPACE_ID)); // This will only work when SPACE_ID fits
                                               // the S3 bucket naming conventions

        // Check space access
        log.debug("Check space access");
        String bucketName =
                ((S3StorageProvider) s3Provider).getBucketName(SPACE_ID);
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
        s3Provider.addContent(SPACE_ID,
                              CONTENT_ID,
                              CONTENT_MIME_VALUE,
                              contentSize,
                              contentStream);

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
        List<String> spaceContents = s3Provider.getSpaceContents(SPACE_ID);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(CONTENT_ID));
        assertFalse(spaceContents.contains(accessKeyId + "." + SPACE_ID
                + "-space-metadata"));

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
        Properties contentMetadata = new Properties();
        contentMetadata.put(CONTENT_META_NAME, CONTENT_META_VALUE);
        s3Provider.setContentMetadata(SPACE_ID, CONTENT_ID, contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Properties cMetadata =
                s3Provider.getContentMetadata(SPACE_ID, CONTENT_ID);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_META_VALUE, cMetadata.get(CONTENT_META_NAME));
        assertEquals(CONTENT_MIME_VALUE, cMetadata.get(CONTENT_MIME_NAME));
        assertEquals(CONTENT_MIME_VALUE, cMetadata.get("Content-Type"));

        // test deleteContent()
        log.debug("Test deleteContent()");
        s3Provider.deleteContent(SPACE_ID, CONTENT_ID);
        spaceContents = s3Provider.getSpaceContents(SPACE_ID);
        assertFalse(spaceContents.contains(CONTENT_ID));

        // test deleteSpace()
        log.debug("Test deleteSpace()");
        s3Provider.deleteSpace(SPACE_ID);
        spaces = s3Provider.getSpaces();
        assertFalse(spaces.contains(SPACE_ID));
    }

}
