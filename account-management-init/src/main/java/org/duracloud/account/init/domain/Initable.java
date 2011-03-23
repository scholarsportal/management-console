/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.domain;

/**
 * @author Andrew Woods
 *         Date: 3/22/11
 */
public interface Initable {

    /**
     * This method is to be implemented by classes that undergo initialization
     * from the AmaConfig.
     *
     * @param config from which to initialize
     */
    public void initialize(AmaConfig config);
}
