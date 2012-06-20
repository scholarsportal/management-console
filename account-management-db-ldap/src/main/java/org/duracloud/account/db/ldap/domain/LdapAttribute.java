/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap.domain;

/**
 * This enum defines LDAP schema attributes used by the Management Console.
 *
 * @author Andrew Woods
 *         Date: 6/12/12
 */
public enum LdapAttribute {

    USER_ID("uid"),
    SURNAME("sn"),
    GIVEN_NAME("givenName"),
    PASSWORD("userPassword"),
    MAIL("mail"),
    ORGANIZATION("o"),
    DESCRIPTION("description"),
    ORG_UNIT("organizationalUnit"),
    QUESTION("x-idp-securityQuestion"),
    ANSWER("x-idp-securityAnswer"),
    ENABLED("x-idp-enabled"),
    ACCT_NON_EXPIRED("x-idp-accountNonExpired"),
    CREDENTIALS_NON_EXPIRED("x-idp-credentialsNonExpired"),
    ACCT_NON_LOCKED("x-idp-accountNonLocked"),
    UNIQUE_ID("uniqueIdentifier"),
    COMMON_NAME("cn"),
    DISPLAY_NAME("displayName"),
    ACCOUNT("x-idp-account"),
    OBJECT_CLASS("objectClass"),
    ROLE_OCCUPANT("roleOccupant"),
    ROLE("x-idp-role"),
    MEMBER("member");

    private String text;

    private LdapAttribute(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
