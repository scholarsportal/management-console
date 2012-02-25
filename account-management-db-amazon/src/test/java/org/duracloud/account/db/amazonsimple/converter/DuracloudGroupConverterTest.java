/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter.GROUPNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter.USERS_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author Andrew Woods
 *         Date: Nov 12, 2011
 */
public class DuracloudGroupConverterTest extends DomainConverterTest<DuracloudGroup> {

    private static final int id = 0;
    private static final String groupname = DuracloudGroup.PREFIX + "test";
    private static final int accountId = 7;
    private static Set<Integer> userIds;
    private static final int counter = 4;

    @BeforeClass
    public static void initialize() throws Exception {
        userIds = new HashSet<Integer>();
        userIds.add(6);
        userIds.add(3);
        userIds.add(9);
    }

    @Override
    protected DomainConverter<DuracloudGroup> createConverter() {
        return new DuracloudGroupConverter();
    }

    @Override
    protected DuracloudGroup createTestItem() {
        return new DuracloudGroup(id, groupname, accountId, userIds, counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        BaseDomainConverter helper = (BaseDomainConverter) createConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(GROUPNAME_ATT, groupname));
        testAtts.add(new Attribute(USERS_ATT, helper.idsAsString(userIds)));
        testAtts.add(new Attribute(ACCOUNT_ID_ATT, helper.asString(accountId)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(DuracloudGroup group) {
        Assert.assertNotNull(group);

        Assert.assertNotNull(group.getCounter());
        Assert.assertNotNull(group.getName());
        Assert.assertNotNull(group.getUserIds());
        Assert.assertNotNull(group.getAccountId());

        Assert.assertEquals(counter, group.getCounter());
        Assert.assertEquals(groupname, group.getName());
        Assert.assertEquals(userIds, group.getUserIds());
        Assert.assertEquals(accountId, group.getAccountId());
    }

}
