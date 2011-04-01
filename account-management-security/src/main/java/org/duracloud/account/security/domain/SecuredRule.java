/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.domain;

import org.duracloud.common.error.DuraCloudRuntimeException;

import java.util.StringTokenizer;

/**
 * @author Andrew Woods
 *         Date: 4/1/11
 */
public class SecuredRule {

    private String role;
    private String scope;

    private static final String delim = ",";
    private static final String prefixRole = "role:";
    private static final String prefixScope = "scope:";

    public SecuredRule(String rule) {
        if (null == rule) {
            doThrow("arg Rule may not be null.");
        }

        StringTokenizer tokenizer = new StringTokenizer(rule, delim);
        if (tokenizer.countTokens() != 2) {
            doThrow("There must be two tokens. " + rule);
        }

        String fullRole = tokenizer.nextToken().trim();
        String fullScope = tokenizer.nextToken().trim();

        if (!fullRole.startsWith(prefixRole)) {
            doThrow("Role must begin with " + prefixRole + ", " + rule);
        }

        if (!fullScope.startsWith(prefixScope)) {
            doThrow("Scope must begin with " + prefixScope + ", " + rule);
        }

        this.role = fullRole.substring(prefixRole.length());
        this.scope = fullScope.substring(prefixScope.length());
    }

    public String getRole() {
        return role;
    }

    public String getScope() {
        return scope;
    }

    private void doThrow(String msg) {
        throw new IllegalArgumentException("Invalid rule: " + msg);
    }

}
