/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance;

import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.appconfig.domain.DurareportConfig;

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
     * Collects the DuraService configuration for a DuraCloud instance
     * @return the information needed to initialize DuraService
     */
    public DuraserviceConfig getDuraserviceConfig();

    /**
     * Collects the DuraReport configuration for a DuraCloud instance
     * @return the information needed to initialize DuraReport
     */
    public DurareportConfig getDurareportConfig();

}
