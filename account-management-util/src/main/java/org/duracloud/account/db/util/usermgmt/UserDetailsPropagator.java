/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.usermgmt;

import org.duracloud.account.db.model.Role;

import java.util.Set;

/**
 * This interface defines the contract for propagating updated and revoked
 * user rights down to the underlying DuraCloud webapps.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
public interface UserDetailsPropagator {

    /**
     * This method propagates the arg roles for the arg user and acct down
     * to the underlying DuraCloud webapps.
     *
     * @param acctId of DuraCloud acct to update
     * @param userId of user with updated rights
     * @param roles  new roles of arg user
     */
    public void propagateRights(Long acctId, Long userId, Set<Role> roles);

    /**
     * This method propagates the revocation of rights for the arg user and acct
     * down to the underlying DuraCloud webapps.
     *
     * @param acctId of DuraCloud acct to update
     * @param userId of user with revoked rights
     */
    public void propagateRevocation(Long acctId, Long userId);

    /**
     * This method propagates the update of a user's details down to the
     * underlying DuraCloud webapps for the given account.
     *
     * @param acctId of DuraCloud acct to update
     * @param userId of the user who has changed their password
     */
    public void propagateUserUpdate(Long acctId, Long userId);


    /**
     * This method propagates the update of a group down to the
     * underlying DuraCloud webapps for the given account.
     *
     * @param acctId of DuraCloud acct to update
     * @param groupId of the group that has changed
     */
    public void propagateGroupUpdate(Long acctId, Long groupId);

    /**
     * This method propagates the changes in an account cluster, such
     * as the addition or removal of an account to a cluster. If the ID of an
     * account within the cluster is known, use this method for propagation.
     *
     * @param acctId of DuraCloud acct to update
     * @param clusterId of the cluster that has changed
     */
    public void propagateClusterUpdate(Long acctId, Long clusterId);

    /**
     * This method propagates the changes in an account cluster, such
     * as the addition or removal of an account to a cluster. If no ID of an
     * account within the cluster is  known, use this method for propagation.
     *
     * @param clusterId of the cluster that has changed
     */
    public void propagateClusterUpdate(Long clusterId);

}
