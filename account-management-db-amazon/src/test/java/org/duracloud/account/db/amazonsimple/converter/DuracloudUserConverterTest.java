/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.DuracloudUser;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.ACCTS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.EMAIL_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.FIRSTNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.LASTNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.PASSWORD_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserConverterTest extends DomainConverterTest<DuracloudUser> {

    private static final String username = "username";
    private static final String password = "password";
    private static final String firstName = "firstName";
    private static final String lastName = "lastName";
    private static final String email = "email";
    private static final int counter = 4;

    private Map<String, List<String>> acctToRoles;
    private static final String acct0 = "acct-0";
    private static final String acct1 = "acct-1";
    private static final String acct2 = "acct-2";

    private static final String role0 = "role-0";
    private static final String role1 = "role-1";
    private static final String role2 = "role-2";

    @Override
    protected DomainConverter<DuracloudUser> createConverter() {
        return new DuracloudUserConverter();
    }

    @Override
    protected DuracloudUser createTestItem() {
        DuracloudUser user = new DuracloudUser(username,
                                               password,
                                               firstName,
                                               lastName,
                                               email,
                                               counter);

        acctToRoles = new HashMap<String, List<String>>();
        List<String> roles0 = new ArrayList<String>();
        List<String> roles1 = new ArrayList<String>();
        List<String> roles2 = new ArrayList<String>();

        roles0.add(role0);
        roles1.add(role0);
        roles2.add(role0);

        roles1.add(role1);
        roles2.add(role1);

        roles2.add(role2);

        acctToRoles.put(acct0, roles0);
        acctToRoles.put(acct1, roles1);
        acctToRoles.put(acct2, roles2);

        user.setAcctToRoles(acctToRoles);
        return user;
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        String acctsText = asString(acctToRoles);

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(FIRSTNAME_ATT, firstName));
        testAtts.add(new Attribute(LASTNAME_ATT, lastName));
        testAtts.add(new Attribute(EMAIL_ATT, email));
        testAtts.add(new Attribute(ACCTS_ATT, acctsText));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    private String asString(Map<String, List<String>> acctToRoles) {
        return new DuracloudUserConverter().asString(acctToRoles);
    }

    @Override
    protected void verifyItem(DuracloudUser user) {
        Assert.assertNotNull(user);

        Assert.assertNotNull(user.getCounter());
        Assert.assertNotNull(user.getUsername());
        Assert.assertNotNull(user.getPassword());
        Assert.assertNotNull(user.getFirstName());
        Assert.assertNotNull(user.getLastName());
        Assert.assertNotNull(user.getEmail());
        Assert.assertNotNull(user.getAcctToRoles());

        Assert.assertEquals(counter, user.getCounter().intValue());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());
        Assert.assertEquals(firstName, user.getFirstName());
        Assert.assertEquals(lastName, user.getLastName());
        Assert.assertEquals(email, user.getEmail());

        Map<String, List<String>> userAcctToRoles = user.getAcctToRoles();
        Assert.assertEquals(acctToRoles.size(), userAcctToRoles.size());
        for (String acct : user.getAcctToRoles().keySet()) {
            List<String> roles = acctToRoles.get(acct);

            boolean found = false;
            for (String userAcct : userAcctToRoles.keySet()) {
                if (acct.equals(userAcct)) {
                    found = true;
                    List<String> userRoles = userAcctToRoles.get(userAcct);
                    Assert.assertNotNull(userRoles);

                    Assert.assertEquals(roles.size(), userRoles.size());
                    for (String role : roles) {
                        Assert.assertTrue(role, userRoles.contains(role));
                    }
                }
            }
            Assert.assertTrue(acct + " not found.", found);
        }        
    }

}
