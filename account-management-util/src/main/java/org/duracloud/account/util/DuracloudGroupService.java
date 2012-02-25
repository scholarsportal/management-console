/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.util.error.DuracloudGroupNotFoundException;
import org.duracloud.account.util.error.InvalidGroupNameException;
import org.springframework.security.access.annotation.Secured;

import java.util.Set;

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
     * @param acctId associated with group
     * @return a set of groups or null if no groups associated with the account.
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public Set<DuracloudGroup> getGroups(int acctId);

    /**
     * 
     * @param name of the group
     * @param acctId associated with group
     * @return the group with the matching name
     * @throws DuracloudGroupNotFoundException
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudGroup getGroup(String name, int acctId)
        throws DuracloudGroupNotFoundException;

    /**
     * 
     * @param name of the new group
     * @param acctId associated with group
     * @return the newly created group
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudGroup createGroup(String name, int acctId)
        throws DuracloudGroupAlreadyExistsException, InvalidGroupNameException,
               DBConcurrentUpdateException;

    /**
     * Deletes a group.  If the group does not exist, nothing happens.
     * @param group to delete
     * @param acctId associated with group
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void deleteGroup(DuracloudGroup group, int acctId)
        throws DBConcurrentUpdateException;

    /**
     * This method replaces the users (if any) associated with the specified group.
     * @param group to be updated
     * @param users to associate with group
     * @param acctId associated with group
     * @throws DuracloudGroupNotFoundException
     * @throws DBConcurrentUpdateException
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void updateGroupUsers(DuracloudGroup group,
                                 Set<DuracloudUser> users,
                                 int acctId)
    throws DuracloudGroupNotFoundException, DBConcurrentUpdateException;

}
