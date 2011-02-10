/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt;

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
public interface UserDetailsInstanceUpdater {

    /**
     * This methods updates the duracloud webapps deployed on the arg host
     * with the arg userBeans.
     *
     * @param host       of webapps
     * @param userBeans  of new user roles
     * @param restHelper connection utility
     */
    void updateUserDetails(String host,
                           Set<SecurityUserBean> userBeans,
                           RestHttpHelper restHelper);
}
