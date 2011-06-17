/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
    PROFESSIONAL("Professional Package");

    private final String text;

    private ServicePlan(String sp) {
        text = sp;
    }

    public static ServicePlan fromString(String pt) {
        for (ServicePlan pType : values()) {
            if (pType.text.equalsIgnoreCase(pt) ||
                pType.name().equalsIgnoreCase(pt)) {
                return pType;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

}
