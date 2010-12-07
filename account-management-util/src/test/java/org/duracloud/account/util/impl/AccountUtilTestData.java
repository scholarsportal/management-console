/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.easymock.EasyMock;

public class AccountUtilTestData {
	public static final Integer[] ACCOUNT_IDS = new Integer[] { 0, 1, 2 };
	public static final String[] USERNAMES = new String[] { "danny", "bill", "andrew" };
	public static final String ORG_PREFIX = "org-";
	public static final String NOT_A_USER_USERNAME = "notauser";
	public static final String NOT_AN_ACCOUNT_ID = "notanaccountid";

	public static DuracloudUserRepo createMockUserRepo() {
		DuracloudUserRepo mockRepo = EasyMock
				.createMock(DuracloudUserRepo.class);
        Set<Integer> ids = new HashSet<Integer>();
        for (Integer i : ACCOUNT_IDS) {
            ids.add(i);
        }

		EasyMock.expect(mockRepo.getIds()).andReturn(ids);
		try {

			for (String id : ids) {
				DuracloudUser user = new DuracloudUser(id, id, "test", "test", id
						+ "@duracloud.org");

				for(String aid : ACCOUNT_IDS){
					user.addAccount(aid);

				}
				EasyMock.expect(mockRepo.findById(id)).andReturn(user).anyTimes();
			}

			EasyMock.expect(mockRepo.findById(NOT_A_USER_USERNAME)).andThrow(
					new DBNotFoundException(NOT_A_USER_USERNAME
							+ " not a user!")).anyTimes();

		} catch (DBNotFoundException ex) {
			ex.printStackTrace();
		}

		EasyMock.replay(mockRepo);
		return mockRepo;
	}


	public static DuracloudAccountRepo createUnplayedeMockAccountRepo() {
		DuracloudAccountRepo mockRepo = EasyMock
				.createMock(DuracloudAccountRepo.class);
		List<String> ids = new ArrayList<String>(Arrays.asList(ACCOUNT_IDS));
		EasyMock.expect(mockRepo.getIds()).andReturn(ids).anyTimes();
		for (String id : ids) {
			try {
				EasyMock.expect(mockRepo.findById(id)).andReturn(
						new AccountInfo(id, "subdomain-" + id, "account-" + id,
								ORG_PREFIX + id, null, null, null)).anyTimes();
			} catch (DBNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
		return mockRepo;
	}
	
	public static DuracloudAccountRepo createMockAccountRepo() {
		DuracloudAccountRepo mockRepo = createUnplayedeMockAccountRepo();
		EasyMock.replay(mockRepo);
		return mockRepo;
	}

    public static DuracloudRightsRepo createMockRightsRepo() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}