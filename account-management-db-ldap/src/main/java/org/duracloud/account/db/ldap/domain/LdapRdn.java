/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap.domain;

/**
 * This enum defines LDAP schema relative DNs used by the Management Console.
 *
 * @author Andrew Woods
 *         Date: 6/12/12
 */
public enum LdapRdn {
    ROLE_DC("dc=roles"),
    ACCOUNT_OU("ou=accounts"),
    RIGHTS_OU("ou=rights"),
    GROUP_OU("ou=groups"),
    PEOPLE_OU("ou=people");

    private String text;

    LdapRdn(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
