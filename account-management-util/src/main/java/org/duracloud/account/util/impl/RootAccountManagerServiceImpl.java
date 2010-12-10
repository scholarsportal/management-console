/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.RootAccountManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class RootAccountManagerServiceImpl implements RootAccountManagerService {

	private Logger log = LoggerFactory.getLogger(RootAccountManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    
    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr) {
        this.repoMgr = duracloudRepoMgr;
    }

	@Override
	public void addDuracloudImage(String imageId, String version,
			String description) {
		throw new NotImplementedException("addDuracloudImage not implemented");
	}


	@Override
	public Set<AccountInfo> listAllAccounts(String filter) {
		Set<Integer> accountIds = getAccountRepo().getIds();
		Set<AccountInfo> accountInfos = new HashSet<AccountInfo>();
		for(int acctId : accountIds){
			try{
				AccountInfo accountInfo = getAccountRepo().findById(acctId);
				if(filter == null || accountInfo.getOrgName().startsWith(filter)){
					accountInfos.add(accountInfo);
				}
			}catch(DBNotFoundException ex){
				log.error("account[{}] not found; skipping.", acctId, ex);
			}
		}
		return accountInfos;
	}

	@Override
	public Set<DuracloudUser> listAllUsers(String filter) {
		Set<Integer> userIds = getUserRepo().getIds();
		Set<DuracloudUser> users = new HashSet<DuracloudUser>();
		for(int id : userIds){
			try{
				DuracloudUser user = getUserRepo().findById(id);
				
				if(filter == null || (user.getUsername().startsWith(filter)
						|| user.getFirstName().startsWith(filter)
						|| user.getLastName().startsWith(filter)
						|| user.getEmail().startsWith(filter))
				){
					users.add(user);
				}
			}catch(DBNotFoundException ex){
				log.error("account[{}] not found; skipping.", id, ex);
			}
		}
		return users;

	}

    private DuracloudUserRepo getUserRepo() {
        return repoMgr.getUserRepo();
    }

    private DuracloudAccountRepo getAccountRepo() {
        return repoMgr.getAccountRepo();
    }
  
}
