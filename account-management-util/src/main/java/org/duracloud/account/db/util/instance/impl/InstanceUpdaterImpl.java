/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.db.util.instance.InstanceUpdater;
import org.duracloud.account.db.util.instance.InstanceUtil;
import org.duracloud.account.db.util.error.DuracloudInstanceUpdateException;
import org.duracloud.appconfig.domain.*;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Feb 3, 2011
 */
public class InstanceUpdaterImpl implements InstanceUpdater, InstanceUtil {

    private Logger log = LoggerFactory.getLogger(InstanceUpdaterImpl.class);

    private final static String port = "443";

    @Override
    public void initializeInstance(String host,
                                   DuradminConfig duradminConfig,
                                   DurastoreConfig durastoreConfig,
                                   DurabossConfig durabossConfig,
                                   RestHttpHelper restHelper){
        log.info("Initializing DuraCloud applications at host {}", host);

        if(StringUtils.isBlank(host) ||
           null == duradminConfig ||
           null == durastoreConfig ||
           null == durabossConfig ||
           null == restHelper) {
            StringBuilder msg = new StringBuilder("Invalid arguments: ");
            msg.append(host + ", ");
            msg.append(duradminConfig + ", ");
            msg.append(durastoreConfig + ", ");
            msg.append(durabossConfig + ", ");
            msg.append(restHelper);

            throw new DuracloudInstanceUpdateException(msg.toString());
        }

        Application durastoreApp = getDurastoreApplication(host, restHelper) ;
        checkResponse("DuraStore", durastoreApp.initialize(durastoreConfig));

        Application durabossApp = getDurabossApplication(host, restHelper);
        checkResponse("DuraBoss", durabossApp.initialize(durabossConfig));

        Application duradminApp = getDuradminApplication(host, restHelper);
        checkResponse("DurAdmin", duradminApp.initialize(duradminConfig));
    }

    @Override
    public void updateUserDetails(String host,
                                  Set<SecurityUserBean> userBeans,
                                  RestHttpHelper restHelper) {
        log.info("Updating user details at DuraCloud host {}", host);

        if (StringUtils.isBlank(host) ||
            null == userBeans ||
            null == restHelper) {
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
        apps.add(getDuradminApplication(host, restHelper));
        apps.add(getDurastoreApplication(host, restHelper));
        apps.add(getDurabossApplication(host, restHelper));
        return apps;
    }

    private Application getDuradminApplication(String host,
                                               RestHttpHelper restHelper) {
        return new Application(host, port, DURADMIN_CONTEXT, restHelper);
    }

    private Application getDurastoreApplication(String host,
                                                RestHttpHelper restHelper) {
        return new Application(host, port, DURASTORE_CONTEXT, restHelper);
    }


    private Application getDurabossApplication(String host,
                                               RestHttpHelper restHelper) {
        return new Application(host, port, DURABOSS_CONTEXT, restHelper);
    }


    private void checkResponse(String name,
                               RestHttpHelper.HttpResponse response) {
        if (null == response || response.getStatusCode() != 200) {
            String body = null;
            try {
                body = response.getResponseBody();
            } catch (IOException e) {
            } finally {
                StringBuilder msg = new StringBuilder("Error Initializing ");
                msg.append(name);
                msg.append(" Response Code: " + response.getStatusCode());
                if (null != body) {
                    msg.append("\nResponse Body:\n");
                    msg.append(body);
                }
                log.error(msg.toString());
                throw new DuracloudInstanceUpdateException(msg.toString());
            }
        }
    }

}
