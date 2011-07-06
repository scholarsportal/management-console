/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup;

import org.duracloud.account.db.backup.util.FileSystemUtil;
import org.duracloud.account.db.backup.util.StoreUtil;
import org.duracloud.account.db.util.DbUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;

/**
 * This class is responsible for backing up the database tables.
 * If the current tables differ from the last archived tables, then a backup
 * is performed.
 *
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class DbBackup {

    private Logger log = LoggerFactory.getLogger(DbBackup.class);

    // back-up zip filenames should be of the format: YYYY-MM-DD.zip
    private static final String FILENAME_FORMAT = "^\\d{4}-\\d{2}-\\d{2}.zip$";

    private DbUtil dbUtil;
    private StoreUtil storeUtil;
    private FileSystemUtil fileSystemUtil;

    private File workDir;
    private File previousDir;

    public DbBackup(DbUtil dbUtil,
                    StoreUtil storeUtil,
                    FileSystemUtil fileSystemUtil,
                    File workDir,
                    File previousDir) {
        this.dbUtil = dbUtil;
        this.storeUtil = storeUtil;
        this.fileSystemUtil = fileSystemUtil;
        this.workDir = workDir;
        this.previousDir = previousDir;

        fileSystemUtil.createIfNecessary(workDir);
        fileSystemUtil.createIfNecessary(previousDir);
    }

    /**
     * This method does the backup if necessary.
     * It downloads both the current and last archived database tables and
     * compares them for equality base on their names and size.
     * If they differ, then a backup is performed.
     *
     * @return true if a backup is performed
     */
    public boolean backup() {
        downloadCurrentTables();
        downloadPreviousBackup();

        boolean needsBackup = !fileSystemUtil.directoriesAreEqual(workDir,
                                                                  previousDir);
        if (needsBackup) {
            log.info("DB has changed. Performing backup.");
            doBackup();

        } else {
            log.info("DB has not changed. No backup required.");
        }

        return needsBackup;
    }

    private void downloadCurrentTables() {
        // tables are downloaded to the directory: workDir
        dbUtil.runCommand(DbUtil.COMMAND.GET);
        fileSystemUtil.verifyNotEmpty(workDir);
    }

    private void downloadPreviousBackup() {
        String contentId = storeUtil.getMostRecentContentId(FILENAME_FORMAT);
        if (null == contentId) {
            log.warn("No previous back-ups exist.");
            return;
        }

        storeUtil.downloadContentToDirectory(contentId, previousDir);

        File backup = new File(previousDir, contentId);
        fileSystemUtil.verifyExists(backup);
        fileSystemUtil.unZipInPlace(backup);
        backup.delete();
    }

    private void doBackup() {
        File zip = fileSystemUtil.zip(workDir);
        String contentId = buildContentId();
        storeUtil.uploadFile(contentId, zip);
    }

    private String buildContentId() {
        Calendar calendar = Calendar.getInstance();
        String contentId = String.format("%1$tY-%1$tm-%1$td.zip", calendar);

        // sanity check
        if (!contentId.matches(FILENAME_FORMAT)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error, contentId does not match file pattern: ");
            sb.append(contentId);
            sb.append(" != ");
            sb.append(FILENAME_FORMAT);
            log.error(sb.toString());
            throw new DuraCloudRuntimeException(sb.toString());
        }

        return contentId;
    }

}
