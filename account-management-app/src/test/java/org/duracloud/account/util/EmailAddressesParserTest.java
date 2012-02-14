/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class EmailAddressesParserTest {
    @Test
    public void testParse() {
        String[] email = new String[4];
        for(int i = 0; i < email.length; i++){
            email[i] = "test" + i + "@duracloud.org";
        }
        
        String emails = email[0] + ", " + email[1] + "\n" + email[2] + " " + email[3];
        
        List<String> list = EmailAddressesParser.parse(emails);
        for(int i = 0; i < email.length; i++){
            Assert.assertEquals(email[i], list.get(i));
        }

    }

}
