/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class StoreUtilS3ImplTest {

    private StoreUtilS3Impl storeUtil;

    private AmazonS3 s3Client;

    private String bucketId = "bucket-id";

    private File testDir = new File("target", "unit-test-storeutil");

    @Before
    public void setUp() throws Exception {
        if (!testDir.exists()) {
            Assert.assertTrue("must exist: " + testDir.getAbsolutePath(),
                              testDir.mkdir());
        }

        s3Client = EasyMock.createMock("AmazonS3", AmazonS3.class);
        EasyMock.expect(s3Client.doesBucketExist(bucketId)).andReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(s3Client);
    }

    private void replayMocks() {
        EasyMock.replay(s3Client);
    }

    @Test
    public void testGetMostRecentContentId() throws Exception {
        String id = createMocksGetMostRecentContentId();
        replayMocks();
        storeUtil = new StoreUtilS3Impl(s3Client, bucketId);

        String pattern = "^\\d{4}-\\d{2}$";
        String contentId = storeUtil.getMostRecentContentId(pattern);

        Assert.assertNotNull(contentId);
        Assert.assertEquals(id, contentId);
    }

    private String createMocksGetMostRecentContentId() {
        String mostRecentId = "1239-56";
        List<S3ObjectSummary> items = new ArrayList<S3ObjectSummary>();
        items.add(newSummary("hello"));
        items.add(newSummary("1234-56"));
        items.add(newSummary("junk"));
        items.add(newSummary(mostRecentId));
        items.add(newSummary("junk"));
        items.add(newSummary("0234-56"));
        items.add(newSummary("goodbye"));

        ObjectListing listing = EasyMock.createMock("ObjectListing",
                                                    ObjectListing.class);

        EasyMock.expect(listing.getObjectSummaries()).andReturn(items);
        EasyMock.replay(listing);

        EasyMock.expect(s3Client.listObjects(bucketId)).andReturn(listing);

        return mostRecentId;
    }

    private S3ObjectSummary newSummary(String contentId) {
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey(contentId);
        return summary;
    }

    @Test
    public void testDownloadContentToDirectory() throws Exception {
        String contentId = "content-id";
        createMocksDownloadContentToDirectory(contentId);
        replayMocks();
        storeUtil = new StoreUtilS3Impl(s3Client, bucketId);

        File dir = new File(testDir, "download");
        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
        }

        File downloadedFile = new File(dir, contentId);
        Assert.assertFalse(downloadedFile.exists());

        storeUtil.downloadContentToDirectory(contentId, dir);
        Assert.assertTrue(downloadedFile.exists());
    }

    private void createMocksDownloadContentToDirectory(String contentId)
        throws ContentStoreException {
        Content content = EasyMock.createMock("Content", Content.class);

        InputStream stream = new AutoCloseInputStream(new ByteArrayInputStream(
            "hello".getBytes()));
        EasyMock.expect(content.getStream()).andReturn(stream);
        EasyMock.replay(content);

        S3Object object = new S3Object();
        object.setObjectContent(stream);

        EasyMock.expect(s3Client.getObject(bucketId, contentId)).andReturn(
            object);
    }

    @Test
    public void testUploadFile() throws Exception {
        String contentId = "content-id";
        File file = new File(testDir, "upload-content.txt");
        FileUtils.writeStringToFile(file, "test content");

        createMocksUploadFile(contentId, file);
        replayMocks();
        storeUtil = new StoreUtilS3Impl(s3Client, bucketId);

        storeUtil.uploadFile(contentId, file);
    }

    private void createMocksUploadFile(String contentId, File file) {
        EasyMock.expect(s3Client.putObject(bucketId, contentId, file))
            .andReturn(null);
    }
}
