/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import org.apache.commons.io.FileUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ServicePlan;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author: Bill Branan
 * Date: Dec 21, 2010
 */
public class DbConverterTest {

    private static File workDir;
    private static String oldAcctList;
    private static String newAcctList;
    private static String newServerDetailsList;
    private DbConverter converter;

    @BeforeClass
    public static void init() {
        workDir = new File("target/db-converter-test");
        workDir.mkdir();

        oldAcctList =
            "<list>\n" +
            "  <AccountInfo>\n" +
            "    <id>0</id>\n" +
            "    <counter>1</counter>\n" +
            "    <subdomain>usa</subdomain>\n" +
            "    <acctName>USA</acctName>\n" +
            "    <orgName>United States</orgName>\n" +
            "    <department>Government</department>\n" +
            "    <computeProviderAccountId>1</computeProviderAccountId>\n" +
            "    <primaryStorageProviderAccountId>2</primaryStorageProviderAccountId>\n" +
            "    <secondaryStorageProviderAccountIds>\n" +
            "      <int>4</int>\n" +
            "      <int>5</int>\n" +
            "    </secondaryStorageProviderAccountIds>\n" +
            "    <secondaryServiceRepositoryIds/>\n" +
            "    <paymentInfoId>-1</paymentInfoId>\n" +
            "    <servicePlan>PROFESSIONAL</servicePlan>\n" +
            "    <status>ACTIVE</status>\n" +
            "  </AccountInfo>\n" +
            "</list>";

        newAcctList =
            "<list>\n" +
            "  <AccountInfo>\n" +
            "    <id>0</id>\n" +
            "    <counter>1</counter>\n" +
            "    <subdomain>usa</subdomain>\n" +
            "    <acctName>USA</acctName>\n" +
            "    <orgName>United States</orgName>\n" +
            "    <department>Government</department>\n" +
            "    <paymentInfoId>-1</paymentInfoId>\n" +
            "    <status>ACTIVE</status>\n" +
            "    <type>FULL</type>\n" +
            "    <serverDetailsId>0</serverDetailsId>\n" +
            "    <accountClusterId>-1</accountClusterId>\n" +
            "  </AccountInfo>\n" +
            "</list>";

        newServerDetailsList =
            "<list>\n" +
            "  <ServerDetails>\n" +
            "    <id>0</id>\n" +
            "    <counter>0</counter>\n" +
            "    <computeProviderAccountId>1</computeProviderAccountId>\n" +
            "    <primaryStorageProviderAccountId>2</primaryStorageProviderAccountId>\n" +
            "    <secondaryStorageProviderAccountIds>\n" +
            "      <int>4</int>\n" +
            "      <int>5</int>\n" +
            "    </secondaryStorageProviderAccountIds>\n" +
            "    <secondaryServiceRepositoryIds/>\n" +
            "    <servicePlan>PROFESSIONAL</servicePlan>\n" +
            "  </ServerDetails>\n" +
            "</list>";
    }

    @Before
    public void setUp() throws Exception {
        converter = new DbConverter(workDir);
        File originalFile =
            new File(workDir, DbConverter.ACCOUNT_INFO_FILENAME);
        FileUtils.writeStringToFile(originalFile, oldAcctList);
    }

    @AfterClass
    public static void shutdown() {
        FileUtils.deleteQuietly(workDir);
    }

    @Test
    public void testConvert() throws Exception {
        // Read old AccountInfo file
        List<OldAccountInfo> oldAccounts = converter.readOldFile();
        assertNotNull(oldAccounts);
        assertEquals(1, oldAccounts.size());

        // Verify that file was read correctly
        OldAccountInfo oldAcct = oldAccounts.get(0);
        assertEquals(0, oldAcct.getId());
        assertEquals(1, oldAcct.getCounter());
        assertEquals("usa", oldAcct.getSubdomain());
        assertEquals("USA", oldAcct.getAcctName());
        assertEquals("United States", oldAcct.getOrgName());
        assertEquals("Government", oldAcct.getDepartment());
        assertEquals(1, oldAcct.getComputeProviderAccountId());
        assertEquals(2, oldAcct.getPrimaryStorageProviderAccountId());
        assertEquals(2, oldAcct.getSecondaryStorageProviderAccountIds().size());
        assertEquals(0, oldAcct.getSecondaryServiceRepositoryIds().size());
        assertEquals(-1, oldAcct.getPaymentInfoId());
        assertEquals(ServicePlan.PROFESSIONAL, oldAcct.getServicePlan());
        assertEquals(AccountInfo.AccountStatus.ACTIVE, oldAcct.getStatus());

        // Write new files
        converter.writeNewFiles(oldAccounts);

        // Verify new AccountInfo list file
        File newAcctFile =
            new File(workDir, DbConverter.ACCOUNT_INFO_FILENAME);
        assertTrue(newAcctFile.exists());
        String newAccts = FileUtils.readFileToString(newAcctFile);
        assertEquals(newAcctList, newAccts);

        // Verify new ServerDetails list file
        File newDetailsFile =
            new File(workDir, DbConverter.SERVER_DETAILS_FILENAME);
        assertTrue(newDetailsFile.exists());
        String newDetails = FileUtils.readFileToString(newDetailsFile);
        assertEquals(newServerDetailsList, newDetails);
    }

}
