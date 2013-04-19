/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication.domain;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Bill Branan
 *         Date: 4/19/13
 */
public class DuplicationInfoTest {

    @Test
    public void testDuplicationInfo() {
        // Check host
        String host = "host";
        DuplicationInfo dupInfo = new DuplicationInfo(host);
        assertEquals(host, dupInfo.getHost());

        // Check issues listing
        assertFalse(dupInfo.hasIssues());
        assertFalse(dupInfo.toString().contains("ISSUES"));

        String issue = "This is an issue";
        dupInfo.addIssue(issue);
        assertTrue(dupInfo.hasIssues());
        List<String> issues = dupInfo.getIssues();
        assertEquals(1, issues.size());
        assertEquals(issue, issues.get(0));

        String issue2 = "This is another issue";
        dupInfo.addIssue(issue2);
        issues = dupInfo.getIssues();
        assertEquals(2, issues.size());

        assertTrue(dupInfo.toString().contains("ISSUES"));

        // Check primary space counts
        Map<String, Long> primaryCounts = dupInfo.getPrimarySpaceCounts();
        assertEquals(0, primaryCounts.size());

        dupInfo.addPrimarySpace("space1", 1);
        dupInfo.addPrimarySpace("space2", 2);
        primaryCounts = dupInfo.getPrimarySpaceCounts();
        assertEquals(2, primaryCounts.size());
        assertEquals(new Long(1), primaryCounts.get("space1"));
        assertEquals(new Long(2), primaryCounts.get("space2"));

        // Check secondary space counts
        Map<String, Long> secondaryCounts = dupInfo.getSecondarySpaceCounts();
        assertEquals(0, secondaryCounts.size());

        dupInfo.addSecondarySpace("space1", 1);
        dupInfo.addSecondarySpace("space2", 2);
        secondaryCounts = dupInfo.getSecondarySpaceCounts();
        assertEquals(2, secondaryCounts.size());
        assertEquals(new Long(1), secondaryCounts.get("space1"));
        assertEquals(new Long(2), secondaryCounts.get("space2"));
    }

}
