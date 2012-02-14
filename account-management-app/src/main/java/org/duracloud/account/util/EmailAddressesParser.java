/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
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
            String[] emailAddressArray = emailAddresses.split("[,\\s\\n]");
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
