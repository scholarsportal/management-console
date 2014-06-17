package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.*;
import org.duracloud.account.db.util.AccountManagerService;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.storage.util.IdUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.*;

public class AmaTestBase {

    protected static final String TEST_USERNAME = "testuser";
    protected static final Long TEST_ACCOUNT_ID = 1L;
    protected static final Long TEST_USER_ID = 0L;
    
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

    protected AccountInfo createAccountInfo() {
        return createAccountInfo(null);
    }

    /**
     * @return
     */
    protected AccountInfo createAccountInfo(Long id) {
        if(id == null) {
            id = TEST_ACCOUNT_ID;
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(id);
        accountInfo.setSubdomain("testdomain");
        accountInfo.setAcctName("test");
        accountInfo.setOrgName("test");
        accountInfo.setDepartment("test");

        ServerDetails serverDetails = createServerDetails(0L);
        accountInfo.setServerDetails(serverDetails);

        return accountInfo;
    }

    protected DuracloudUser createUser() {
        return createUser(TEST_USERNAME);
    }
    
    protected DuracloudUser createUser(String username) {
        DuracloudUser user = new DuracloudUser();
        user.setId(TEST_USER_ID);
        user.setUsername(username);
        user.setPassword("test");
        user.setFirstName("test");
        user.setLastName("test");
        user.setEmail("test");
        user.setSecurityQuestion("test");
        user.setSecurityAnswer("test");

        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        AccountRights rights = new AccountRights();
        rights.setId(1L);
        rights.setAccount(createAccountInfo(TEST_ACCOUNT_ID));
        rights.setUser(user);
        rights.setRoles(roles);
        user.setAccountRights(new HashSet<AccountRights>());
        user.getAccountRights().add(rights);
        return user;
    }

    protected ServerDetails createServerDetails() {
        return createServerDetails(TEST_ACCOUNT_ID);
        
    }

    protected ServerDetails createServerDetails(Long id) {
        if(id == null) {
            id = TEST_ACCOUNT_ID;
        }
        ServerDetails serverDetails = new ServerDetails();
        serverDetails.setId(id);
        ComputeProviderAccount compute = new ComputeProviderAccount();
        compute.setId(0L);;
        serverDetails.setComputeProviderAccount(compute);
        StorageProviderAccount primaryStorage = new StorageProviderAccount();
        primaryStorage.setId(0L);
        serverDetails.setPrimaryStorageProviderAccount(primaryStorage);

        return serverDetails;
    }

    protected void setupGenericAccountAndUserServiceMocks(Long accountId)
        throws AccountNotFoundException, DBNotFoundException {
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

