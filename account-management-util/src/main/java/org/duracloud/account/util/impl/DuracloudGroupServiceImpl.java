/*
* Copyright (c) 2009-2010 DuraSpace. All rights reserved.
*/

package org.duracloud.account.util.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.DuracloudGroupService;
import org.duracloud.account.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.util.error.DuracloudGroupNotFoundException;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Nov 11, 2011
 *
 */
public class DuracloudGroupServiceImpl implements DuracloudGroupService{

    private Set<DuracloudGroup> userGroups = new HashSet<DuracloudGroup>();

    
    
    @Override
    public Set<DuracloudGroup> getGroups() {
        return Collections.unmodifiableSet(userGroups);
    }

    @Override
    public DuracloudGroup getGroup(String name)
        throws DuracloudGroupNotFoundException {

        for (DuracloudGroup g : userGroups) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        
        throw new DuracloudGroupNotFoundException(name + " not found");
    }
    
    @Override
    public DuracloudGroup createGroup(String name)
        throws DuracloudGroupAlreadyExistsException,
            DBConcurrentUpdateException {
        try{
            getGroup(name);
            throw new DuracloudGroupAlreadyExistsException("already exists for this group:" + name);
        }catch(DuracloudGroupNotFoundException ex){
            DuracloudGroup group = new DuracloudGroup(name);
            userGroups.add(group);
            return group;
        }
    }

    @Override
    public void deleteGroup(String name) throws DBConcurrentUpdateException {
        for (DuracloudGroup g : userGroups) {
            if (g.getName().equals(name)) {
                userGroups.remove(g);
                break;
            }
        }

    }

    @Override
    public void updateGroupUsers(String groupName, Set<DuracloudUser> users)
        throws DuracloudGroupNotFoundException,
            DBConcurrentUpdateException {
        DuracloudGroup group = getGroup(groupName);
        group.setUsers(users);
    }

}
