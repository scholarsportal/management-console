/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Nov 12, 2011
 */
public interface DuracloudGroupRepo extends BaseRepo<DuracloudGroup> {

    /**
     * This method returns a single group with the given groupname.
     *
     * @param groupname of group
     * @return group
     * @throws DBNotFoundException if no item found
     */
    public DuracloudGroup findByGroupname(String groupname)
        throws DBNotFoundException;

    /**
     * This method returns all groups.
     *
     * @return all groups
     * @throws DBNotFoundException if no groups found
     */
    public Set<DuracloudGroup> findAllGroups() throws DBNotFoundException;

}
