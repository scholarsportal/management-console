/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.mockimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class MockDuracloudUserRepo implements DuracloudUserRepo {
	private Map<Integer,DuracloudUser> usermap;

	private Logger log = LoggerFactory.getLogger(getClass());

	public MockDuracloudUserRepo() throws DBConcurrentUpdateException{
		usermap = new HashMap<Integer,DuracloudUser>();
        int userId = 0;
		save(new DuracloudUser(userId,"admin", "admin", "Sky", "Dancer", "admin@duracloud.org"));
		save(new DuracloudUser(++userId ,"user", "user", "Joe", "Bloggs", "jbloggs@duracloud.org"));
		save(new DuracloudUser(++userId,"root", "root", "Root", "User", "root@duracloud.org",
				new HashSet(Arrays.asList(new Role[]{Role.ROLE_ROOT}))));

		log.debug("constructed " + getClass());
		
	}
	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#findById(java.lang.String)
	 */
	@Override
	public DuracloudUser findById(int id) throws DBNotFoundException {
		if(usermap.containsKey(id)){
			return usermap.get(id);
		}
		throw new DBNotFoundException(id + " not found");
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#getIds()
	 */
	@Override
	public Set<Integer> getIds() {
		return this.usermap.keySet();
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#save(java.lang.Object)
	 */
	@Override
	public void save(DuracloudUser item) throws DBConcurrentUpdateException {
		log.debug("saved {}", item.getUsername());
		this.usermap.put(item.getId(), item);
	}

    @Override
    public DuracloudUser findByUsername(String username)
        throws DBNotFoundException {
        // Default method body
        return null;
    }

}
