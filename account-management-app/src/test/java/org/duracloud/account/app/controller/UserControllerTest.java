package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class UserControllerTest  extends AmaControllerTestBase {
    private UserController userController;

    private DuracloudUserService userService;


    @Before
    public void before() throws Exception {
        super.before();

        userController = new UserController();
    }

    @Test
    public void testGetNewForm() {
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(session.getAttribute("redemptionCode")).
            andReturn(null).anyTimes();
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();

        EasyMock.replay(request, session);

        ModelAndView mv = userController.getNewForm(request);

        Assert.assertEquals(userController.NEW_USER_VIEW,
                            mv.getViewName());

        Map map = mv.getModel();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey(userController.NEW_USER_FORM_KEY));

        Object obj = map.get(userController.NEW_USER_FORM_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof NewUserForm);

        EasyMock.verify(request, session);
    }

    @Test
    public void testGetForgotPasswordForm() {
        ModelAndView mv = userController.getForgotPasswordForm(null);

        Assert.assertEquals(userController.FORGOT_PASSWORD_VIEW,
                            mv.getViewName());

        Map map = mv.getModel();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey(userController.FORGOT_PASSWORD_FORM_KEY));

        Object obj = map.get(userController.FORGOT_PASSWORD_FORM_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof ForgotPasswordForm);
    }

    @Test
    public void testGetChangePasswordForm() throws Exception {
        boolean internal = false;
        initializeMockUserServiceLoadUser(internal);

        Model model = new ExtendedModelMap();
        String view = userController.changePassword(TEST_USERNAME, model);

        Assert.assertEquals(userController.CHANGE_PASSWORD_VIEW,
                            view);

        Map map = model.asMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey(userController.CHANGE_PASSWORD_FORM_KEY));
        Assert.assertTrue(map.containsKey(userController.USER_KEY));

        Object obj = map.get(userController.CHANGE_PASSWORD_FORM_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof ChangePasswordForm);

        obj = map.get(userController.USER_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof DuracloudUser);

        EasyMock.verify(userService);
    }

    @Test
    public void testGetUser() throws Exception {
        setupAccountManagerService();
        boolean internal = true;
        initializeMockUserServiceLoadUser(internal);

        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);

        EasyMock.expect(session.getAttribute("redemptionCode")).
            andReturn(null).anyTimes();
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();

        EasyMock.replay(request, session);

        ModelAndView mv = userController.getUser(TEST_USERNAME, request);

        Assert.assertEquals(userController.USER_ACCOUNTS, mv.getViewName());

        Map map = mv.getModel();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey(userController.USER_KEY));

        Object obj = map.get(userController.USER_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof DuracloudUser);

        Assert.assertTrue(map.containsKey("activeAccounts"));
        obj = map.get("activeAccounts");
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);

        Assert.assertTrue(map.containsKey("pendingAccounts"));
        obj = map.get("pendingAccounts");
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);

        Assert.assertTrue(map.containsKey("inactiveAccounts"));
        obj = map.get("inactiveAccounts");
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof List);

        EasyMock.verify(request, session, userService, accountManagerService);
    }

    @Test
    public void testUpdateUser() throws Exception {
        initializeMockUserServiceStoreUser();

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        UserProfileEditForm editForm =
            EasyMock.createMock(UserProfileEditForm.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(false).anyTimes();
        EasyMock.expect(editForm.getFirstName()).
            andReturn("").anyTimes();
        EasyMock.expect(editForm.getLastName()).
            andReturn("").anyTimes();
        EasyMock.expect(editForm.getEmail()).
            andReturn("").anyTimes();
        EasyMock.expect(editForm.getSecurityQuestion()).
            andReturn("").anyTimes();
        EasyMock.expect(editForm.getSecurityAnswer()).
            andReturn("").anyTimes();

        EasyMock.replay(bindingResult, editForm);

        String view = userController.update(TEST_USERNAME,
                                            editForm,
                                            bindingResult,
                                            null);

        Assert.assertNotNull(view);
        Assert.assertEquals("redirect:/users/byid/" + TEST_USERNAME, view);

        EasyMock.verify(bindingResult, editForm, userService);
    }

    @Test
    public void testUpdateUserErrors() throws Exception {
        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(true).anyTimes();

        EasyMock.replay(bindingResult);

        String view = userController.update(TEST_USERNAME,
                                            null,
                                            bindingResult,
                                            null);

        Assert.assertNotNull(view);
        Assert.assertEquals(userController.USER_EDIT_VIEW, view);

        EasyMock.verify(bindingResult);
    }

    @Test
    public void testAddUser() throws Exception {
        userController.setAuthenticationManager(authenticationManager);
        initializeMockUserService();

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        NewUserForm newUserForm =
            EasyMock.createMock(NewUserForm.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(false).anyTimes();
        EasyMock.expect(newUserForm.getUsername()).
            andReturn(TEST_USERNAME).anyTimes();
        EasyMock.expect(newUserForm.getPassword()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getFirstName()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getLastName()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getEmail()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getSecurityQuestion()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getSecurityAnswer()).
            andReturn("").anyTimes();
        EasyMock.expect(newUserForm.getRedemptionCode()).
            andReturn("").anyTimes();

        EasyMock.replay(bindingResult, newUserForm);

        String view = userController.add(newUserForm, bindingResult, null, null);

        Assert.assertNotNull(view);
        Assert.assertEquals(
            "redirect:/users/byid/" + TEST_USERNAME, view);

        EasyMock.verify(bindingResult, newUserForm, userService);
    }

    @Test
    public void testAddUserErrors() throws Exception {
        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(true).anyTimes();

        EasyMock.replay(bindingResult);

        String view = userController.add(null, bindingResult, null, null);

        Assert.assertNotNull(view);
        Assert.assertEquals(userController.NEW_USER_VIEW, view);

        EasyMock.verify(bindingResult);
    }

    @Test
    public void testForgotPassword() throws Exception {
        initializeForgotPasswordMockUserService();

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        ForgotPasswordForm forgotPasswordForm =
            EasyMock.createMock(ForgotPasswordForm.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(false).anyTimes();
        EasyMock.expect(forgotPasswordForm.getUsername()).
            andReturn(TEST_USERNAME).anyTimes();
        EasyMock.expect(forgotPasswordForm.getSecurityQuestion()).
            andReturn("").anyTimes();
        EasyMock.expect(forgotPasswordForm.getSecurityAnswer()).
            andReturn("").anyTimes();

        EasyMock.replay(bindingResult, forgotPasswordForm);

        String view = userController.forgotPassword(forgotPasswordForm,
                                                    bindingResult,
                                                    null,
                                                    null);

        Assert.assertNotNull(view);
        Assert.assertEquals("redirect:/login", view);

        EasyMock.verify(bindingResult, forgotPasswordForm, userService);
    }

    @Test
    public void testChangePassword() throws Exception {
        initializeChangePasswordMockUserService();

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        ChangePasswordForm cbangePasswordForm =
            EasyMock.createMock(ChangePasswordForm.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(false).anyTimes();
        EasyMock.expect(cbangePasswordForm.getOldPassword()).
            andReturn("").anyTimes();
        EasyMock.expect(cbangePasswordForm.getPassword()).
            andReturn("").anyTimes();

        EasyMock.replay(bindingResult, cbangePasswordForm);

        String view = userController.changePassword(TEST_USERNAME,
                                                    cbangePasswordForm,
                                                    bindingResult,
                                                    null);

        Assert.assertNotNull(view);
        Assert.assertEquals("redirect:/users/byid/" + TEST_USERNAME, view);

        EasyMock.verify(bindingResult, cbangePasswordForm, userService);
    }

    @Test
    public void testForgotPasswordErrors() throws Exception {
        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(true).anyTimes();

        EasyMock.replay(bindingResult);

        String view = userController.forgotPassword(null,
                                                    bindingResult,
                                                    null,
                                                    null);

        Assert.assertNotNull(view);
        Assert.assertEquals(userController.FORGOT_PASSWORD_VIEW, view);

        EasyMock.verify(bindingResult);
    }

    @Test
    public void testChangePasswordErrors() throws Exception {
        boolean internal = false;
        initializeMockUserServiceLoadUser(internal);

        Model model = new ExtendedModelMap();        
        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(bindingResult.hasErrors()).
            andReturn(true).anyTimes();

        EasyMock.replay(bindingResult);

        String view = userController.changePassword(TEST_USERNAME,
                                                    null,
                                                    bindingResult,
                                                    model);

        Assert.assertNotNull(view);
        Assert.assertEquals(userController.USER_EDIT_VIEW, view);

        Map map = model.asMap();
        Assert.assertNotNull(map);
        Assert.assertTrue(map.containsKey(userController.USER_KEY));

        Object obj = map.get(userController.USER_KEY);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof DuracloudUser);

        EasyMock.verify(bindingResult, userService);
    }

    private void initializeMockUserService() throws Exception {
        userService =
            EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.createNewUser(EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class),
                                                  EasyMock.isA(String.class)))
            .andReturn(createUser())
            .anyTimes();
        EasyMock.replay(userService);
        userController.setUserService(userService);
    }

    private void initializeMockUserServiceLoadUser(boolean internal) throws Exception {
        userService =
            EasyMock.createMock(DuracloudUserService.class);

        if (internal) {
            EasyMock.expect(userService.loadDuracloudUserByUsernameInternal(
                TEST_USERNAME)).andReturn(createUser()).anyTimes();

        } else {
            EasyMock.expect(userService.loadDuracloudUserByUsername(
                TEST_USERNAME)).andReturn(createUser()).anyTimes();
        }
        EasyMock.replay(userService);
        userController.setUserService(userService);
    }

    private void initializeMockUserServiceStoreUser() throws Exception {
        userService =
            EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.loadDuracloudUserByUsername(TEST_USERNAME))
            .andReturn(createUser())
            .anyTimes();
        userService.storeUserDetails(EasyMock.anyInt(),
                                     EasyMock.isA(String.class),
                                     EasyMock.isA(String.class),
                                     EasyMock.isA(String.class),
                                     EasyMock.isA(String.class),
                                     EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(userService);
        userController.setUserService(userService);
    }

    private void initializeForgotPasswordMockUserService() throws Exception {
        userService =
            EasyMock.createMock(DuracloudUserService.class);
        userService.forgotPassword(EasyMock.eq(TEST_USERNAME),
                                   EasyMock.isA(String.class),
                                   EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .anyTimes();
        EasyMock.replay(userService);
        userController.setUserService(userService);
    }

    private void initializeChangePasswordMockUserService() throws Exception {
        userService =
            EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.loadDuracloudUserByUsername(TEST_USERNAME))
            .andReturn(createUser())
            .anyTimes();
        userService.changePassword(EasyMock.anyInt(),
                                   EasyMock.isA(String.class),
                                   EasyMock.anyBoolean(),
                                   EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .anyTimes();
        EasyMock.replay(userService);
        userController.setUserService(userService);
    }

    protected void setupAccountManagerService()
        throws AccountNotFoundException {
        accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        Set set =
            EasyMock.createMock(Set.class);
        Iterator iterator = EasyMock.createMock(Iterator.class);

        EasyMock.expect(iterator.hasNext())
            .andReturn(false)
            .anyTimes();
        EasyMock.expect(set.iterator())
            .andReturn(iterator)
            .anyTimes();

        EasyMock.expect(accountManagerService.findAccountsByUserId(EasyMock.anyInt()))
            .andReturn(set)
            .anyTimes();
        EasyMock.replay(accountManagerService, set, iterator);
        userController.setAccountManagerService(accountManagerService);
    }
}
