/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup;

import org.duracloud.account.db.backup.util.FileSystemUtil;
import org.duracloud.account.db.backup.util.StoreUtil;
import org.duracloud.account.db.util.DbUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class DbBackupTest {

    private DbBackup dbBackup;

    private DbUtil dbUtil;
    private StoreUtil storeUtil;
    private FileSystemUtil fileSystemUtil;

    private File currentDir = new File("target", "unit-test-current");
    private File previousDir = new File("target", "unit-test-previous");

    private String contentId = "content-id";

    @Before
    public void setUp() throws Exception {
        dbUtil = EasyMock.createMock("DbUtil", DbUtil.class);
        storeUtil = EasyMock.createMock("StoreUtil", StoreUtil.class);
        fileSystemUtil = EasyMock.createMock("FileSystemUtil",
                                             FileSystemUtil.class);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(dbUtil, storeUtil, fileSystemUtil);
    }

    private void replayMocks() {
        EasyMock.replay(dbUtil, storeUtil, fileSystemUtil);
    }

    @Test
    public void testBackup() throws Exception {
        createMockExpectations();
        replayMocks();

        dbBackup = new DbBackup(dbUtil, storeUtil,
                                fileSystemUtil,
                                currentDir,
                                previousDir);

        dbBackup.backup();
    }

    private void createMockExpectations() {
        dbUtil.runCommand(DbUtil.COMMAND.GET);
        EasyMock.expectLastCall();

        fileSystemUtil.verifyNotEmpty(currentDir);
        EasyMock.expectLastCall();

        fileSystemUtil.createIfNecessary(currentDir);
        EasyMock.expectLastCall();

        fileSystemUtil.createIfNecessary(previousDir);
        EasyMock.expectLastCall();

        EasyMock.expect(storeUtil.getMostRecentContentId(EasyMock.<String>anyObject()))
            .andReturn(contentId);

        storeUtil.downloadContentToDirectory(contentId, previousDir);
        EasyMock.expectLastCall();

        fileSystemUtil.verifyExists(EasyMock.<File>anyObject());
        EasyMock.expectLastCall();

        fileSystemUtil.unZipInPlace(EasyMock.<File>anyObject());
        EasyMock.expectLastCall();

        EasyMock.expect(fileSystemUtil.directoriesAreEqual(currentDir,
                                                           previousDir))
            .andReturn(false);

        File file = new File("a-zip-file");
        EasyMock.expect(fileSystemUtil.zip(EasyMock.<File>anyObject()))
            .andReturn(file);
        storeUtil.uploadFile(EasyMock.<String>notNull(), EasyMock.eq(file));
    }
}
