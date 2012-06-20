/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap.domain;

/**
 * This enum defines LDAP schema object classes used by the Management Console.
 *
 * @author Andrew Woods
 *         Date: 6/12/12
 */
public enum LdapObjectClass {

    DC_OBJECT("dcObject"),
    ORGANIZATION("organization"),
    PERSON("x-idp-person"),
    RIGHTS("x-idp-rights"),
    GROUP("x-idp-group");

    private String text;

    LdapObjectClass(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
