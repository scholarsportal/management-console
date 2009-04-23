package org.duraspace.rackspacestorage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

import com.mosso.client.cloudfiles.FilesCDNContainer;
import com.mosso.client.cloudfiles.FilesClient;

import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.provider.StorageProvider;
import org.duraspace.storage.provider.StorageProvider.AccessType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests the Rackspace Storage Provider. This test is run via the
 * command line in order to allow passing in credentials.
 *
 * @author Bill Branan
 */
public class RackspaceStorageProviderTestDriver {

    RackspaceStorageProvider rackspaceProvider;
    FilesClient filesClient;

    public RackspaceStorageProviderTestDriver(RackspaceStorageProvider provider,
                                              FilesClient filesClient)
    throws Exception {
        this.rackspaceProvider = provider;
        this.filesClient = filesClient;
        assertTrue(filesClient.login());
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
        System.out.println("Test createSpace()");
        rackspaceProvider.createSpace(spaceId);

        // test setSpaceMetadata()
        System.out.println("Test setSpaceMetadata()");
        Properties spaceMetadata = new Properties();
        spaceMetadata.put(spaceMetadataName, spaceMetadataValue);
        rackspaceProvider.setSpaceMetadata(spaceId, spaceMetadata);

        // test getSpaceMetadata()
        System.out.println("Test getSpaceMetadata()");
        Properties sMetadata = rackspaceProvider.getSpaceMetadata(spaceId);
        assertTrue(sMetadata.containsKey(spaceMetadataName));
        assertEquals(spaceMetadataValue, sMetadata.get(spaceMetadataName));

        // test getSpaces()
        System.out.println("Test getSpaces()");
        List<String> spaces = rackspaceProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.contains(spaceId)); // This will only work when spaceId fits
                                              // the Rackspace container naming conventions

        // Check space access - should be closed
        // TODO: Uncomment after Rackspace SDK bug is fixed
//        System.out.println("Check space access");
//        FilesCDNContainer cdnContainer = filesClient.getCDNContainerInfo(spaceId);
//        assertFalse(cdnContainer.isEnabled());

        // test setSpaceAccess()
        System.out.println("Test setSpaceAccess(OPEN)");
        rackspaceProvider.setSpaceAccess(spaceId, AccessType.OPEN);

        // test getSpaceAccess()
        System.out.println("Test getSpaceAccess()");
        AccessType access = rackspaceProvider.getSpaceAccess(spaceId);
        assertEquals(access, AccessType.OPEN);

        // Check space access - should be open
        System.out.println("Check space access");
        FilesCDNContainer cdnContainer = filesClient.getCDNContainerInfo(spaceId);
        assertTrue(cdnContainer.isEnabled());

        // test addContent()
        System.out.println("Test addContent()");
        byte[] content = contentData.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        rackspaceProvider.addContent(spaceId,
                                     contentId,
                                     "text/plain",
                                     contentSize,
                                     contentStream);

        // Check content access
        System.out.println("Check content access");
        String spaceUrl = cdnContainer.getCdnURL();
        String contentUrl = spaceUrl+"/"+contentId;
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse httpResponse = restHelper.get(contentUrl);
        assertEquals(200, httpResponse.getStatusCode());

        // test setSpaceAccess()
        System.out.println("Test setSpaceAccess(CLOSED)");
        rackspaceProvider.setSpaceAccess(spaceId, AccessType.CLOSED);
        // Note that content stays in the Rackspace CDN for 24 hours
        // after it is made available, even if the container has been
        // set to private access or the content has been deleted.

        // test getSpaceContents()
        System.out.println("Test getSpaceContents()");
        List<String> spaceContents = rackspaceProvider.getSpaceContents(spaceId);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(contentId));
        String containerName = rackspaceProvider.getContainerName(spaceId);
        String spaceMetaSuffix = RackspaceStorageProvider.SPACE_METADATA_SUFFIX;
        assertFalse(spaceContents.contains(containerName+spaceMetaSuffix));

        // test getContent()
        System.out.println("Test getContent()");
        InputStream is = rackspaceProvider.getContent(spaceId, contentId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(contentData));

        // test invalid content
        System.out.println("Test getContent() with invalid content ID");
        is = rackspaceProvider.getContent(spaceId, "non-existant-content");
        assertTrue(is == null);

        // test setContentMetadata()
        System.out.println("Test setContentMetadata()");
        Properties contentMetadata = new Properties();
        contentMetadata.put(contentMetadataName, contentMetadataValue);
        rackspaceProvider.setContentMetadata(spaceId, contentId, contentMetadata);

        // test getContentMetadata()
        System.out.println("Test getContentMetadata()");
        Properties cMetadata = rackspaceProvider.getContentMetadata(spaceId, contentId);
        assertNotNull(cMetadata);
        // TODO: Determine how to handle metadata name case change (content-name becomes Content-Name)
//        assertEquals(contentMetadataValue, cMetadata.get(contentMetadataName));
        assertEquals("text/plain",
                     cMetadata.get(StorageProvider.METADATA_CONTENT_MIMETYPE));

        // test deleteContent()
        System.out.println("Test deleteContent()");
        rackspaceProvider.deleteContent(spaceId, contentId);
        spaceContents = rackspaceProvider.getSpaceContents(spaceId);
        assertFalse(spaceContents.contains(contentId));

        // test deleteSpace()
        System.out.println("Test deleteSpace()");
        rackspaceProvider.deleteSpace(spaceId);
        spaces = rackspaceProvider.getSpaces();
        assertFalse(spaces.contains(spaceId));
    }

    private static void usage(String message) {
        System.err.println(message);
        System.err.println("Usage: java RackspaceStorageProviderTestDriver " +
        		           "username apiAccessKey\n");
        System.err.println("\tusername - The Username for your Rackspace Cloud Files account");
        System.err.println("\tapiAccessKey - The API Access Key for your Rackspace Cloud Files account");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage("Two arguments required");
        }

        String username = args[0];
        String apiAccessKey = args[1];

        FilesClient filesClient =
            new FilesClient(username, apiAccessKey);
        RackspaceStorageProvider provider =
            new RackspaceStorageProvider(username, apiAccessKey);
        RackspaceStorageProviderTestDriver testDriver =
            new RackspaceStorageProviderTestDriver(provider, filesClient);

        testDriver.testRackspaceStorageProvider();
        System.out.println("Tests passed successfully");
    }

}
