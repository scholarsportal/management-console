/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class EmailAddressesParser {
    public static List<String> parse(String emailAddresses) {
        if (emailAddresses == null) {
            return new ArrayList<String>(0);
        } else {
            String[] emailAddressArray = emailAddresses.split("[,]");
            List<String> emailList =
                new ArrayList<String>(emailAddressArray.length);
            for (int i = 0; i < emailAddressArray.length; i++) {
                String email = emailAddressArray[i];
                email = email.trim();
                //other extraction, analysis might go here
                //...
                
                if (email.length() > 0) {
                    emailList.add(email);
                }
            }
            return emailList;
        }
    }
}
