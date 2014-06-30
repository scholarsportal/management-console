/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.common;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.db.repo.DuracloudServerImageRepo;
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
    protected DuracloudInstanceRepo instanceRepo;
    protected DuracloudServerImageRepo imageRepo;

    protected void init(DuracloudAccountRepo acctRepo,
                     DuracloudInstanceRepo instanceRepo,
                     DuracloudServerImageRepo imageRepo) {
        this.acctRepo = acctRepo;
        this.instanceRepo = instanceRepo;
        this.imageRepo = imageRepo;
    }

    protected AccountInfo getAccount(String host)
        throws DBNotFoundException {
        String subdomain = host;
        if(subdomain.endsWith(HOST_SUFFIX)) {
            subdomain = host.substring(0, host.indexOf(HOST_SUFFIX));
        }
        return acctRepo.findBySubdomain(subdomain);
    }

    protected Credential getRootCredential(AccountInfo acct)
        throws DBNotFoundException {
        ServerImage serverImage = findServerImage(acct);
        String rootPassword = serverImage.getDcRootPassword();
        return new Credential(ServerImage.DC_ROOT_USERNAME, rootPassword);
    }

    protected ServerImage findServerImage(AccountInfo acct)
        throws DBNotFoundException {
        List<DuracloudInstance> instances =
            instanceRepo.findByAccountId(acct.getId());
        return instances.iterator().next().getImage();
    }

    protected List<DuracloudInstance> getDuracloudInstances() {
        return instanceRepo.findAll();
    }

    protected Credential getRootCredential(DuracloudInstance instance) {
        String rootPassword = instance.getImage().getDcRootPassword();
        return new Credential(ServerImage.DC_ROOT_USERNAME, rootPassword);
    }


}
