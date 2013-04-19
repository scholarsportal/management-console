/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.duplication.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds details about the duplication status of the spaces in a
 * DuraCloud instance
 *
 * @author Bill Branan
 *         Date: 4/16/13
 */
public class DuplicationInfo {

    private String host;
    private Map<String, Long> primarySpaceCounts;
    private Map<String, Long> secondarySpaceCounts;
    private List<String> issues;

    public DuplicationInfo(String host) {
        this.host = host;
        this.primarySpaceCounts = new HashMap<>();
        this.secondarySpaceCounts = new HashMap<>();
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

    public void addPrimarySpace(String spaceId, long count) {
        primarySpaceCounts.put(spaceId, count);
    }

    public void addSecondarySpace(String spaceId, long count) {
        secondarySpaceCounts.put(spaceId, count);
    }

    public String getHost() {
        return host;
    }

    public Map<String, Long> getPrimarySpaceCounts() {
        return primarySpaceCounts;
    }

    public Map<String, Long> getSecondarySpaceCounts() {
        return secondarySpaceCounts;
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

}
