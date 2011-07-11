/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance;

import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.appconfig.domain.DurareportConfig;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;

import java.util.Set;

/**
 * This interface defines the contract for updating the duracloud webapps
 * deployed on the given host.
 *
 * @author Andrew Woods
 *         Date: 2/10/11
 */
public interface InstanceUpdater {

    /**
     * This method initializes the duracloud webapps deployed on the arg host
     *
     * @param host              of webapps
     * @param duradminConfig    config for DurAdmin
     * @param durastoreConfig   config for DuraStore
     * @param duraserviceConfig config for DuraService
     * @param durareportConfig config for DuraReport
     * @param restHelper        connection utility
     */
    public void initializeInstance(String host,
                                   DuradminConfig duradminConfig,
                                   DurastoreConfig durastoreConfig,
                                   DuraserviceConfig duraserviceConfig,
                                   DurareportConfig durareportConfig,
                                   RestHttpHelper restHelper);

    /**
     * This methods updates the duracloud webapps deployed on the arg host
     * with the arg userBeans.
     *
     * @param host       of webapps
     * @param userBeans  of new user roles
     * @param restHelper connection utility
     */
    public void updateUserDetails(String host,
                                  Set<SecurityUserBean> userBeans,
                                  RestHttpHelper restHelper);

}
