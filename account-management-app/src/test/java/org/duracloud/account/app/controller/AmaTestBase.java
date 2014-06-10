package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class AmaTestBase {

    protected static final String TEST_USERNAME = "testuser";
    protected static final Integer TEST_ACCOUNT_ID = 1;
    protected static final int TEST_USER_ID = 0;
    
    protected IdUtil idUtil;
    protected AuthenticationManager authenticationManager;
    protected AccountManagerService accountManagerService;
    protected AccountService accountService;
    protected DuracloudUserService userService;

    private List<Object> mocks = new ArrayList<Object>();

    @Before
    public void before() throws Exception {
        mocks.clear();
        idUtil = createMock(IdUtil.class);
        accountManagerService = createMock(AccountManagerService.class);
        accountService = createMock(AccountService.class);
        userService = createMock(DuracloudUserService.class);        
        intializeAuthManager();
    }

    
     @After
    public void tearDown() throws Exception {
        verifyMocks();
    }

    private void verifyMocks() {
        for (Object o : mocks) {
            EasyMock.verify(o);
        }
    }

    
    protected <T> T createMock(Class<T> clazz) {
        T mock = EasyMock.createMock(clazz.getSimpleName(), clazz);
        mocks.add(mock);
        return mock;
    }

    protected void replayMocks() {
        for (Object o : mocks) {
            EasyMock.replay(o);
        }
    }
    
    protected void intializeAuthManager() {
        Authentication auth = createMock(Authentication.class);

        EasyMock.expect(auth.getName()).andReturn(TEST_USERNAME).anyTimes();
        authenticationManager =
            createMock(AuthenticationManager.class);
        
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        EasyMock.expect(auth.getPrincipal()).andReturn(createUser()).anyTimes();
        SecurityContextHolder.setContext(ctx);
    }

    /**
     * @return
     */
    protected AccountInfo createAccountInfo() {
        return new AccountInfo(TEST_ACCOUNT_ID, "testdomain", "test", "test",
                               "test", 0, 0, -1, null, null);
    }

    protected DuracloudUser createUser() {
        return createUser(TEST_USERNAME);
    }
    
    protected DuracloudUser createUser(String username) {
        DuracloudUser user =
            new DuracloudUser(TEST_USER_ID, username, "test", "test", "test", "test",
                              "test", "test");
        Set<AccountRights> rights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        rights.add(new AccountRights(1, TEST_ACCOUNT_ID.intValue(), 0, roles));
        user.setAccountRights(rights);
        return user;
    }
    
    protected ServerDetails createServerDetails() {
        return new ServerDetails(TEST_ACCOUNT_ID, 0, 0, null);
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
    

    protected Set<DuracloudUser> createUserSet() {
        Set<DuracloudUser> s = new HashSet<DuracloudUser>();
        s.add(createUser());
        return s;
    }

    
}

