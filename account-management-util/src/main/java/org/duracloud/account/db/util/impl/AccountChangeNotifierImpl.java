/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.common.event.AccountChangeEvent;
import org.duracloud.common.event.AccountChangeEvent.EventType;
import org.duracloud.account.db.model.GlobalProperties;
import org.duracloud.account.db.util.AccountChangeNotifier;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.AmazonSNSClient;
/**
 * 
 * @author Daniel Bernstein
 *
 */
@Component("accountChangeNotifier")
public class AccountChangeNotifierImpl implements AccountChangeNotifier {
    
    private AmazonSNSClient snsClient;
    
    private GlobalPropertiesConfigService globalPropertiesConfigService;

    /**
     * 
     * @param globalPropertiesConfigService
     */
    @Autowired
    public AccountChangeNotifierImpl(GlobalPropertiesConfigService globalPropertiesConfigService) {
        this.snsClient = new AmazonSNSClient();
        this.globalPropertiesConfigService = globalPropertiesConfigService;
    }
    
    @Override
    public void accountChanged(String account) {
        AccountChangeEvent accountChangeEvent = new AccountChangeEvent(
                AccountChangeEvent.EventType.ACCOUNT_CHANGED, account);
        publishEvent(accountChangeEvent);
    }

    private void publishEvent(AccountChangeEvent accountChangeEvent) {
        GlobalProperties properties = globalPropertiesConfigService.get();
        this.snsClient.publish(properties.getInstanceNotificationTopicArn(),
                AccountChangeEvent.serialize(accountChangeEvent));
    }

    @Override
    public void storageProvidersChanged(String accountId) {
        publishEvent(new AccountChangeEvent(EventType.STORAGE_PROVIDERS_CHANGED,
                accountId));
    }

    @Override
    public void userStoreChanged(String accountId) {
        publishEvent(new AccountChangeEvent(EventType.USERS_CHANGED,
                accountId));
    }

    @Override
    public void rootUsersChanged() {
        publishEvent(new AccountChangeEvent(EventType.ACCOUNT_CHANGED));
    }
}
