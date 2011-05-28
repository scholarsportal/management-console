/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.DuracloudUser;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.EMAIL_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.FIRSTNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.LASTNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.PASSWORD_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.SECURITY_ANSWER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.SECURITY_QUESTION_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.USERNAME_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserConverterTest extends DomainConverterTest<DuracloudUser> {

    private static final int id = 0;
    private static final String username = "username";
    private static final String password = "password";
    private static final String firstName = "firstName";
    private static final String lastName = "lastName";
    private static final String email = "email";
    private static final String securityQuestion = "question";
    private static final String securityAnswer = "answer";
    private static final int counter = 4;

    @Override
    protected DomainConverter<DuracloudUser> createConverter() {
        return new DuracloudUserConverter();
    }

    @Override
    protected DuracloudUser createTestItem() {
        return new DuracloudUser(id,
                                 username,
                                 password,
                                 firstName,
                                 lastName,
                                 email,
                                 securityQuestion,
                                 securityAnswer,
                                 counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(USERNAME_ATT, username));
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(FIRSTNAME_ATT, firstName));
        testAtts.add(new Attribute(LASTNAME_ATT, lastName));
        testAtts.add(new Attribute(EMAIL_ATT, email));
        testAtts.add(new Attribute(SECURITY_QUESTION_ATT, securityQuestion));
        testAtts.add(new Attribute(SECURITY_ANSWER_ATT, securityAnswer));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
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
        Assert.assertNotNull(user.getSecurityQuestion());
        Assert.assertNotNull(user.getSecurityAnswer());

        Assert.assertEquals(counter, user.getCounter());
        Assert.assertEquals(username, user.getUsername());
        Assert.assertEquals(password, user.getPassword());
        Assert.assertEquals(firstName, user.getFirstName());
        Assert.assertEquals(lastName, user.getLastName());
        Assert.assertEquals(email, user.getEmail());
        Assert.assertEquals(securityQuestion, user.getSecurityQuestion());
        Assert.assertEquals(securityAnswer, user.getSecurityAnswer());
    }

}
