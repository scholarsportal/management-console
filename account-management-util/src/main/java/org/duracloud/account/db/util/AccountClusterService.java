/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import org.duracloud.account.db.model.AccountCluster;
import org.springframework.security.access.annotation.Secured;

/**
 * @author Erik Paulsson
 *         Date: 7/16/13
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
    public void addAccountToCluster(Long accountId);

    /**
     * Removes an account from this cluster. Ensures that both the cluster and
     * the account are aware that the cluster membership has ended.
     *
     * @param accountId
     */
    @Secured({"role:ROLE_ROOT, scope:SELF_ACCT"})
    public void removeAccountFromCluster(Long accountId);
}
