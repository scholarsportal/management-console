/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.common.domain;

/**
 * This enum defines the services plans of an account which also determines
 * which services are available in a service repository.
 *
 * @author Andrew Woods
 *         Date: 6/16/11
 */
public enum ServicePlan {

    STARTER_ARCHIVING("Starter Package: Archiving and Preservation"),
    STARTER_MEDIA("Starter Package: Media Access"),
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
