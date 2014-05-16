/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.flow.createaccount;

import junit.framework.Assert;
import org.duracloud.account.app.controller.AmaTestBase;
import org.duracloud.account.app.controller.FullAccountForm;
import org.duracloud.account.app.controller.NewAccountForm;
import org.duracloud.account.db.model.AccountType;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.util.MessageHelper;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.message.Message;
import org.springframework.context.MessageSource;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
/**
 * @author Daniel Bernstein
 *         Date: Mar 5, 2012
 */
public class CreateAccountActionTest extends AmaTestBase{
    private CreateAccountAction action = null;
    private MessageHelper messageHelper;
    private RequestContext requestContext;
    
    @Before
    public void before() throws Exception {
        super.before();
        this.action = new CreateAccountAction();
        //setup account manager service
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
        this.action.setAccountManagerService(this.accountManagerService);
        //setup message helper
        this.messageHelper = createMock(MessageHelper.class);
        this.messageHelper.createMessageSuccess(EasyMock.anyObject(MessageSource.class),
                                             EasyMock.isA(String.class),
                                             (Object[]) EasyMock.isA(Object.class));
        EasyMock.expectLastCall().andReturn(EasyMock.createMock(Message.class));
        this.action.setMessageHelper(this.messageHelper);
    }

    private void setupRequestContext(AccountType accountType, FullAccountForm fullAccountForm) {
        this.requestContext = createMock(RequestContext.class);
        LocalAttributeMap map = new LocalAttributeMap();
        NewAccountForm newAccountForm = new NewAccountForm();
        newAccountForm.setAccountType(accountType);
        map.put("newAccountForm", newAccountForm);
        if(fullAccountForm != null){
            map.put("fullAccountForm", fullAccountForm);
        }

        EasyMock.expect(this.requestContext.getFlowScope()).andReturn(map).times(3);
        ExternalContext externalContext = createMock(ExternalContext.class);
        EasyMock.expect(externalContext.getContextPath()).andReturn("testpath");
        EasyMock.expect(requestContext.getExternalContext())
                .andReturn(externalContext);
    }

    @Test
    public void testDoCreateCommunity() throws Exception {
        //setup requestcontext
        setupRequestContext(AccountType.COMMUNITY, null);

        this.accountManagerService.createAccount(EasyMock.isA(AccountCreationInfo.class),
                                                                 EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall().andReturn(accountService);
        replayMocks();
        
        assertSuccess(this.action.doExecute(requestContext));
    }

    
    @Test
    public void testDoCreateFull() throws Exception {

        FullAccountForm fullAccountForm = new FullAccountForm();
        fullAccountForm.setUseReducedRedundancy(true);

        //setup requestcontext
        setupRequestContext(AccountType.FULL, fullAccountForm);

        
        EasyMock.expect(this.accountManagerService.createAccount(EasyMock.isA(AccountCreationInfo.class),
                                                                 EasyMock.isA(DuracloudUser.class)))
                .andReturn(accountService);
        
        accountService.setPrimaryStorageProviderRrs(true);
        replayMocks();
        assertSuccess(this.action.doExecute(requestContext));
    }

    private void assertSuccess(Event event) {
        Assert.assertEquals("success", event.getId());
    }

}
