/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
