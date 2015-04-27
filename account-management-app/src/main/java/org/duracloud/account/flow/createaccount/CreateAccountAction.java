/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.flow.createaccount;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.app.controller.AccountDetailsController;
import org.duracloud.account.app.controller.AccountsController;
import org.duracloud.account.app.controller.FullAccountForm;
import org.duracloud.account.app.controller.NewAccountForm;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.util.AccountManagerService;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.util.MessageHelper;
import org.duracloud.account.util.UrlHelper;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * 
 * @author Daniel Bernstein 
 *         Date: Mar 3, 2012
 * 
 */
@Component
public class CreateAccountAction extends AbstractAction {
    @Autowired
    private AccountManagerService accountManagerService;
    @Autowired 
    private MessageHelper messageHelper;
 
    @Autowired
    private MessageSource messageSource = null;
    
    @Transactional
    public Event doExecute(RequestContext context) throws Exception {

        NewAccountForm newAccountForm =
            (NewAccountForm) context.getFlowScope().get("newAccountForm");
        FullAccountForm fullAccountForm =
            (FullAccountForm) context.getFlowScope().get("fullAccountForm");
        
        boolean reducedRedundancy = false;
        Set<StorageProviderType> secondaryStorageProviders =
            new HashSet<StorageProviderType>();

        if (!newAccountForm.isCommunity()) {
            if (fullAccountForm.getSecondaryStorageProviders() != null) {
                secondaryStorageProviders.addAll(fullAccountForm.getSecondaryStorageProviders());
            }
            reducedRedundancy = fullAccountForm.isUseReducedRedundancy();
        }

        AccountCreationInfo aci =
            new AccountCreationInfo(newAccountForm.getSubdomain(),
                                    newAccountForm.getAcctName(),
                                    newAccountForm.getOrgName(),
                                    newAccountForm.getDepartment(),
                                    StorageProviderType.AMAZON_S3,
                                    secondaryStorageProviders,
                                    newAccountForm.getAccountType(),
                                    newAccountForm.getAccountClusterId() == null
                                        ? -1L
                                        : newAccountForm.getAccountClusterId());

        AccountService as = accountManagerService.createAccount(aci);
        if(!newAccountForm.isCommunity()){
            as.setPrimaryStorageProviderRrs(reducedRedundancy);
        }

        String contextPath = context.getExternalContext().getContextPath();
        String accountName = newAccountForm.getAcctName();
        Long accountId = as.getAccountId();

        Message message;

        if(newAccountForm.isCommunity()){
            String pattern =
                contextPath
                    + AccountDetailsController.ACCOUNTS_PATH
                    + AccountDetailsController.ACCOUNT_DETAILS_MAPPING;
            String accountUri = UrlHelper.formatAccountId(accountId, pattern);
            
            Object[] args = new Object[] { accountName, accountUri };
            message = 
                messageHelper.createMessageSuccess(messageSource,
                                                   "account.create.community.success",
                                                   args);
            
        }else{
            String pattern =
                contextPath
                    + AccountsController.BASE_MAPPING
                    + AccountsController.ACCOUNT_SETUP_MAPPING;
            String accountUri = UrlHelper.formatId(accountId, pattern);
            
            Object[] args = new Object[] { accountName, accountUri };
            message = 
                messageHelper.createMessageSuccess(messageSource,
                                                   "account.create.full.success",
                                                   args);
            
        }
        
        context.getFlowScope().put(UserFeedbackUtil.FEEDBACK_KEY, message);
        return success();
    }

    public void
        setAccountManagerService(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

}
