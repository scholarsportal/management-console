/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.backup.util.StoreUtil;
import org.duracloud.client.ContentStore;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class StoreUtilSpaceImpl implements StoreUtil {

    private final Logger log = LoggerFactory.getLogger(StoreUtilSpaceImpl.class);

    private ContentStore contentStore;
    private String spaceId;

    public StoreUtilSpaceImpl(ContentStore contentStore, String spaceId) {
        this.contentStore = contentStore;
        this.spaceId = spaceId;
    }

    @Override
    public String getMostRecentContentId(String pattern) {
        String contentId = null;

        Iterator<String> contents = null;
        try {
            contents = contentStore.getSpaceContents(spaceId);

        } catch (ContentStoreException e) {
            log.error("Error getting space contents for: {}", spaceId);
            throw new DuraCloudRuntimeException(e);
        }

        while (contents.hasNext()) {
            String id = contents.next();
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
        Content content = null;
        try {
            content = contentStore.getContent(spaceId, contentId);

        } catch (ContentStoreException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error downloading: " + spaceId + " / " + contentId);
            sb.append(" msg: " + e.getMessage());
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString(), e);
        }

        if (null == content) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error downloading: " + spaceId + " / " + contentId);
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }

        InputStream inputStream = content.getStream();
        if (null == inputStream) {
            StringBuilder sb = new StringBuilder();
            sb.append("Null content stream: " + spaceId + " / " + contentId);
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }
        return inputStream;
    }

    @Override
    public void uploadFile(String contentId, File file) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error opening file: " + file.getAbsolutePath());
            sb.append(" msg: " + e.getMessage());
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(e);
        }

        try {
            contentStore.addContent(spaceId,
                                    contentId,
                                    stream,
                                    file.length(),
                                    null,
                                    null,
                                    null);

        } catch (ContentStoreException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error adding: " + spaceId + " / " + contentId);
            sb.append(" msg: " + e.getMessage());
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString(), e);
        }
    }
}
