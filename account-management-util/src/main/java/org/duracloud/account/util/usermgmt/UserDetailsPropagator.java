/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt;

import org.duracloud.account.common.domain.Role;

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
    public void propagateRights(int acctId, int userId, Set<Role> roles);

    /**
     * This method propagates the revocation of rights for the arg user and acct
     * down to the underlying DuraCloud webapps.
     *
     * @param acctId of DuraCloud acct to update
     * @param userId of user with revoked rights
     */
    public void propagateRevocation(int acctId, int userId);

    /**
     * This method propagates the update of a user's password down to the
     * underlying DuraCloud webapps for the given account.
     *
     * @param acctId of DuraCloud acct to update
     * @param userId of the user who has changed their password
     */
    public void propagatePasswordUpdate(int acctId, int userId);
}
