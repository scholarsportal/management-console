/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountCluster;
import org.springframework.security.access.annotation.Secured;

/**
 * @author: Bill Branan
 * Date: 2/21/12
 */
public interface AccountClusterService {

    /**
     * Retrieves information about the Account Cluster
     *
     * @return AccountCluster
     */
    @Secured({"role:ROLE_ROOT, scope:SELF_ACCT"})
    public AccountCluster retrieveAccountCluster();

    /**
     * Changes the display name of the cluster
     *
     * @param clusterName
     */
    @Secured({"role:ROLE_ROOT, scope:SELF_ACCT"})
    public void renameAccountCluster(String clusterName);

    /**
     * Adds an account to this cluster. Ensures that both the cluster and
     * the account are aware of the cluster membership.
     *
     * @param accountId
     */
    @Secured({"role:ROLE_ROOT, scope:SELF_ACCT"})
    public void addAccountToCluster(int accountId);

    /**
     * Removes an account from this cluster. Ensures that both the cluster and
     * the account are aware that the cluster membership has ended.
     *
     * @param accountId
     */
    @Secured({"role:ROLE_ROOT, scope:SELF_ACCT"})
    public void removeAccountFromCluster(int accountId);

}
