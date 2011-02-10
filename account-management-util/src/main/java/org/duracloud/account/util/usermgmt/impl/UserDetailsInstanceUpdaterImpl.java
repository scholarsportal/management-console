/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.util.error.DuracloudInstanceUpdateException;
import org.duracloud.account.util.usermgmt.UserDetailsInstanceUpdater;
import org.duracloud.appconfig.domain.Application;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Feb 3, 2011
 */
public class UserDetailsInstanceUpdaterImpl implements UserDetailsInstanceUpdater {

    private Logger log = LoggerFactory.getLogger(UserDetailsInstanceUpdaterImpl.class);

    private final static String port = "443";
    private final static String duraserviceContext = "duraservice";
    private final static String durastoreContext = "durastore";
    private final static String duradminContext = "duradmin";

    @Override
    public void updateUserDetails(String host,
                                  Set<SecurityUserBean> userBeans,
                                  RestHttpHelper restHelper) {

        if (StringUtils.isBlank(host) || null == userBeans ||
            userBeans.size() == 0 || null == restHelper) {
            StringBuilder msg = new StringBuilder("Invalid arguments: ");
            msg.append(host + ", ");
            msg.append(userBeans + ", ");
            msg.append(restHelper);

            throw new DuracloudInstanceUpdateException(msg.toString());
        }

        for (Application app : getApplications(host, restHelper)) {
            app.setSecurityUsers(userBeans);
        }
    }

    private List<Application> getApplications(String host,
                                              RestHttpHelper restHelper) {
        List<Application> apps = new ArrayList<Application>();
        apps.add(new Application(host, port, duraserviceContext, restHelper));
        apps.add(new Application(host, port, durastoreContext, restHelper));
        apps.add(new Application(host, port, duradminContext, restHelper));

        return apps;
    }

}
