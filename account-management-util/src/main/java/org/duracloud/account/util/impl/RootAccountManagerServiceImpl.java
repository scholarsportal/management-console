/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.RootAccountManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class RootAccountManagerServiceImpl implements RootAccountManagerService {
	private Logger log = LoggerFactory.getLogger(getClass());
    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;
    
    public RootAccountManagerServiceImpl(DuracloudUserRepo userRepo, DuracloudAccountRepo accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
    }

	@Override
	public void addDuracloudImage(String imageId, String version,
			String description) {
		throw new NotImplementedException("addDuracloudImage not implemented");
	}


	@Override
	public List<AccountInfo> listAllAccounts(String filter) {
		List<String> accountIds = accountRepo.getIds();
		List<AccountInfo> accountInfos = new LinkedList<AccountInfo>();
		for(String id : accountIds){
			try{
				AccountInfo accountInfo = accountRepo.findById(id);
				if(filter == null || accountInfo.getOrgName().startsWith(filter)){
					accountInfos.add(accountInfo);
				}
			}catch(DBNotFoundException ex){
				log.error("account[{}] not found; skipping.", id, ex);
			}
		}
		return accountInfos;
	}

	@Override
	public List<DuracloudUser> listAllUsers(String filter) {
		List<String> userIds = userRepo.getIds();
		List<DuracloudUser> users = new LinkedList<DuracloudUser>();
		for(String id : userIds){
			try{
				DuracloudUser user = userRepo.findById(id);
				
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

  
}
