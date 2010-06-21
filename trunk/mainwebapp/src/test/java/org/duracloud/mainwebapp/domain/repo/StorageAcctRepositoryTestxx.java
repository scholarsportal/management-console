/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;


public class StorageAcctRepositoryTestxx {

//    private StorageAcctRepositoryFileImpl repo;
//
//    private final String testRepoLoc = "testStorageAcctRepo.xml";
//
//    private final String owner0 = "owner0";
//
//    private final String owner1 = "owner1";
//
//    private Credential credA;
//
//    private Credential credB;
//
//    private Credential credC;
//
//    private final String usernameA = "usernameA";
//
//    private final String usernameB = "usernameB";
//
//    private final String usernameC = "usernameC";
//
//    private final String passwordA = "passwordA";
//
//    private final String passwordB = "passwordB";
//
//    private final String passwordC = "passwordC";
//
//    private final String providerIdX = "amazon-s3-id0";
//
//    private final String providerIdZ = "ms-azure-id0";
//
//    private final String providerTypeX = "amazon-s3";
//
//    private final String providerTypeZ = "ms-azure";
//
//    @Before
//    public void setUp() throws Exception {
//        repo = new StorageAcctRepositoryFileImpl();
//
//        Properties props = new Properties();
//        props.put(StorageAcctRepositoryFileImpl.REPO_LOCATION, testRepoLoc);
//
//        repo.setProperties(props);
//
//        credA = new Credential();
//        credB = new Credential();
//        credC = new Credential();
//
//        credA.setUsername(usernameA);
//        credA.setPassword(passwordA);
//        credB.setUsername(usernameB);
//        credB.setPassword(passwordB);
//        credC.setUsername(usernameC);
//        credC.setPassword(passwordC);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        repo = null;
//        credA = null;
//        credB = null;
//        credC = null;
//    }
//
//    @SuppressWarnings("unused")
//    private void populateFileDatabase() throws Exception {
//        Set<StorageAcct> accts = new HashSet<StorageAcct>();
//
//        StorageAcct acctA = new StorageAcct();
//        acctA.setOwnerId(owner0);
//        acctA.setPrimary(true);
//        acctA.setStorageProviderCred(credA);
//        acctA.setStorageProviderId(providerIdX);
//        acctA.setStorageProviderType(providerTypeX);
//        accts.add(acctA);
//
//        StorageAcct acctB = new StorageAcct();
//        acctB.setOwnerId(owner0);
//        acctB.setPrimary(false);
//        acctB.setStorageProviderCred(credB);
//        acctB.setStorageProviderId(providerIdZ);
//        acctB.setStorageProviderType(providerTypeZ);
//        accts.add(acctB);
//
//        StorageAcct acctC = new StorageAcct();
//        acctC.setOwnerId(owner1);
//        acctC.setPrimary(true);
//        acctC.setStorageProviderCred(credC);
//        acctC.setStorageProviderId(providerIdX);
//        acctC.setStorageProviderType(providerTypeX);
//        accts.add(acctC);
//    }
//
//    @Test
//    public void testFindStorageAccts() throws Exception {
//        // Verify first owner's accounts.
//        List<StorageAcct> accts0 = repo.findStorageAccts(owner0);
//        assertNotNull(accts0);
//        assertTrue(accts0.size() == 2);
//
//        for (StorageAcct acct : accts0) {
//            assertTrue(owner0.equals(acct.getOwnerId()));
//            Credential cred = acct.getStorageProviderCred();
//            assertNotNull(cred);
//
//            if (acct.getIsPrimary()) {
//                assertTrue(providerIdX.equals(acct.getStorageProviderId()));
//                assertTrue(providerTypeX.equals(acct.getStorageProviderType()));
//                assertTrue(usernameA.equals(cred.getUsername()));
//                assertTrue(passwordA.equals(cred.getPassword()));
//            } else {
//                assertTrue(providerIdZ.equals(acct.getStorageProviderId()));
//                assertTrue(providerTypeZ.equals(acct.getStorageProviderType()));
//                assertTrue(usernameB.equals(cred.getUsername()));
//                assertTrue(passwordB.equals(cred.getPassword()));
//            }
//        }
//
//        // Verify second owner's accounts.
//        List<StorageAcct> accts1 = repo.findStorageAccts(owner1);
//        assertNotNull(accts1);
//        assertTrue(accts1.size() == 1);
//
//        StorageAcct acct = accts1.get(0);
//        assertTrue(owner1.equals(acct.getOwnerId()));
//        Credential cred = acct.getStorageProviderCred();
//        assertNotNull(cred);
//
//        assertTrue(acct.getIsPrimary());
//        assertTrue(providerIdX.equals(acct.getStorageProviderId()));
//        assertTrue(usernameC.equals(cred.getUsername()));
//        assertTrue(passwordC.equals(cred.getPassword()));
//
//    }
//
//    @Test
//    public void testSaveStorageAcct() {
//
//        try {
//            repo.saveStorageAcct(new StorageAcct());
//            fail("Method should not currently be supported!");
//        } catch (Exception e) {
//            assertTrue(e instanceof UnsupportedOperationException);
//        }
//    }

}
