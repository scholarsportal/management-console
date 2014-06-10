/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance;

import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;

/**
 * @author: Bill Branan
 * Date: 2/23/11
 */
public interface InstanceConfigUtil {

    /**
     * Collects the DurAdmin configuration for a DuraCloud instance
     * @return the information needed to initialize DurAdmin
     */
    public DuradminConfig getDuradminConfig();

    /**
     * Collects the DuraStore configuration for a DuraCloud instance
     * @return the information needed to initialize DuraStore
     */
    public DurastoreConfig getDurastoreConfig();

    /**
     * Collects the DuraBoss configuration for a DuraCloud instance
     * @return the information needed to initialize DuraBoss
     */
    public DurabossConfig getDurabossConfig();

}
