package org.duracloud.account.util.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.easymock.EasyMock;

public class AccountUtilTestData {
	public static final String[] ACCOUNT_IDS = new String[]{"0","1","2"};
	public static final String[] USERNAMES = new String[]{"danny","bill","andrew"};
	public static final String ORG_PREFIX = "org-";
	
	public static  DuracloudUserRepo createMockUserRepo() {
		DuracloudUserRepo mockRepo = EasyMock.createMock(DuracloudUserRepo.class);
		List<String> ids = new ArrayList<String>(Arrays.asList(USERNAMES));
		EasyMock.expect(mockRepo.getIds()).andReturn(ids);
		for(String id : ids){
			try{
				EasyMock.expect(mockRepo.findById(id)).andReturn(new DuracloudUser(id,id,"test", "test", id+"@duracloud.org"));
			}catch(DBNotFoundException ex){
				ex.printStackTrace();
			}
		}

		EasyMock.replay(mockRepo);
		return mockRepo;
	}

	public static DuracloudAccountRepo createMockAccountRepo() {
		DuracloudAccountRepo mockRepo = EasyMock.createMock(DuracloudAccountRepo.class);
		List<String> ids = new ArrayList<String>(Arrays.asList(ACCOUNT_IDS));
		EasyMock.expect(mockRepo.getIds()).andReturn(ids);
		for(String id : ids){
			try{
				EasyMock.expect(mockRepo.findById(id)).andReturn(new AccountInfo(id,"subdomain-"+id,"account-"+id,ORG_PREFIX+id,null, null, null));
			}catch(DBNotFoundException ex){
				ex.printStackTrace();
			}
		}

		EasyMock.replay(mockRepo);
		return mockRepo;	
	}
}