/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.duracloud.account.app.controller.GroupsForm.Action;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudGroupService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * 
 * @author Daniel Bernstein 
 *   Date: Nov 12, 2011
 * 
 */
public class AccountGroupsControllerTest extends AmaControllerTestBase {

    private static final String TEST_GROUP_NAME = "test";
    private AccountGroupsController accountGroupsController;
    private AccountService accountService;
    private DuracloudUserService userService;
    private DuracloudGroupService groupService;
    private Integer accountId = AmaControllerTestBase.TEST_ACCOUNT_ID;
    private Model model = new ExtendedModelMap();

    @Before
    public void before() throws Exception {
        super.before();

        accountGroupsController = new AccountGroupsController();
        accountManagerService =
            EasyMock.createMock("AccountManagerService",
                                AccountManagerService.class);
        accountService =
            EasyMock.createMock("AccountService", AccountService.class);
        userService =
            EasyMock.createMock("DuracloudUserService",
                                DuracloudUserService.class);
        groupService =
            EasyMock.createMock("DuracloudGroupService",
                                DuracloudGroupService.class);

        accountGroupsController.setAccountManagerService(accountManagerService);
        accountGroupsController.setUserService(userService);
        accountGroupsController.setDuracloudGroupService(groupService);

        setupMocks(accountId);

    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(accountManagerService);
        EasyMock.verify(accountService);
        EasyMock.verify(userService);
        EasyMock.verify(groupService);

    }

    private void setupMocks(int accountId)
        throws DBConcurrentUpdateException,
            AccountNotFoundException,
            DBNotFoundException {
        EasyMock.expect(accountService.getAccountId())
                .andReturn(accountId)
                .anyTimes();

        EasyMock.expect(accountService.getUsers())
                .andReturn(new HashSet<DuracloudUser>(Arrays.asList(new DuracloudUser[] { createUser() })))
                .anyTimes();

        EasyMock.expect(accountManagerService.getAccount(accountId))
                .andReturn(accountService)
                .anyTimes();

        DuracloudUser user = createUser();

        EasyMock.expect(userService.loadDuracloudUserByUsername(user.getUsername()))
                .andReturn(user)
                .anyTimes();

        EasyMock.expect(userService.loadDuracloudUserById(user.getId()))
                .andReturn(user)
                .anyTimes();

    }

    private Set<DuracloudGroup> createGroups() {
        DuracloudGroup group = new DuracloudGroup(DuracloudGroup.PREFIX+TEST_GROUP_NAME);
        group.addUserId(createUser().getId());
        Set<DuracloudGroup> set = new HashSet<DuracloudGroup>();
        set.add(group);
        return set;
    }

    @Test
    public void testGetGroups() throws Exception {
        expectGroupGroups(1);
        replay();

        String view = this.accountGroupsController.getGroups(accountId, model);
        Assert.assertEquals(AccountGroupsController.GROUPS_VIEW_ID, view);
    }

    private void expectGroupGroups(int times) {
        EasyMock.expect(groupService.getGroups())
                .andReturn(createGroups())
                .times(times);
    }

    private void replay() {
        EasyMock.replay(this.accountManagerService,
                        this.accountService,
                        this.userService,
                        this.groupService);
    }

    @Test
    public void testGetGroupsAddGroup() throws Exception {

        String groupName = "group2";
        EasyMock.expect(groupService.createGroup(DuracloudGroup.PREFIX+groupName))
                .andReturn(new DuracloudGroup(DuracloudGroup.PREFIX+groupName))
                .times(1);

        replay();
        GroupsForm form = new GroupsForm();
        form.setAction(Action.ADD);
        form.setGroupName(groupName);

        String view =
            this.accountGroupsController.modifyGroups(accountId, form, model);
        Assert.assertTrue(view.contains(groupName));
    }

    @Test
    public void testGetGroupsRemoveGroups() throws Exception {
        expectGroupGroups(1);
        DuracloudGroup group = createGroups().iterator().next();
        groupService.deleteGroup(group);
        EasyMock.expectLastCall().once();
        EasyMock.expect(groupService.getGroup(group.getName()))
                .andReturn(group)
                .once();
        replay();
        GroupsForm form = new GroupsForm();
        form.setAction(Action.REMOVE);
        form.setGroupNames(new String[] { TEST_GROUP_NAME });

        String view =
            this.accountGroupsController.modifyGroups(accountId, form, model);
        Assert.assertEquals(AccountGroupsController.GROUPS_VIEW_ID, view);
    }

    private Object getModelAttribute(String name) {
        return model.asMap().get(name);
    }

    @Test
    public void testGetGroup() throws Exception {
        expectGroupGroups(1);
        replay();
        String view =
            this.accountGroupsController.getGroup(accountId,
                                                  TEST_GROUP_NAME,
                                                  model);
        Assert.assertEquals(AccountGroupsController.GROUP_VIEW_ID, view);
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_KEY));
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_USERS_KEY));
    }

    @Test
    public void testGetGroupEdit() throws Exception {
        expectGroupGroups(1);

        HttpServletRequest request =
            EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();

        session.removeAttribute(AccountGroupsController.GROUP_USERS_KEY);
        EasyMock.expectLastCall().once();

        EasyMock.expect(session.getAttribute(AccountGroupsController.GROUP_USERS_KEY))
                .andReturn(null)
                .once();

        session.setAttribute(EasyMock.anyObject(String.class),
                             (Collection<DuracloudUser>) EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        EasyMock.replay(request, session);

        replay();
        String view =
            this.accountGroupsController.editGroup(accountId,
                                                   TEST_GROUP_NAME,
                                                   request,
                                                   model);
        Assert.assertEquals(AccountGroupsController.GROUP_EDIT_VIEW_ID, view);
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_KEY));
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_USERS_KEY));
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.AVAILABLE_USERS_KEY));

    }

    @Test
    public void testSaveGroup() throws Exception {
        expectGroupGroups(1);

        HttpServletRequest request =
            EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).once();

        session.removeAttribute(AccountGroupsController.GROUP_USERS_KEY);
        EasyMock.expectLastCall().once();

        EasyMock.expect(session.getAttribute(AccountGroupsController.GROUP_USERS_KEY))
                .andReturn(null)
                .once();

        session.setAttribute(EasyMock.anyObject(String.class),
                             (Collection<DuracloudUser>) EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        this.groupService.updateGroupUsers(EasyMock.anyObject(DuracloudGroup.class),
                                           (Set<DuracloudUser>) EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        EasyMock.replay(request, session);

        replay();

        GroupForm form = new GroupForm();
        form.setAction(GroupForm.Action.SAVE);

        String view =
            this.accountGroupsController.editGroup(accountId,
                                                   TEST_GROUP_NAME,
                                                   form,
                                                   request,
                                                   model);
        Assert.assertTrue(view.contains(TEST_GROUP_NAME));
        Assert.assertFalse(view.endsWith("edit"));

        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_KEY));
        Assert.assertNotNull(getModelAttribute(AccountGroupsController.GROUP_USERS_KEY));

    }

    @Test
    public void testAddRemoveUser() throws Exception {
        expectGroupGroups(2);
        HttpServletRequest request =
            EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();

        List<DuracloudUser> groupUsers =
            new LinkedList<DuracloudUser>(Arrays.asList(new DuracloudUser[] { createUser() }));

        EasyMock.expect(session.getAttribute(AccountGroupsController.GROUP_USERS_KEY))
                .andReturn(groupUsers)
                .once();

        EasyMock.expect(session.getAttribute(AccountGroupsController.GROUP_USERS_KEY))
                .andReturn(groupUsers)
                .once();

        session.setAttribute(EasyMock.anyObject(String.class),
                             (Collection<DuracloudUser>) EasyMock.anyObject());
        EasyMock.expectLastCall().once();

        EasyMock.replay(request, session);

        replay();

        String testUsername = groupUsers.get(0).getUsername();

        GroupForm form = new GroupForm();
        form.setAction(GroupForm.Action.REMOVE);
        form.setGroupUsernames(new String[] { testUsername });
        String view =
            this.accountGroupsController.editGroup(accountId,
                                                   TEST_GROUP_NAME,
                                                   form,
                                                   request,
                                                   model);

        Assert.assertEquals(AccountGroupsController.GROUP_EDIT_VIEW_ID, view);
        Assert.assertEquals(1,
                            getModelAttributeSize(AccountGroupsController.AVAILABLE_USERS_KEY));
        Assert.assertEquals(0,
                            getModelAttributeSize(AccountGroupsController.GROUP_USERS_KEY));

        form = new GroupForm();
        form.setAction(GroupForm.Action.ADD);
        form.setAvailableUsernames(new String[] { testUsername });

        this.accountGroupsController.editGroup(accountId,
                                               TEST_GROUP_NAME,
                                               form,
                                               request,
                                               model);

        Assert.assertEquals(AccountGroupsController.GROUP_EDIT_VIEW_ID, view);
        Assert.assertEquals(0,
                            getModelAttributeSize(AccountGroupsController.AVAILABLE_USERS_KEY));
        Assert.assertEquals(1,
                            getModelAttributeSize(AccountGroupsController.GROUP_USERS_KEY));
    }

    @SuppressWarnings("unchecked")
    private int getModelAttributeSize(String name) {
        return ((Collection<? extends Object>) getModelAttribute(name)).size();
    }

}
