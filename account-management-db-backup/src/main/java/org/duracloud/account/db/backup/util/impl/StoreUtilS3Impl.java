/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.backup.util.StoreUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: 7/6/11
 */
public class StoreUtilS3Impl implements StoreUtil {

    private final Logger log = LoggerFactory.getLogger(StoreUtilS3Impl.class);

    private AmazonS3 s3Client;
    private String bucketId;

    public StoreUtilS3Impl(String accessKey,
                           String secretKey,
                           String bucketId) {
        this(new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)),
             bucketId);
    }

    public StoreUtilS3Impl(AmazonS3 s3Client, String bucketId) {
        this.s3Client = s3Client;
        this.bucketId = bucketId;

        createBucketIfNecessary();
    }

    private void createBucketIfNecessary() {
        if (!s3Client.doesBucketExist(bucketId)) {
            s3Client.createBucket(bucketId);
        }
    }

    @Override
    public String getMostRecentContentId(String pattern) {
        String contentId = null;

        ObjectListing listing = s3Client.listObjects(bucketId);
        List<S3ObjectSummary> objects = listing.getObjectSummaries();
        for (S3ObjectSummary object : objects) {
            String id = object.getKey();
            if (id.matches(pattern)) {

                if (null == contentId) {
                    contentId = id;

                } else if (contentId.compareTo(id) < 0) {
                    contentId = id;
                }
            }
        }
        return contentId;
    }

    @Override
    public void downloadContentToDirectory(String contentId, File dir) {
        InputStream inputStream = getContentStream(contentId);

        File destFile = new File(dir, contentId);
        OutputStream outputStream = null;
        try {
            outputStream = FileUtils.openOutputStream(destFile);

        } catch (IOException e) {
            log.error("Error opening: {}, {}", destFile, e.getMessage());
            throw new DuraCloudRuntimeException(e);
        }

        try {
            IOUtils.copy(inputStream, outputStream);

        } catch (IOException e) {
            log.error("Error copying to: {}, {}", destFile, e.getMessage());
            throw new DuraCloudRuntimeException(e);

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private InputStream getContentStream(String contentId) {
        S3Object object = s3Client.getObject(bucketId, contentId);
        if (null == object) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error downloading: " + bucketId + " / " + contentId);
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }

        InputStream inputStream = object.getObjectContent();
        if (null == inputStream) {
            StringBuilder sb = new StringBuilder();
            sb.append("Null content stream: " + bucketId + " / " + contentId);
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }
        return inputStream;
    }

    @Override
    public void uploadFile(String contentId, File file) {
        s3Client.putObject(bucketId, contentId, file);
    }
}
