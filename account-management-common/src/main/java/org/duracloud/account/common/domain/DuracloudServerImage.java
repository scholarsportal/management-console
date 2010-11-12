/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DuracloudServerImage implements Identifiable {
    private String id;

    @Override
    public String getId() {
        return id;
    }
}
