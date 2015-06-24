/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.instance;

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
