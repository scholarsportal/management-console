/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.domain;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.duracloud.account.monitor.hadoop.util.HadoopUtil.STATE;

/**
 * This class holds the state of the Hadoop services currently running or
 * completed across all managed DuraCloud instances.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopServiceReport {

    private Map<AccountInfo, Collection<HadoopServiceInfo>> servicesByAcct;
    private Map<AccountInfo, String> errorsByAcct;

    // Number of days a service can run before a warning flag is noted.
    private int thresholdDays;

    public HadoopServiceReport(int thresholdDays) {
        this.servicesByAcct =
            new HashMap<AccountInfo, Collection<HadoopServiceInfo>>();
        this.errorsByAcct = new HashMap<AccountInfo, String>();
        this.thresholdDays = thresholdDays;
    }

    /**
     * This method adds service info to the report for Hadoop services within
     * the arg account.
     *
     * @param acct     hosting services
     * @param services info
     */
    public void addAcctServices(AccountInfo acct,
                                Collection<HadoopServiceInfo> services) {
        if (null == acct || null == services) {
            return;
        }

        Collection<HadoopServiceInfo> existing = servicesByAcct.get(acct);
        if (null != existing) {
            services.addAll(existing);
        }
        servicesByAcct.put(acct, services);
    }

    /**
     * This method adds errors encountered to the report while collecting
     * info about Hadoop services within the arg account.
     *
     * @param acct  hosting services
     * @param error encountered
     */
    public void addAcctError(AccountInfo acct, String error) {
        errorsByAcct.put(acct, error);
    }

    /**
     * This method returns true if the report holds any errors.
     *
     * @return true if the report holds any errors
     */
    public boolean hasErrors() {
        return !errorsByAcct.isEmpty();
    }

    /**
     * This method returns true if the report holds and running or completed
     * services
     *
     * @return true if the report holds any services
     */
    public boolean hasServices() {
        for (AccountInfo acct : servicesByAcct.keySet()) {
            if (!servicesByAcct.get(acct).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is a simple getter.
     *
     * @return map of services keyed on account
     */
    public Map<AccountInfo, Collection<HadoopServiceInfo>> getServicesByAcct() {
        return servicesByAcct;
    }

    /**
     * This method is a simple getter.
     *
     * @return map of errors keyed on account
     */
    public Map<AccountInfo, String> getErrorsByAcct() {
        return errorsByAcct;
    }

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();

        if (hasErrors()) {
            report.append(getErrors());
        }

        if (hasServices()) {
            report.append("\n");
            report.append(getServices());
        } else {
            report.append("Currently no Hadoop services");
        }

        return report.toString();
    }

    private String getServices() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------\n");
        sb.append("Services:\n");
        sb.append("---------\n");

        List<AccountInfo> accts = new ArrayList<AccountInfo>();
        for (AccountInfo acct : servicesByAcct.keySet()) {
            accts.add(acct);
        }
        Collections.sort(accts);

        for (AccountInfo acctInfo : accts) {
            Collection<HadoopServiceInfo> serviceInfo = servicesByAcct.get(
                acctInfo);

            if (null != serviceInfo) {
                sb.append("Account: ");
                sb.append(acctInfo.getAcctName());
                sb.append(" (https://");
                sb.append(acctInfo.getSubdomain());
                sb.append(".duracloud.org)");

                sb.append("\n\t");
                sb.append("running: ");
                addServicesToReport(serviceInfo, STATE.RUNNING, sb);

                sb.append("\n\t");
                sb.append("completed: ");
                addServicesToReport(serviceInfo, STATE.COMPLETED, sb);
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    private String getErrors() {
        StringBuilder errors = new StringBuilder();
        errors.append("-------\n");
        errors.append("Errors:\n");
        errors.append("-------\n");

        List<AccountInfo> accts = new ArrayList<AccountInfo>();
        for (AccountInfo acct : errorsByAcct.keySet()) {
            accts.add(acct);
        }
        Collections.sort(accts);

        for (AccountInfo acctInfo : accts) {
            errors.append("Account: ");
            errors.append(acctInfo.getAcctName());
            errors.append(" (https://");
            errors.append(acctInfo.getSubdomain());
            errors.append(".duracloud.org)");
            errors.append(", msg: ");
            errors.append(errorsByAcct.get(acctInfo));
            errors.append("\n\n");
        }
        return errors.toString();
    }

    private void addServicesToReport(Collection<HadoopServiceInfo> services,
                                     HadoopUtil.STATE state,
                                     StringBuilder report) {
        if (null == services || services.size() == 0) {
            report.append("--");
            return;
        }

        Iterator<HadoopServiceInfo> itr = services.iterator();
        while (itr.hasNext()) {
            HadoopServiceInfo serviceInfo = itr.next();

            if (state.equals(serviceInfo.getState())) {
                report.append("\n\t\t");

                if (serviceInfo.elapsedExceedsDays(thresholdDays)) {
                    report.append("!! WARNING, EXCEEDS ");
                    report.append(thresholdDays);
                    report.append(" DAYS !!");
                    report.append("\n\t\t");
                }
                report.append(serviceInfo.toString());
            }
        }
    }

}
