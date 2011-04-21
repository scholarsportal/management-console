/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.BaseDomainData;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter;
import org.duracloud.common.model.Credential;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.USER_ID_ATT;

/**
 * @author Andrew Woods
 *         Date: 4/21/11
 */
public class TestBaseDuracloudRepoImpl extends BaseTestDuracloudRepoImpl {

    private static final AmazonSimpleDBCaller simpleDBCaller = new AmazonSimpleDBCaller();
    private static final AmazonSimpleDB db = newClient();
    private static final String domain = "TEST_BASIC_DOMAIN";

    private static final BaseDuracloudRepoImplTester testRepo = new BaseDuracloudRepoImplTester(
        simpleDBCaller,
        db,
        domain);

    private final DomainConverter converter = new DuracloudRightsConverter();
    private List<Integer> ids;

    private static AmazonSimpleDBClient newClient() {
        Credential cred = null;
        try {
            cred = getCredential();
        } catch (Exception e) {
            Assert.fail("Error reading credential from db: " + e.getMessage());
        }
        AWSCredentials awsCred = new BasicAWSCredentials(cred.getUsername(),
                                                         cred.getPassword());
        return new AmazonSimpleDBClient(awsCred);
    }

    @BeforeClass
    public static void beforeClass() {
        testRepo.createDomainIfNecessary();
    }

    @Before
    public void setUp() throws Exception {
        verifyRepoSize(testRepo, 0);

        ids = new ArrayList<Integer>();
    }

    @After
    public void tearDown() throws Exception {
        for (int id : ids) {
            testRepo.delete(id);
        }
    }

    @Test
    public void testFindItemById() throws Exception {
        for (int i = 0; i < 3; i++) {
            int id = getNextId();
            BaseDomainData item = createItem(id);
            testRepo.doSave(item, converter);

            for (int x = 0; x < 5; x++) {
                testRepo.findItemById(id);
            }
        }
    }

    @Test
    public void testFindItemsByAttribute() throws Exception {
        for (int i = 0; i < 3; i++) {
            int id = getNextId();
            BaseDomainData item = createItem(id);
            testRepo.doSave(item, converter);

            testRepo.findItemsByAttribute(ACCOUNT_ID_ATT, Integer.toString(id));
        }
    }

    @Test
    public void testFindItemByAttributes() throws Exception {
        for (int i = 0; i < 3; i++) {
            int id = getNextId();
            BaseDomainData item = createItem(id);

            Map<String, String> atts = new HashMap<String, String>();
            atts.put(ACCOUNT_ID_ATT, Integer.toString(id));
            atts.put(USER_ID_ATT, Integer.toString(id));

            testRepo.doSave(item, converter);
            testRepo.findItemByAttributes(atts);
        }
    }

    @Test
    public void testDelete() throws Exception {
        for (int i = 0; i < 3; i++) {
            int id = getNextId();
            BaseDomainData item = createItem(id);
            testRepo.doSave(item, converter);
            testRepo.delete(id);
        }
    }

    private int getNextId() {
        int i = ids.size();
        ids.add(i);
        return i;
    }

    private BaseDomainData createItem(int id) {
        int acctId = id;
        int userId = id;
        Set<Role> roles = Role.ROLE_ADMIN.getRoleHierarchy();

        return new AccountRights(id, acctId, userId, roles);
    }

    private void verifyRepoSize(final BaseDuracloudRepoImpl repo,
                                final int expectedSize) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return repo.getIds().size();
            }
        }.call(expectedSize);
    }

    /**
     * Simple implementation of the base abstract class.
     */
    private static class BaseDuracloudRepoImplTester extends BaseDuracloudRepoImpl {

        public BaseDuracloudRepoImplTester(AmazonSimpleDBCaller caller,
                                           AmazonSimpleDB db,
                                           String domain) {
            super(caller, db, domain);
        }
    }
}
