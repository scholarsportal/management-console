/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.mockimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class MockDuracloudUserRepo implements DuracloudUserRepo {
	private Map<String,DuracloudUser> usermap;
	private Logger log = LoggerFactory.getLogger(getClass());

	public MockDuracloudUserRepo() throws DBConcurrentUpdateException{
		usermap = new HashMap<String,DuracloudUser>();
		save(new DuracloudUser("admin", "admin", "Sky", "Dancer", "admin@duracloud.org"));
		save(new DuracloudUser("user", "user", "Joe", "Bloggs", "jbloggs@duracloud.org"));
		save(new DuracloudUser("root", "root", "Root", "User", "root@duracloud.org", 
				new HashSet(Arrays.asList(new Role[]{Role.ROLE_ROOT}))));

		log.debug("constructed " + getClass());
		
	}
	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#findById(java.lang.String)
	 */
	@Override
	public DuracloudUser findById(String id) throws DBNotFoundException {
		if(usermap.containsKey(id)){
			return usermap.get(id);
		}
		throw new DBNotFoundException(id + " not found");
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#getIds()
	 */
	@Override
	public List<String> getIds() {
		return new ArrayList<String>(this.usermap.keySet());
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#save(java.lang.Object)
	 */
	@Override
	public void save(DuracloudUser item) throws DBConcurrentUpdateException {
		log.debug("saved {}", item.getUsername());
		this.usermap.put(item.getUsername(), item);
	}

}
