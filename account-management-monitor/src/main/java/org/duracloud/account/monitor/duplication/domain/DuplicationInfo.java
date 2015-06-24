/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class holds details about the duplication status of the spaces in a
 * DuraCloud instance
 *
 * @author Bill Branan
 *         Date: 4/16/13
 */
public class DuplicationInfo {

    private String host;
    private Map<String, Store> stores;
    private List<String> issues;

    public DuplicationInfo(String host) {
        this.host = host;
        this.stores = new HashMap<>();
        this.issues = new ArrayList<>();
    }

    public void addIssue(String issue) {
        issues.add(issue);
    }

    public List<String> getIssues() {
        return issues;
    }

    public boolean hasIssues() {
        return issues.size() > 0;
    }

    public void addSpaceCount(String storeId, String spaceId, long count) {
        checkStore(storeId);
        stores.get(storeId).addSpace(spaceId, count);
    }

    private void checkStore(String storeId) {
        if(!stores.containsKey(storeId)) {
            stores.put(storeId, new Store(storeId));
        }
    }

    public String getHost() {
        return host;
    }

    public Set<String> getStoreIds() {
        return stores.keySet();
    }

    public Map<String, Long> getSpaceCounts(String storeId) {
        checkStore(storeId);
        return stores.get(storeId).getSpaceCounts();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(host);
        sb.append(" status: ");

        if (issues.size() == 0) {
            sb.append("OK");

        } else {
            sb.append("ISSUES REPORTED:");
            for(String issue: issues) {
                sb.append("\n");
                sb.append(issue);
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private class Store {
        private String storeId;
        private Map<String, Long> spaceCounts;

        public Store(String storeId) {
            this.storeId = storeId;
            spaceCounts = new HashMap<>();
        }

        public String getStoreId() {
            return storeId;
        }

        public void addSpace(String spaceId, long count) {
            spaceCounts.put(spaceId, count);
        }

        public Map<String, Long> getSpaceCounts() {
            return spaceCounts;
        }
    }

}
