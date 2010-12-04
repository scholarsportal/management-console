/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.DELIM;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.ROLES_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.USER_ID_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class DuracloudRightsConverterTest extends DomainConverterTest<AccountRights> {

    private static final int id = 0;
    private static final int accountId = 100;
    private static final int userId = 200;
    private static Set<Role> roles;
    private static final int counter = 4;

    @BeforeClass
    public static void initialize() throws Exception {
        roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);
    }

    @Override
    protected DomainConverter<AccountRights> createConverter() {
        return createRightsConverter();
    }

    private DuracloudRightsConverter createRightsConverter() {
        return new DuracloudRightsConverter();
    }

    @Override
    protected AccountRights createTestItem() {
        return new AccountRights(id,
                                 accountId,
                                 userId,
                                 roles,
                                 counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudRightsConverter rightsCvtr = createRightsConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(ACCOUNT_ID_ATT,
                                   rightsCvtr.asString(accountId)));
        testAtts.add(new Attribute(USER_ID_ATT,
                                   rightsCvtr.asString(userId)));
        testAtts.add(new Attribute(ROLES_ATT,
                                   rightsCvtr.asString(roles)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(AccountRights rights) {
        assertNotNull(rights);

        assertNotNull(rights.getCounter());
        assertNotNull(rights.getAccountId());
        assertNotNull(rights.getUserId());
        assertNotNull(rights.getRoles());

        assertEquals(counter, rights.getCounter());
        assertEquals(accountId, rights.getAccountId());
        assertEquals(userId, rights.getUserId());
        assertEquals(roles, rights.getRoles());
    }

    @Test
    public void testAsString() {
        DuracloudRightsConverter rightsCvtr = createRightsConverter();

        assertEquals("100", rightsCvtr.asString(accountId));
        assertEquals("200", rightsCvtr.asString(userId));

        String rolesString = rightsCvtr.asString(roles);
        assertEquals(2, rolesString.split(DELIM).length);
        assertTrue(rolesString.contains(Role.ROLE_USER.name()));
        assertTrue(rolesString.contains(Role.ROLE_ADMIN.name()));
    }

    @Test
    public void testFromString() {
        DuracloudRightsConverter rightsCvtr = createRightsConverter();

        String roleString = Role.ROLE_USER.name() + DELIM +
                            Role.ROLE_ADMIN.name();
        Set<Role> roleSet = rightsCvtr.fromString(roleString);
        assertEquals(roles, roleSet);
    }

}
