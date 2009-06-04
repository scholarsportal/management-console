
package org.duraspace.sunstorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import static org.duraspace.storage.util.StorageProviderUtil.compareChecksum;
import static org.duraspace.storage.util.StorageProviderUtil.contains;

/**
 * Tests the Sun Storage Provider. This test uses a local
 * database to retrieve the necessary Sun Cloud credentials.
 *
 * @author Bill Branan
 */
public class SunStorageProviderTest {

    protected static final Logger log =
            Logger.getLogger(SunStorageProviderTest.class);

    private SunStorageProvider sunProvider;

    private static String SPACE_ID = null;
    private static final String CONTENT_ID = "duracloud-test-content";
    private static final String SPACE_META_NAME = StorageProvider.METADATA_SPACE_NAME;
    private static final String SPACE_META_VALUE = "Testing Space";
    private static final String CONTENT_META_NAME = StorageProvider.METADATA_CONTENT_NAME;
    private static final String CONTENT_META_VALUE = "Testing Content";
    private static final String CONTENT_MIME_NAME = StorageProvider.METADATA_CONTENT_MIMETYPE;
    private static final String CONTENT_MIME_VALUE = "text/plain";
    private static final String CONTENT_SUBJECT_NAME = "content-subject";
    private static final String CONTENT_SUBJECT_VALUE = "Storage System Testing";
    private static final String CONTENT_DATA = "Test Content";

    @Before
    public void setUp() throws Exception {
        Credential sunCredential = getCredential();
        Assert.assertNotNull(sunCredential);

        String username = sunCredential.getUsername();
        String password = sunCredential.getPassword();
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);

        sunProvider = new SunStorageProvider(username, password);

        String random = String.valueOf(new Random().nextInt(99999));
        SPACE_ID = "duracloud-test-bucket." + random;

        // test createSpace()
        log.debug("Test createSpace()");
        sunProvider.createSpace(SPACE_ID);
    }

    @After
    public void tearDown() throws Exception {
        // test deleteSpace()
        log.debug("Test deleteSpace()");
        sunProvider.deleteSpace(SPACE_ID);
        Iterator<String> spaces = sunProvider.getSpaces();
        assertFalse(contains(spaces, SPACE_ID));

        sunProvider = null;
    }

    private Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        return dbUtil.findCredentialForProvider(StorageProviderType.SUN);
    }

    @Test
    public void testSpaceMetadata() throws Exception {
        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Map<String, String> spaceMetadata = new HashMap<String, String>();
        spaceMetadata.put(SPACE_META_NAME, SPACE_META_VALUE);
        sunProvider.setSpaceMetadata(SPACE_ID, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Map<String, String> sMetadata = sunProvider.getSpaceMetadata(SPACE_ID);
        assertTrue(sMetadata.containsKey(SPACE_META_NAME));
        assertEquals(SPACE_META_VALUE, sMetadata.get(SPACE_META_NAME));
    }

    @Test
    public void testGetSpaces() throws Exception {
        // test getSpaces()
        log.debug("Test getSpaces()");
        Iterator<String> spaces = sunProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, SPACE_ID)); // This will only work when SPACE_ID fits
                                               // the S3 bucket naming conventions
    }

    @Test
    public void testSpaceAccess() throws Exception {
        // Check space access
        log.debug("Check space access");
        String bucketName = sunProvider.getBucketName(SPACE_ID);
        String spaceUrl = "http://" + bucketName + ".object.storage.network.com";
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse spaceResponse = restHelper.get(spaceUrl);
        // Currently getting a 503 (service unavailable) error, which is odd, the
        // response should really be either 401 (unauthorized) or 403 (forbidden)
        assertEquals(503, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(OPEN)");
        try {
            sunProvider.setSpaceAccess(SPACE_ID, AccessType.OPEN);
            fail("The Sun Cloud service does not support open access.");
        } catch(RuntimeException re) {
            assertNotNull(re);
        }

        // test getSpaceAccess()
        log.debug("Test getSpaceAccess()");
        AccessType access = sunProvider.getSpaceAccess(SPACE_ID);
        assertEquals(access, AccessType.CLOSED);
    }

    @Test
    public void testContent() throws Exception {
        // test addContent()
        log.debug("Test addContent()");
        byte[] content = CONTENT_DATA.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        String checksum = sunProvider.addContent(SPACE_ID,
                                                 CONTENT_ID,
                                                 CONTENT_MIME_VALUE,
                                                 contentSize,
                                                 contentStream);
        compareChecksum(sunProvider, SPACE_ID, CONTENT_ID, checksum);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Map<String, String> cMetadata =
                sunProvider.getContentMetadata(SPACE_ID, CONTENT_ID);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_ID, cMetadata.get(CONTENT_META_NAME));
        assertEquals(CONTENT_MIME_VALUE, cMetadata.get(CONTENT_MIME_NAME));

        // TODO: Test access control when it becomes available

        // test getSpaceContents()
        log.debug("Test getSpaceContents()");
        Iterator<String> spaceContents = sunProvider.getSpaceContents(SPACE_ID);
        assertNotNull(spaceContents);
        assertTrue(contains(spaceContents, CONTENT_ID));

        // Ensure that space metadata is not included in contents list
        //String spaceMetaSuffix = RackspaceStorageProvider.SPACE_METADATA_SUFFIX;
        spaceContents = sunProvider.getSpaceContents(SPACE_ID);
        String bucketName = sunProvider.getBucketName(SPACE_ID);
        String spaceMetaSuffix = SunStorageProvider.SPACE_METADATA_SUFFIX;
        assertFalse(contains(spaceContents, bucketName + spaceMetaSuffix));

        // test getContent()
        log.debug("Test getContent()");
        InputStream is = sunProvider.getContent(SPACE_ID, CONTENT_ID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        is.close();
        assertTrue(contentLine.equals(CONTENT_DATA));

        // test invalid content
        log.debug("Test getContent() with invalid content ID");
        log.debug("-- Begin expected error log -- ");
        is = sunProvider.getContent(SPACE_ID, "non-existant-content");
        assertTrue(is == null);
        log.debug("-- End expected error log --");

        // test setContentMetadata()
        log.debug("Test setContentMetadata()");
        Map<String, String> contentMetadata = new HashMap<String, String>();
        contentMetadata.put(CONTENT_META_NAME, CONTENT_META_VALUE);
        contentMetadata.put(CONTENT_SUBJECT_NAME, CONTENT_SUBJECT_VALUE);
        sunProvider.setContentMetadata(SPACE_ID, CONTENT_ID, contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        cMetadata = sunProvider.getContentMetadata(SPACE_ID, CONTENT_ID);
        assertNotNull(cMetadata);
        assertEquals(CONTENT_META_VALUE, cMetadata.get(CONTENT_META_NAME));
        assertEquals(CONTENT_SUBJECT_VALUE, cMetadata.get(CONTENT_SUBJECT_NAME));
        // Mime type was not included when setting content metadata
        // so it should have been reset to a default value
        assertEquals(StorageProvider.DEFAULT_MIMETYPE, cMetadata.get(CONTENT_MIME_NAME));

        // test deleteContent()
        log.debug("Test deleteContent()");
        sunProvider.deleteContent(SPACE_ID, CONTENT_ID);
        spaceContents = sunProvider.getSpaceContents(SPACE_ID);
        assertFalse(contains(spaceContents, CONTENT_ID));
    }

}
