/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

/**
 * This enum defines the services plans of an account which also determines
 * which services are available in a service repository.
 *
 * @author Andrew Woods
 *         Date: 6/16/11
 */
public enum ServicePlan {

    PROFESSIONAL("Professional Package"),
    TRIAL("Trial Package"),
    ALL("All Services");

    private final String text;

    private ServicePlan(String sp) {
        text = sp;
    }

    public String getText() {
        return text;
    }

    public String getValue(){
        return name();
    }

    @Override
    public String toString() {
        return name();
    }

}