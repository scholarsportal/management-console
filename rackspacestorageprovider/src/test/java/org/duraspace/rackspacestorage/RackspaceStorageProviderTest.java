
package org.duraspace.rackspacestorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

import com.mosso.client.cloudfiles.FilesCDNContainer;
import com.mosso.client.cloudfiles.FilesClient;

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
 * Tests the Rackspace Storage Provider. This test is run via the command line
 * in order to allow passing in credentials.
 *
 * @author Bill Branan
 */
public class RackspaceStorageProviderTest {

    protected static final Logger log =
            Logger.getLogger(RackspaceStorageProviderTest.class);

    RackspaceStorageProvider rackspaceProvider;

    FilesClient filesClient;

    @Before
    public void setUp() throws Exception {
        Credential rackspaceCredential = getCredential();
        Assert.assertNotNull(rackspaceCredential);

        String username = rackspaceCredential.getUsername();
        String password = rackspaceCredential.getPassword();
        Assert.assertNotNull(username);
        Assert.assertNotNull(password);

        rackspaceProvider = new RackspaceStorageProvider(username, password);
        filesClient = new FilesClient(username, password);
        assertTrue(filesClient.login());
    }

    @After
    public void tearDown() {
        rackspaceProvider = null;
        filesClient = null;
    }

    private Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        return dbUtil.findCredentialForProvider(StorageProviderType.RACKSPACE);
    }

    @Test
    public void testRackspaceStorageProvider() throws Exception {
        String spaceId = "duraspace-test-bucket";
        String contentId = "duraspace-test-content";
        String spaceMetadataName = StorageProvider.METADATA_SPACE_NAME;
        String spaceMetadataValue = "Testing Space";
        String contentMetadataName = StorageProvider.METADATA_CONTENT_NAME;
        String contentMetadataValue = "Testing Content";
        String contentData = "Test Content";

        // test createSpace()
        log.debug("Test createSpace()");
        rackspaceProvider.createSpace(spaceId);

        // test setSpaceMetadata()
        log.debug("Test setSpaceMetadata()");
        Properties spaceMetadata = new Properties();
        spaceMetadata.put(spaceMetadataName, spaceMetadataValue);
        rackspaceProvider.setSpaceMetadata(spaceId, spaceMetadata);

        // test getSpaceMetadata()
        log.debug("Test getSpaceMetadata()");
        Properties sMetadata = rackspaceProvider.getSpaceMetadata(spaceId);
        assertTrue(sMetadata.containsKey(spaceMetadataName));
        assertEquals(spaceMetadataValue, sMetadata.get(spaceMetadataName));

        // test getSpaces()
        log.debug("Test getSpaces()");
        List<String> spaces = rackspaceProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.contains(spaceId)); // This will only work when spaceId fits
        // the Rackspace container naming conventions

        // Check space access - should be closed
        // TODO: Uncomment after Rackspace SDK bug is fixed
        //        log.debug("Check space access");
        //        FilesCDNContainer cdnContainer = filesClient.getCDNContainerInfo(spaceId);
        //        assertFalse(cdnContainer.isEnabled());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(OPEN)");
        rackspaceProvider.setSpaceAccess(spaceId, AccessType.OPEN);

        // test getSpaceAccess()
        log.debug("Test getSpaceAccess()");
        AccessType access = rackspaceProvider.getSpaceAccess(spaceId);
        assertEquals(access, AccessType.OPEN);

        // Check space access - should be open
        log.debug("Check space access");
        FilesCDNContainer cdnContainer =
                filesClient.getCDNContainerInfo(spaceId);
        assertTrue(cdnContainer.isEnabled());

        // test addContent()
        log.debug("Test addContent()");
        byte[] content = contentData.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        rackspaceProvider.addContent(spaceId,
                                     contentId,
                                     "text/plain",
                                     contentSize,
                                     contentStream);

        // Check content access
        log.debug("Check content access");
        String spaceUrl = cdnContainer.getCdnURL();
        String contentUrl = spaceUrl + "/" + contentId;
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse httpResponse = restHelper.get(contentUrl);
        assertEquals(200, httpResponse.getStatusCode());

        // test setSpaceAccess()
        log.debug("Test setSpaceAccess(CLOSED)");
        rackspaceProvider.setSpaceAccess(spaceId, AccessType.CLOSED);
        // Note that content stays in the Rackspace CDN for 24 hours
        // after it is made available, even if the container has been
        // set to private access or the content has been deleted.

        // test getSpaceContents()
        log.debug("Test getSpaceContents()");
        List<String> spaceContents =
                rackspaceProvider.getSpaceContents(spaceId);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(contentId));
        String containerName = rackspaceProvider.getContainerName(spaceId);
        String spaceMetaSuffix = RackspaceStorageProvider.SPACE_METADATA_SUFFIX;
        assertFalse(spaceContents.contains(containerName + spaceMetaSuffix));

        // test getContent()
        log.debug("Test getContent()");
        InputStream is = rackspaceProvider.getContent(spaceId, contentId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(contentData));

        // test invalid content
        log.debug("Test getContent() with invalid content ID");
        is = rackspaceProvider.getContent(spaceId, "non-existant-content");
        assertTrue(is == null);

        // test setContentMetadata()
        log.debug("Test setContentMetadata()");
        Properties contentMetadata = new Properties();
        contentMetadata.put(contentMetadataName, contentMetadataValue);
        rackspaceProvider.setContentMetadata(spaceId,
                                             contentId,
                                             contentMetadata);

        // test getContentMetadata()
        log.debug("Test getContentMetadata()");
        Properties cMetadata =
                rackspaceProvider.getContentMetadata(spaceId, contentId);
        assertNotNull(cMetadata);
        // TODO: Determine how to handle metadata name case change (content-name becomes Content-Name)
        //        assertEquals(contentMetadataValue, cMetadata.get(contentMetadataName));
        assertEquals("text/plain", cMetadata
                .get(StorageProvider.METADATA_CONTENT_MIMETYPE));

        // test deleteContent()
        log.debug("Test deleteContent()");
        rackspaceProvider.deleteContent(spaceId, contentId);
        spaceContents = rackspaceProvider.getSpaceContents(spaceId);
        assertFalse(spaceContents.contains(contentId));

        // test deleteSpace()
        log.debug("Test deleteSpace()");
        rackspaceProvider.deleteSpace(spaceId);
        spaces = rackspaceProvider.getSpaces();
        assertFalse(spaces.contains(spaceId));
    }

}
