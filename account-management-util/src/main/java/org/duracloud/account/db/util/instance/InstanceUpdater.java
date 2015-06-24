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
     * @param durabossConfig config for DuraBoss
     * @param restHelper        connection utility
     */
    public void initializeInstance(String host,
                                   DuradminConfig duradminConfig,
                                   DurastoreConfig durastoreConfig,
                                   DurabossConfig durabossConfig,
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
