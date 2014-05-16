/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.error.AccessDeniedException;
import org.duracloud.account.db.util.security.AnnotationParser;
import org.duracloud.account.db.util.security.impl.MethodInvocationImpl;
import org.duracloud.notification.Emailer;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class wraps another AccountService implementation with
 * AccessDecisionVoter security checks based on the AccountService annotations.
 *
 * @author Andrew Woods
 *         Date: 4/7/11
 */
public class AccountServiceSecuredImpl implements AccountService {

    private Logger log = LoggerFactory.getLogger(AccountServiceSecuredImpl.class);

    private AccountService accountService;
    private Authentication authentication;
    private AccessDecisionVoter voter;

    private Map<String, Object[]> methodMap;

    public AccountServiceSecuredImpl(AccountService accountService,
                                     Authentication authentication,
                                     AccessDecisionVoter voter,
                                     AnnotationParser annotationParser) {
        this.accountService = accountService;
        this.authentication = authentication;
        this.voter = voter;
        this.methodMap = annotationParser.getMethodAnnotationsForClass(Secured.class,
                                                                       this.getClass());
    }

    private void throwIfAccessDenied(Object... args) {
        String methodName = getCurrentMethodName();

        Set<ConfigAttribute> configAtts = new HashSet<ConfigAttribute>();
        for (Object obj : methodMap.get(methodName)) {
            configAtts.add(new SecurityConfig((String) obj));
        }

        MethodInvocation invocation = new MethodInvocationImpl(this,
                                                               methodName,
                                                               args);
        int decision = voter.vote(authentication, invocation, configAtts);
        if (decision != AccessDecisionVoter.ACCESS_GRANTED) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    @Override
    public Long getAccountId() {
        throwIfAccessDenied();
        return accountService.getAccountId();
    }

    @Override
    public AccountInfo retrieveAccountInfo() {
        throwIfAccessDenied();
        return accountService.retrieveAccountInfo();
    }

    @Override
    public void storeAccountInfo(String acctName,
                                 String orgName,
                                 String department) {
        throwIfAccessDenied(acctName, orgName, department);
        accountService.storeAccountInfo(acctName, orgName, department);
    }

    @Override
    public void storeAccountStatus(AccountInfo.AccountStatus status) {
        throwIfAccessDenied(status);
        accountService.storeAccountStatus(status);
    }

    @Override
    public void storeSubdomain(String subdomain) {
        throwIfAccessDenied(subdomain);
        accountService.storeSubdomain(subdomain);
    }

    @Override
    public String getSubdomain() {
        throwIfAccessDenied();
        return accountService.getSubdomain();
    }

    @Override
    public StorageProviderAccount getPrimaryStorageProvider() {
        throwIfAccessDenied();
        return accountService.getPrimaryStorageProvider();
    }

    @Override
    public void setPrimaryStorageProviderRrs(boolean rrs) {
        throwIfAccessDenied();
        accountService.setPrimaryStorageProviderRrs(rrs);
    }


    @Override
    public Set<StorageProviderAccount> getSecondaryStorageProviders() {
        throwIfAccessDenied();
        return accountService.getSecondaryStorageProviders();
    }

    @Override
    public void addStorageProvider(StorageProviderType storageProviderType) {
        throwIfAccessDenied(storageProviderType);
        accountService.addStorageProvider(storageProviderType);
    }

    @Override
    public void removeStorageProvider(Long storageProviderId) {
        throwIfAccessDenied(storageProviderId);
        accountService.removeStorageProvider(storageProviderId);
    }

    @Override
    public Set<DuracloudUser> getUsers() {
        throwIfAccessDenied();
        return accountService.getUsers();
    }

    @Override
    public UserInvitation inviteUser(String emailAddress, String adminUsername, Emailer emailer) {
        throwIfAccessDenied(emailAddress, adminUsername, emailer);
        return accountService.inviteUser(emailAddress, adminUsername, emailer);
    }

    @Override
    public Set<UserInvitation> getPendingInvitations() {
        throwIfAccessDenied();
        return accountService.getPendingInvitations();
    }

    @Override
    public void deleteUserInvitation(Long invitationId) {
        throwIfAccessDenied(invitationId);
        accountService.deleteUserInvitation(invitationId);
    }

    @Override
    public void cancelAccount(String username, Emailer emailer,
                              Collection<String> adminAddresses) {
        throwIfAccessDenied(username, emailer, adminAddresses);
        accountService.cancelAccount(username, emailer, adminAddresses);
    }

    @Override
    public ServerDetails retrieveServerDetails() {
        throwIfAccessDenied();
        return accountService.retrieveServerDetails();
    }

    @Override
    public void storeServerDetails(ServerDetails serverDetails) {
        throwIfAccessDenied(serverDetails);
        accountService.storeServerDetails(serverDetails);
    }

    @Override
    public ComputeProviderAccount getComputeProvider() {
        throwIfAccessDenied();
        return accountService.getComputeProvider();
    }
}
