/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.domain;

import org.duracloud.account.common.domain.AccountInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains report detailing health for a set of DuraCloud instances.
 *
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class InstanceReport {

    private Map<AccountInfo, InstanceInfo> instanceInfos;

    public InstanceReport() {
        this.instanceInfos = new HashMap<AccountInfo, InstanceInfo>();
    }

    /**
     * This method adds an error message associated with the arg account.
     *
     * @param acct  with error
     * @param error message
     */
    public void addAcctError(AccountInfo acct, String error) {
        InstanceInfo instance = new InstanceInfo(acct.getSubdomain());
        instance.setServerStatus(error);
        instanceInfos.put(acct, instance);
    }

    /**
     * This method adds arg instance details for the arg account.
     *
     * @param acct         with instance info
     * @param instanceInfo for arg account
     */
    public void addAcctInstance(AccountInfo acct, InstanceInfo instanceInfo) {
        instanceInfos.put(acct, instanceInfo);
    }

    /**
     * This methods gets all instance infos for all accounts.
     *
     * @return map of account to instance info
     */
    public Map<AccountInfo, InstanceInfo> getInstanceInfos() {
        return instanceInfos;
    }

    /**
     * This method gets all errors for all accounts.
     *
     * @return map of account to error info
     */
    public Map<AccountInfo, InstanceInfo> getInstanceErrors() {
        Map<AccountInfo, InstanceInfo> instanceErrors =
            new HashMap<AccountInfo, InstanceInfo>();
        for (AccountInfo acct : instanceInfos.keySet()) {
            InstanceInfo instance = instanceInfos.get(acct);
            if (instance.hasErrors()) {
                instanceErrors.put(acct, instance);
            }
        }
        return instanceErrors;
    }

    /**
     * This method returns true if any of the instances within this report
     * contain errors.
     *
     * @return true if any errors exist within DuraCloud instances
     */
    public boolean hasErrors() {
        for (InstanceInfo instance : instanceInfos.values()) {
            if (instance.hasErrors()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------\n");
        sb.append("Accounts with errors:\n");
        sb.append("---------------------\n");

        List<AccountInfo> accts = new ArrayList<AccountInfo>();
        for (AccountInfo acct : instanceInfos.keySet()) {
            accts.add(acct);
        }
        Collections.sort(accts);

        for (AccountInfo acctInfo : accts) {

            InstanceInfo instanceInfo = instanceInfos.get(acctInfo);
            if (null != instanceInfo && instanceInfo.hasErrors()) {
                sb.append("Account: (");
                sb.append(acctInfo.getAcctName());
                sb.append(") ");
                sb.append(instanceInfo);
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

}
