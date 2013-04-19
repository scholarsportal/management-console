/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.common;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Set<Integer> instanceIds = instanceRepo.findByAccountId(acct.getId());
        int instanceId = instanceIds.iterator().next();
        DuracloudInstance instance = instanceRepo.findById(instanceId);

        return imageRepo.findById(instance.getImageId());
    }

    protected List<AccountInfo> getDuracloudAcctsHavingInstances() {
        List<AccountInfo> acctsHavingInstances = new ArrayList<>();
        List<AccountInfo> allAccts = getDuracloudAccts();

        for (AccountInfo acct : allAccts) {
            Set<Integer> result = null;
            try {
                result = instanceRepo.findByAccountId(acct.getId());

            } catch (DBNotFoundException e) {
                StringBuilder sb = new StringBuilder("No instance found ");
                sb.append("for account id ");
                sb.append(acct.getId());
                sb.append(" (");
                sb.append(acct.getSubdomain());
                sb.append(")");
                log.info(sb.toString());
            }

            if (null != result && result.size() != 0) {
                acctsHavingInstances.add(acct);
            }
        }
        return acctsHavingInstances;
    }

    protected List<AccountInfo> getDuracloudAccts() {
        List<AccountInfo> acctInfos = new ArrayList<>();

        Set<Integer> ids = acctRepo.getIds();
        for (int id : ids) {
            try {
                acctInfos.add(acctRepo.findById(id));

            } catch (DBNotFoundException e) {
                StringBuilder error = new StringBuilder("Error getting ");
                error.append("account with id ");
                error.append(id);
                log.error(error.toString());
                throw new DuraCloudRuntimeException(error.toString(), e);
            }
        }
        return acctInfos;
    }

}
