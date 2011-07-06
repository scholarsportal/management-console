/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util;

import java.io.File;

/**
 * This interface defines the contract for a utility that manages uploading and
 * downloading content to an abstract store.
 * There are currently both DuraStore and S3 implementations.
 *
 * @author Andrew Woods
 *         Date: 7/6/11
 */
public interface StoreUtil {

    /**
     * This method returns the lexicographically greater contentId that matches
     * the arg regex pattern.
     *
     * @param pattern filter for contentId selection
     * @return contentId
     */
    public String getMostRecentContentId(String pattern);

    /**
     * This method downloads the content item having the arg contentId to the
     * arg directory.
     *
     * @param contentId of item to download
     * @param dir       to which content is downloaded
     */
    public void downloadContentToDirectory(String contentId, File dir);

    /**
     * This method uploads the arg file labeling it with the arg contentId.
     *
     * @param contentId label for uploaded content
     * @param file      to upload
     */
    public void uploadFile(String contentId, File file);
}
