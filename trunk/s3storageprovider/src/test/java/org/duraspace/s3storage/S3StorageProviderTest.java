
package org.duraspace.s3storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

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
        String spaceId = "duraspace-test-bucket";
        String contentId = "duraspace-test-content";
        String spaceMetadataName = StorageProvider.METADATA_SPACE_NAME;
        String spaceMetadataValue = "Testing Space";
        String contentMetadataName = StorageProvider.METADATA_CONTENT_NAME;
        String contentMetadataValue = "Testing Content";
        String contentData = "Test Content";

        // test createSpace()
        log.debug("Test createSpace()");
        s3Provider.createSpace(spaceId);

        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Properties spaceMetadata = new Properties();
        spaceMetadata.put(spaceMetadataName, spaceMetadataValue);
        s3Provider.setSpaceMetadata(spaceId, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Properties sMetadata = s3Provider.getSpaceMetadata(spaceId);
        assertTrue(sMetadata.containsKey(spaceMetadataName));
        assertEquals(spaceMetadataValue, sMetadata.get(spaceMetadataName));

        // test getSpaces()
        log.debug("Test getSpaces()");
        List<String> spaces = s3Provider.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.contains(spaceId)); // This will only work when spaceId fits
        // the S3 bucket naming conventions

        // Check space access
        log.debug("Check space access");
        String bucketName =
                ((S3StorageProvider) s3Provider).getBucketName(spaceId);
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
        byte[] content = contentData.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        s3Provider.addContent(spaceId,
                              contentId,
                              "text/plain",
                              contentSize,
                              contentStream);

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

        // test getSpaceContents()
        log.debug("Test getSpaceContents()");
        List<String> spaceContents = s3Provider.getSpaceContents(spaceId);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(contentId));
        assertFalse(spaceContents.contains(accessKeyId + "." + spaceId
                + "-space-metadata"));

        // test getContent()
        log.debug("Test getContent()");
        InputStream is = s3Provider.getContent(spaceId, contentId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(contentData));

        // test invalid content
        log.debug("Test getContent() with invalid content ID");
        log.debug("-- Begin expected error log -- ");
        is = s3Provider.getContent(spaceId, "non-existant-content");
        assertTrue(is == null);
        log.debug("-- End expected error log --");

        // test setContentMetadata()
        log.debug("Test setContentMetadata()");
        Properties contentMetadata = new Properties();
        contentMetadata.put(contentMetadataName, contentMetadataValue);
        s3Provider.setContentMetadata(spaceId, contentId, contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Properties cMetadata =
                s3Provider.getContentMetadata(spaceId, contentId);
        assertNotNull(cMetadata);
        assertEquals(contentMetadataValue, cMetadata.get(contentMetadataName));
        assertEquals("text/plain", cMetadata.get("Content-Type"));

        // test deleteContent()
        log.debug("Test deleteContent()");
        s3Provider.deleteContent(spaceId, contentId);
        spaceContents = s3Provider.getSpaceContents(spaceId);
        assertFalse(spaceContents.contains(contentId));

        // test deleteSpace()
        log.debug("Test deleteSpace()");
        s3Provider.deleteSpace(spaceId);
        spaces = s3Provider.getSpaces();
        assertFalse(spaces.contains(spaceId));
    }

}
