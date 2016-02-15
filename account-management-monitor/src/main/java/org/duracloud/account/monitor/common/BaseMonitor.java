/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.common;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.GlobalProperties;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.common.model.Credential;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author Bill Branan
 *         Date: 4/16/13
 */
public abstract class BaseMonitor {

    private static final String HOST_SUFFIX = ".duracloud.org";

    protected Logger log;

    protected DuracloudAccountRepo acctRepo;

    protected GlobalPropertiesConfigService globalPropertiesConfigService;
    
    protected void init(DuracloudAccountRepo acctRepo,
            GlobalPropertiesConfigService globalPropertiesConfigService) {
        this.acctRepo = acctRepo;
        this.globalPropertiesConfigService = globalPropertiesConfigService;
    }

    protected AccountInfo getAccount(String host)
        throws DBNotFoundException {
        String subdomain = host;
        if(subdomain.endsWith(HOST_SUFFIX)) {
            subdomain = host.substring(0, host.indexOf(HOST_SUFFIX));
        }
        return acctRepo.findBySubdomain(subdomain);
    }

    protected Credential getRootCredential() {
        GlobalProperties props = this.globalPropertiesConfigService.get();
        String rootUsername = props.getDuracloudRootUsername();
        
        String rootPassword = props.getDuracloudRootPassword();
        return new Credential(rootUsername, rootPassword);
    }


}
