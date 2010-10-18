/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.mockimpl;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class MockDuracloudAccountRepo implements DuracloudAccountRepo {

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#findById(java.lang.String)
	 */
	@Override
	public AccountInfo findById(String id) throws DBNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#getIds()
	 */
	@Override
	public List<String> getIds() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.db.BaseRepo#save(java.lang.Object)
	 */
	@Override
	public void save(AccountInfo item) throws DBConcurrentUpdateException {
		// TODO Auto-generated method stub

	}

}
