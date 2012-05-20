/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.domain;

import org.duracloud.account.common.domain.AccountInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains report detailing health for a set of DuraCloud instance
 * Storage Reporters.
 *
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterReport {

    private Map<AccountInfo, StoreReporterInfo> reporterInfos;

    public StoreReporterReport() {
        this.reporterInfos = new HashMap<AccountInfo, StoreReporterInfo>();
    }

    /**
     * This method adds an error message associated with the arg account.
     *
     * @param acct  with error
     * @param error message
     */
    public void addAcctError(AccountInfo acct, String error) {
        StoreReporterInfo instance = new StoreReporterInfo(acct.getSubdomain());
        instance.setError(error);
        reporterInfos.put(acct, instance);
    }

    /**
     * This method adds arg instance details for the arg account.
     *
     * @param acct         with instance info
     * @param reporterInfo for arg account
     */
    public void addAcctInfo(AccountInfo acct, StoreReporterInfo reporterInfo) {
        reporterInfos.put(acct, reporterInfo);
    }

    /**
     * This methods gets all Storage Reporter infos for all accounts.
     *
     * @return map of account to instance info
     */
    public Map<AccountInfo, StoreReporterInfo> getReporterInfos() {
        return reporterInfos;
    }

    /**
     * This method gets all errors for all accounts.
     *
     * @return map of account to error info
     */
    public Map<AccountInfo, StoreReporterInfo> getReporterErrors() {
        Map<AccountInfo, StoreReporterInfo> reporterErrors =
            new HashMap<AccountInfo, StoreReporterInfo>();
        for (AccountInfo acct : reporterInfos.keySet()) {
            StoreReporterInfo instance = reporterInfos.get(acct);
            if (instance.hasErrors()) {
                reporterErrors.put(acct, instance);
            }
        }
        return reporterErrors;
    }

    /**
     * This method returns true if any of the Storage Reporters within this
     * report contain errors.
     *
     * @return true if any errors exist in DuraCloud instance Store Reporters
     */
    public boolean hasErrors() {
        for (StoreReporterInfo instance : reporterInfos.values()) {
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
        for (AccountInfo acct : reporterInfos.keySet()) {
            accts.add(acct);
        }
        Collections.sort(accts);

        for (AccountInfo acctInfo : accts) {

            StoreReporterInfo instanceInfo = reporterInfos.get(acctInfo);
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
