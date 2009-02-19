package org.duraspace.s3storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProvider.AccessType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests the S3 Storage Provider. This test is run via the
 * command line in order to allow passing in S3 credentials.
 *
 * @author Bill Branan
 */
public class S3StorageProviderTestDriver {

    StorageProvider s3Provider;

    private static String accessKeyId;

    public S3StorageProviderTestDriver(S3StorageProvider provider) {
        s3Provider = provider;
    }

    @Test
    public void testS3StorageProvider() throws Exception {
        String spaceId = "duraspace-test-bucket";
        String contentId = "duraspace-test-content";
        String spaceMetadataName = "space-name";
        String spaceMetadataValue = "Testing Space";
        String contentMetadataName = "content-name";
        String contentMetadataValue = "Testing Content";
        String contentData = "Test Content";

        // test createSpace()
        System.out.println("Test createSpace()");
        s3Provider.createSpace(spaceId);

        // test setSpaceMetadata()
        System.out.println("Test setSpaceMetadata()");
        Properties spaceMetadata = new Properties();
        spaceMetadata.put(spaceMetadataName, spaceMetadataValue);
        s3Provider.setSpaceMetadata(spaceId, spaceMetadata);

        // test getSpaceMetadata()
        System.out.println("Test getSpaceMetadata()");
        Properties sMetadata = s3Provider.getSpaceMetadata(spaceId);
        assertTrue(sMetadata.containsKey(spaceMetadataName));
        assertEquals(spaceMetadataValue, sMetadata.get(spaceMetadataName));

        // test getSpaces()
        System.out.println("Test getSpaces()");
        List<String> spaces = s3Provider.getSpaces();
        assertNotNull(spaces);
        assertTrue(spaces.contains(spaceId)); // This will only work when spaceId fits
                                              // the S3 bucket naming conventions

        // Check space access
        System.out.println("Check space access");
        String bucketName = ((S3StorageProvider)s3Provider).getBucketName(spaceId);
        String spaceUrl = "http://" + bucketName + ".s3.amazonaws.com";
        RestHttpHelper restHelper = new RestHttpHelper();
        HttpResponse spaceResponse = restHelper.get(spaceUrl);
        // Expect a 403 forbidden error because the Space Access is closed by default
        assertEquals(403, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        System.out.println("Test setSpaceAccess(OPEN)");
        s3Provider.setSpaceAccess(spaceId, AccessType.OPEN);

        // test getSpaceAccess()
        System.out.println("Test getSpaceAccess()");
        AccessType access = s3Provider.getSpaceAccess(spaceId);
        assertEquals(access, AccessType.OPEN);

        // Check space access
        System.out.println("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(200, spaceResponse.getStatusCode());

        // test addContent()
        System.out.println("Test addContent()");
        byte[] content = contentData.getBytes();
        int contentSize = content.length;
        ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
        s3Provider.addContent(spaceId,
                              contentId,
                              "text/plain",
                              contentSize,
                              contentStream);

        // Check content access
        System.out.println("Check content access");
        spaceResponse = restHelper.get(spaceUrl+"/"+contentId);
        assertEquals(200, spaceResponse.getStatusCode());

        // test setSpaceAccess()
        System.out.println("Test setSpaceAccess(CLOSED)");
        s3Provider.setSpaceAccess(spaceId, AccessType.CLOSED);

        // Check space access
        System.out.println("Check space access");
        spaceResponse = restHelper.get(spaceUrl);
        assertEquals(403, spaceResponse.getStatusCode());

        // Check content access
        System.out.println("Check content access");
        spaceResponse = restHelper.get(spaceUrl+"/"+contentId);
        assertEquals(403, spaceResponse.getStatusCode());

        // test getSpaceContents()
        System.out.println("Test getSpaceContents()");
        List<String> spaceContents = s3Provider.getSpaceContents(spaceId);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(contentId));
        assertFalse(spaceContents.contains(accessKeyId+"."+spaceId+"-space-metadata"));

        // test getContent()
        System.out.println("Test getContent()");
        InputStream is = s3Provider.getContent(spaceId, contentId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String contentLine = reader.readLine();
        assertTrue(contentLine.equals(contentData));

        // test setContentMetadata()
        System.out.println("Test setContentMetadata()");
        Properties contentMetadata = new Properties();
        contentMetadata.put(contentMetadataName, contentMetadataValue);
        s3Provider.setContentMetadata(spaceId, contentId, contentMetadata);

        // test getContentMetadata()
        System.out.println("Test getContentMetadata()");
        Properties cMetadata = s3Provider.getContentMetadata(spaceId, contentId);
        assertNotNull(cMetadata);
        assertEquals(contentMetadataValue, cMetadata.get(contentMetadataName));
        assertEquals("text/plain", cMetadata.get("Content-Type"));

        // test deleteContent()
        System.out.println("Test deleteContent()");
        s3Provider.deleteContent(spaceId, contentId);
        spaceContents = s3Provider.getSpaceContents(spaceId);
        assertFalse(spaceContents.contains(contentId));

        // test deleteSpace()
        System.out.println("Test deleteSpace()");
        s3Provider.deleteSpace(spaceId);
        spaces = s3Provider.getSpaces();
        assertFalse(spaces.contains(spaceId));
    }

    private static void usage(String message) {
        System.err.println(message);
        System.err.println("Usage: java S3StorageProviderTestDriver " +
        		           "accessKeyId secretAccessKey\n");
        System.err.println("\taccessKeyId - The Access Key for your Amazon S3 account");
        System.err.println("\tsecretAccessKey - The Secret Key for your Amazon S3 account");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage("Two arguments required");
        }

        accessKeyId = args[0];
        S3StorageProvider provider =
            new S3StorageProvider(args[0], args[1]);
        S3StorageProviderTestDriver testDriver =
            new S3StorageProviderTestDriver(provider);

        testDriver.testS3StorageProvider();
    }

}
