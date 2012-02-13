/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.notification.Emailer;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AmaControllerTestBase {
   protected static final String TEST_USERNAME = "testuser";
    protected static final Integer TEST_ACCOUNT_ID = 1;
    protected static final Integer TEST_INSTANCE_ID = 1;

    protected AccountManagerService accountManagerService;
    protected AccountService accountService;
    protected DuracloudUserService userService;
    protected NotificationMgr notificationMgr;
    protected Emailer emailer;
    protected BindingResult result;
    protected RedirectAttributes redirectAttributes;
    protected IdUtil idUtil;
    protected AuthenticationManager authenticationManager;
    protected List<Object> mocks = new LinkedList<Object>();
    protected Model model;
    @Before
    public void before() throws Exception {
        mocks.clear();
        
        idUtil = EasyMock.createNiceMock("IdUtil", IdUtil.class);
        mocks.add(idUtil);
        
        accountManagerService = EasyMock.createMock("AccountManagerService",
                                                    AccountManagerService.class);
        mocks.add(accountManagerService);

        accountService = EasyMock.createMock("AccountService",
                                             AccountService.class);
        mocks.add(accountService);
        
        userService = EasyMock.createMock("DuracloudUserService",
                                          DuracloudUserService.class);
        mocks.add(userService);
        
        notificationMgr = EasyMock.createMock("NotificationMgr",
                                              NotificationMgr.class);
        mocks.add(notificationMgr);
        
        emailer = EasyMock.createMock("Emailer", Emailer.class);
        mocks.add(emailer);
        
        result = EasyMock.createMock("BindingResult", BindingResult.class);
        mocks.add(result);
        
        redirectAttributes = EasyMock.createMock("RedirectAttributes", RedirectAttributes.class);
        mocks.add(redirectAttributes);        
        
        model = new ExtendedModelMap();
        intializeAuthManager();
    }
    
    @After
    public void after() throws Exception {
        verifyMocks();
    }

    private void verifyMocks() {
        for(Object m : mocks){
            EasyMock.verify(m);
        }
    }
    /*

    protected void setupSimpleAccountManagerService()
        throws AccountNotFoundException {
        EasyMock.expect(this.accountService.retrieveAccountInfo())
            .andReturn(createAccountInfo())
            .times(1);
        EasyMock.expect(this.accountService.retrieveServerDetails())
            .andReturn(createServerDetails())
            .times(1);
    }
    */
    protected void replayMocks() {
        for(Object m : mocks){
            EasyMock.replay(m);
        }
    }

    protected void intializeAuthManager() {
        SecurityContext ctx = new SecurityContextImpl();
        Authentication auth = EasyMock.createMock(Authentication.class);
        mocks.add(auth);

        EasyMock.expect(auth.getName()).andReturn(TEST_USERNAME).anyTimes();
        authenticationManager =
            EasyMock.createNiceMock(AuthenticationManager.class);
        mocks.add(authenticationManager);

        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    /**
     * @return
     */
    protected AccountInfo createAccountInfo() {
        return new AccountInfo(TEST_ACCOUNT_ID, "testdomain", "test", "test",
                               "test", 0, 0, null, null);
    }

    protected ServerDetails createServerDetails() {
        return new ServerDetails(TEST_ACCOUNT_ID, 0, 0, null, null, null);
    }

    protected DuracloudUser createUser() {
        return createUser(TEST_USERNAME);
    }
    
    protected DuracloudUser createUser(String username) {
        DuracloudUser user =
            new DuracloudUser(0, username, "test", "test", "test", "test",
                              "test", "test");
        Set<AccountRights> rights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        rights.add(new AccountRights(1, TEST_ACCOUNT_ID.intValue(), 0, roles));
        user.setAccountRights(rights);
        return user;
    }

    protected void setupGenericAccountAndUserServiceMocks(int accountId)
        throws DBConcurrentUpdateException,
            AccountNotFoundException,
            DBNotFoundException {
        EasyMock.expect(accountService.getAccountId())
                .andReturn(accountId)
                .anyTimes();


        Collection<DuracloudUser> users = new LinkedList<DuracloudUser>();
        users.add(createUser());
        EasyMock.expect(accountService.getUsers())
                .andReturn(new HashSet<DuracloudUser>(users))
                .anyTimes();

        EasyMock.expect(accountManagerService.getAccount(accountId))
                .andReturn(accountService)
                .anyTimes();

        EasyMock.expect(accountService.retrieveAccountInfo())
                .andReturn(createAccountInfo())
                .anyTimes();
        
        EasyMock.expect(accountService.retrieveServerDetails())
        .andReturn(createServerDetails())
        .anyTimes();

        DuracloudUser user = createUser();

        EasyMock.expect(userService.loadDuracloudUserByUsername(user.getUsername()))
                .andReturn(user)
                .anyTimes();

        EasyMock.expect(userService.loadDuracloudUserByIdInternal(user.getId()))
                .andReturn(user)
                .anyTimes();

    }

    protected void setupNoBindingResultErrors() {
        setupHasBindingResultErrors(false);
    }

    protected void setupHasBindingResultErrors(boolean hasErrors) {
        EasyMock.expect(result.hasErrors()).andReturn(hasErrors);
    }

    protected Set<DuracloudUser> createUserSet() {
        Set<DuracloudUser> s = new HashSet<DuracloudUser>();
        s.add(createUser());
        return s;
    }

    protected void addFlashAttribute() {
        EasyMock.expect(redirectAttributes.addFlashAttribute(EasyMock.isA(String.class),
                                                             EasyMock.isA(String.class)))
                .andReturn(redirectAttributes);
    }
}
