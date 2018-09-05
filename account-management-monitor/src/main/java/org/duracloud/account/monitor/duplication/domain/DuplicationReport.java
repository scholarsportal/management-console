/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class contains a report detailing space duplication status for a
 * set of DuraCloud accounts.
 *
 * @author Bill Branan
 * Date: 4/16/13
 */
public class DuplicationReport {

    private Map<String, DuplicationInfo> dupInfos;

    public DuplicationReport() {
        this.dupInfos = new HashMap<>();
    }

    /**
     * This method adds details for the duplication status at a given host.
     *
     * @param host host of account
     * @param info duplication information
     */
    public void addDupInfo(String host, DuplicationInfo info) {
        dupInfos.put(host, info);
    }

    /**
     * This method gets all duplication infos for all accounts.
     *
     * @return map of account to instance info
     */
    public Map<String, DuplicationInfo> getDupInfos() {
        return dupInfos;
    }

    /**
     * This method gets all issues discovered for all checked accounts
     *
     * @return list of duplication infos with issues
     */
    public List<DuplicationInfo> getDupIssues() {
        List<DuplicationInfo> dupIssues = new LinkedList<>();
        for (DuplicationInfo dupInfo : dupInfos.values()) {
            if (dupInfo.hasIssues()) {
                dupIssues.add(dupInfo);
            }
        }
        return dupIssues;
    }

    /**
     * This method returns true if at least one account duplication info
     * indicates that issues were encountered
     *
     * @return true if any errors exist in DuraCloud instance Store Reporters
     */
    public boolean hasIssues() {
        for (DuplicationInfo dupCheck : dupInfos.values()) {
            if (dupCheck.hasIssues()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------\n");
        sb.append("Duplication Issues:\n");
        sb.append("-------------------\n");

        for (DuplicationInfo dupInfo : getDupIssues()) {
            sb.append(dupInfo.toString()).append("\n");
        }
        return sb.toString();
    }

}
