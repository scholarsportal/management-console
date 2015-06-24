/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.flow.createaccount;

import junit.framework.Assert;

import org.duracloud.account.app.controller.AmaTestBase;
import org.duracloud.account.app.controller.FullAccountForm;
import org.duracloud.account.app.controller.NewAccountForm;
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

    private void setupRequestContext(FullAccountForm fullAccountForm) {
        this.requestContext = createMock(RequestContext.class);
        LocalAttributeMap map = new LocalAttributeMap();
        NewAccountForm newAccountForm = new NewAccountForm();
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
    public void testDoCreate() throws Exception {

        FullAccountForm fullAccountForm = new FullAccountForm();
        fullAccountForm.setUseReducedRedundancy(true);

        //setup requestcontext
        setupRequestContext(fullAccountForm);

        
        EasyMock.expect(this.accountManagerService.createAccount(EasyMock.isA(AccountCreationInfo.class)))
                .andReturn(accountService);
        
        accountService.setPrimaryStorageProviderRrs(true);
        replayMocks();
        assertSuccess(this.action.doExecute(requestContext));
    }

    private void assertSuccess(Event event) {
        Assert.assertEquals("success", event.getId());
    }

}
