/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.BaseRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.init.domain.AmaConfig;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 21, 2010
 */
public class DbUtilTest {

    private static File workDir;
    private BaseRepo mockRepo;
    private DuracloudRepoMgr mockRepoMgr;
    private DbUtil dbUtil;

    @BeforeClass
    public static void init() {
        workDir = new File("target/db-util-test");
        workDir.mkdir();
    }

    @Before
    public void setUp() throws Exception {
        mockRepo = createMockRepo();
        mockRepoMgr = createMockRepoMgr(mockRepo);
        dbUtil = new DbUtil(mockRepoMgr, workDir);
    }

    @After
    public void tearDown() {
        EasyMock.verify(mockRepo);
        EasyMock.verify(mockRepoMgr);
    }

    @AfterClass
    public static void shutdown() {
        FileUtils.deleteQuietly(workDir);
    }

    @Test
    public void testDbGet() {
        dbUtil.runCommand(DbUtil.COMMAND.GET);
        Assert.assertEquals(1, workDir.listFiles().length);
    }

    @Test
    public void testDbPut() {
        Assert.assertEquals(1, workDir.listFiles().length);
        dbUtil.runCommand(DbUtil.COMMAND.PUT);
    }

    @Test
    public void testDbClear() {
        dbUtil.runCommand(DbUtil.COMMAND.CLEAR);
    }

    private BaseRepo createMockRepo() throws Exception {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);

        Set<Integer> ids = new HashSet<Integer>();
        ids.add(0);
        EasyMock.expect(repo.getIds())
            .andReturn(ids)
            .anyTimes();

        DuracloudUser data =
            new DuracloudUser(0, "user", "pass", "First", "Last", "e@mail.com");
        EasyMock.expect(repo.findById(EasyMock.anyInt()))
            .andReturn(data)
            .anyTimes();

        repo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudRepoMgr createMockRepoMgr(BaseRepo mockRepo) {
        DuracloudRepoMgr repoMgr = EasyMock.createMock(DuracloudRepoMgr.class);

        repoMgr.initialize(EasyMock.isA(AmaConfig.class));
        EasyMock.expectLastCall().anyTimes();

        Set<BaseRepo> repos = new HashSet<BaseRepo>();
        repos.add(mockRepo);
        EasyMock.expect(repoMgr.getAllRepos())
            .andReturn(repos)
            .anyTimes();

        EasyMock.expect(repoMgr.getUserRepo())
            .andReturn((DuracloudUserRepo)mockRepo)
            .anyTimes();

        EasyMock.replay(repoMgr);
        return repoMgr;
    }

}
