/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import java.util.Set;

import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.db.util.error.DuracloudGroupNotFoundException;
import org.duracloud.account.db.util.error.InvalidGroupNameException;
import org.springframework.security.access.annotation.Secured;

/**
 * A 'group-centric' interface for reading and writing groups associated
 * with an individual account.
 *
 * @author: Daniel Bernstein
 * Date: Nov 11, 2011
 */
public interface DuracloudGroupService {

    /**
     * Returns a set of groups associated with the underlying Account.
     *
     * @param acctId associated with group
     * @return a set of groups or null if no groups associated with the account.
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public Set<DuracloudGroup> getGroups(Long acctId);

    /**
     * @param name   of the group
     * @param acctId associated with group
     * @return the group with the matching name
     * @throws DuracloudGroupNotFoundException
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudGroup getGroup(String name, Long acctId)
        throws DuracloudGroupNotFoundException;

    /**
     * @param name   of the new group
     * @param acctId associated with group
     * @return the newly created group
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudGroup createGroup(String name, Long acctId)
        throws DuracloudGroupAlreadyExistsException, InvalidGroupNameException;

    /**
     * Deletes a group.  If the group does not exist, nothing happens.
     *
     * @param group  to delete
     * @param acctId associated with group
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void deleteGroup(DuracloudGroup group, Long acctId);

    /**
     * This method replaces the users (if any) associated with the specified group.
     *
     * @param group  to be updated
     * @param users  to associate with group
     * @param acctId associated with group
     * @throws DuracloudGroupNotFoundException
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void updateGroupUsers(DuracloudGroup group,
                                 Set<DuracloudUser> users,
                                 Long acctId)
        throws DuracloudGroupNotFoundException;

}
