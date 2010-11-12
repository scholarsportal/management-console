/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.mockimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class MockDuracloudAccountRepo implements DuracloudAccountRepo {
	private Map<String, AccountInfo> accountInfoMap = new HashMap<String, AccountInfo>();

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#findById(java.lang.String)
	 */
	@Override
	public AccountInfo findById(String id) throws DBNotFoundException {
		if(!this.accountInfoMap.containsKey(id)){
			throw new DBNotFoundException("account [ " + id + "] not found.");
		}
		return this.accountInfoMap.get(id);
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#getIds()
	 */
	@Override
	public List<String> getIds() {
		return new ArrayList<String>(this.accountInfoMap.keySet());
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#save(java.lang.Object)
	 */
	@Override
	public void save(AccountInfo item) throws DBConcurrentUpdateException {
		accountInfoMap.put(item.getId(),item);
	}

}
