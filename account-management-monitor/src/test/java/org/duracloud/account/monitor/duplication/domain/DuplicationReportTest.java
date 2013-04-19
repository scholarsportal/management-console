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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Bill Branan
 *         Date: 4/19/13
 */
public class DuplicationReportTest {

    @Test
    public void testDuplicationReport() {
        DuplicationReport dupReport = new DuplicationReport();
        assertEquals(0, dupReport.getDupInfos().size());
        assertFalse(dupReport.hasIssues());

        // Test info with no issues
        String host1 = "host-1";
        DuplicationInfo dupInfo1 = new DuplicationInfo(host1);
        dupReport.addDupInfo(host1, dupInfo1);
        assertEquals(1, dupReport.getDupInfos().size());
        assertFalse(dupReport.hasIssues());
        assertEquals(0, dupReport.getDupIssues().size());

        // Test info with issues
        String host2 = "host-2";
        DuplicationInfo dupInfo2 = new DuplicationInfo(host2);
        dupInfo2.addIssue("This is an issue");
        dupReport.addDupInfo(host2, dupInfo2);
        assertEquals(2, dupReport.getDupInfos().size());
        assertTrue(dupReport.hasIssues());
        List<DuplicationInfo> dupIssues = dupReport.getDupIssues();
        assertEquals(1, dupIssues.size());
        assertEquals(dupInfo2, dupIssues.get(0));
    }

}
