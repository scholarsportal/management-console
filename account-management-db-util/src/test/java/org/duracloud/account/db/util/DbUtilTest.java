/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import org.apache.commons.io.FileUtils;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudUserRepo;
import org.easymock.EasyMock;
import org.junit.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 21, 2010
 */
public class DbUtilTest {

    private static File workDir;
    private JpaRepository mockRepo;
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
//
//    @Test
//    public void testDbPut() {
//        Assert.assertEquals(1, workDir.listFiles().length);
//        dbUtil.runCommand(DbUtil.COMMAND.PUT);
//    }
//
//    @Test
//    public void testDbClear() {
//        dbUtil.runCommand(DbUtil.COMMAND.CLEAR);
//    }
//
    private JpaRepository createMockRepo() throws Exception {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);

        DuracloudUser data = new DuracloudUser();
        data.setId(0L);
        data.setUsername("user");
        data.setPassword("pass");
        data.setFirstName("First");
        data.setLastName("Last");
        data.setEmail("e@mail.com");
        data.setSecurityQuestion("question");
        data.setSecurityAnswer("answer");
        EasyMock.expect(repo.findOne(EasyMock.anyLong()))
            .andReturn(data)
            .anyTimes();
        List<DuracloudUser> users = new ArrayList<DuracloudUser>();
        users.add(data);
        EasyMock.expect(repo.findAll())
                .andReturn(users)
                .anyTimes();

        repo.delete(EasyMock.anyLong());
        EasyMock.expectLastCall().anyTimes();

        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudRepoMgr createMockRepoMgr(JpaRepository mockRepo) {
        DuracloudRepoMgr repoMgr = EasyMock.createMock(DuracloudRepoMgr.class);

        //repoMgr.initialize(EasyMock.isA(AmaConfig.class));
        //EasyMock.expectLastCall().anyTimes();

        Set<JpaRepository> repos = new HashSet<JpaRepository>();
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
