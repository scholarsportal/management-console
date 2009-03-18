
package org.duraspace.mainwebapp.domain.repo;


public class ComputeAcctRepositoryFileImplTestxx {

//    private ComputeAcctRepositoryFileImpl repo;
//
//    private final String testRepoLocKey =
//            DuraSpaceAcctRepositoryFileImpl.REPO_LOCATION;
//
//    private final String testRepoLoc =
//            "src/test/java/org/duraspace/mainwebapp/domain/repo/testComputeRepo.xml";
//
//    private MockServiceProviderProperties props0;
//
//    private Properties computeAcctProps;
//
//    private final int NUM_ACCTS = 3;
//
//    private ComputeAcct acct0;
//
//    private ComputeAcct acct1;
//
//    private ComputeAcct acct2;
//
//    private Credential credential0;
//
//    private Credential credential1;
//
//    private Credential credential2;
//
//    private final String id0 = "id0";
//
//    private final String id1 = "id1";
//
//    private final String id2 = "id2";
//
//    private final String idNEW = "idNEW";
//
//    private final String namespace0 = "namespace0";
//
//    private final String username0 = "username0";
//
//    private final String password0 = "password0";
//
//    private final String username1 = "username1";
//
//    private final String password1 = "password1";
//
//    private final String username2 = "username2";
//
//    private final String password2 = "password2";
//
//    private final String usernameNEW = "usernameNEW";
//
//    private final String passwordNEW = "passwordNEW";
//
//    private final String computeProviderId = "provider";
//
//    private final String propA = "propA";
//
//    private final String propB = "propB";
//
//    private final String propC = "propC";
//
//    private final String instanceId0 = "instanceId0";
//
//    private final String instanceId1 = "instanceId1";
//
//    private final String instanceId2 = "instanceId2";
//
//    private final String instanceIdNEW = "instanceIdNEW";
//
//    @Before
//    public void setUp() throws Exception {
//        repo = new ComputeAcctRepositoryFileImpl();
//
//        props0 = new MockServiceProviderProperties();
//        computeAcctProps = new Properties();
//
//        acct0 = new ComputeAcct();
//        acct1 = new ComputeAcct();
//        acct2 = new ComputeAcct();
//        credential0 = new Credential();
//        credential1 = new Credential();
//        credential2 = new Credential();
//
//        createInitialRepository();
//    }
//
//    private void createInitialRepository() throws Exception {
//
//        credential0.setUsername(username0);
//        credential0.setPassword(password0);
//
//        credential1.setUsername(username1);
//        credential1.setPassword(password1);
//
//        credential2.setUsername(username2);
//        credential2.setPassword(password2);
//
//        props0.setProp0(propA);
//        props0.setProp1(propB);
//        props0.setProp2(propC);
//
//        acct0.setId(id0);
//        acct0.setProps(props0);
//        acct0.setNamespace(namespace0);
//        acct0.setComputeProviderId(computeProviderId);
//        acct0.setComputeCredential(credential0);
//        acct0.setInstanceId(instanceId0);
//
//        acct1.setId(id1);
//        acct1.setProps(props0);
//        acct1.setNamespace(namespace0);
//        acct1.setComputeProviderId(computeProviderId);
//        acct1.setComputeCredential(credential1);
//        acct1.setInstanceId(instanceId1);
//
//        acct2.setId(id2);
//        acct2.setProps(props0);
//        acct2.setNamespace(namespace0);
//        acct2.setComputeProviderId(computeProviderId);
//        acct2.setComputeCredential(credential2);
//        acct2.setInstanceId(instanceId2);
//
//        // Build the actual repository.
//        computeAcctProps.setProperty(testRepoLocKey, testRepoLoc);
//        repo.setProperties(computeAcctProps);
//
//        repo.saveComputeAcct(acct0);
//        repo.saveComputeAcct(acct1);
//        repo.saveComputeAcct(acct2);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        props0 = null;
//
//        acct0 = null;
//        acct1 = null;
//        acct2 = null;
//        credential0 = null;
//        credential1 = null;
//        credential2 = null;
//
//        repo = null;
//        computeAcctProps = null;
//
//        removeTestRepository();
//    }
//
//    private void removeTestRepository() {
//        File toRemove = new File(testRepoLoc);
//        toRemove.delete();
//    }
//
//    @Test
//    public void testFindComputeAcct() throws Exception {
//        verifyInitialRepoContents();
//
//        ComputeAcct acct = repo.findComputeAcct(id0);
//        assertNotNull(acct);
//
//        Credential cred = acct.getComputeCredential();
//        assertNotNull(cred);
//
//        String uname = cred.getUsername();
//        String pword = cred.getPassword();
//        assertNotNull(uname);
//        assertNotNull(pword);
//        assertTrue(uname.equals(username0));
//        assertTrue(pword.equals(password0));
//
//        String id = acct.getId();
//        assertNotNull(id);
//        assertTrue(id.equals(id0));
//
//        String nspace = acct.getNamespace();
//        assertNotNull(nspace);
//        assertTrue(nspace.equals(namespace0));
//
//        MockServiceProviderProperties props =
//                (MockServiceProviderProperties) acct.getProps();
//        assertNotNull(props);
//
//        String p0 = props.getProp0();
//        assertNotNull(p0);
//        assertTrue(p0.equals(propA));
//
//        String p1 = props.getProp1();
//        assertNotNull(p1);
//        assertTrue(p1.equals(propB));
//
//        String p2 = props.getProp2();
//        assertNotNull(p2);
//        assertTrue(p2.equals(propC));
//    }
//
//    private void verifyInitialRepoContents() throws Exception {
//        int numAccts = repo.getNumComputeAccts();
//        assertEquals(NUM_ACCTS, numAccts);
//
//        assertNotNull(repo.findComputeAcct(id0));
//        assertNotNull(repo.findComputeAcct(id1));
//        assertNotNull(repo.findComputeAcct(id2));
//
//    }
//
//    @Test
//    public void testGetNumComputeAccts() throws Exception {
//        assertEquals(NUM_ACCTS, repo.getNumComputeAccts());
//    }
//
//    @Test
//    public void testSetProperties() {
//        // Bad properties.
//        Properties propsX = new Properties();
//        propsX.put("junk", "junk");
//        try {
//            repo.setProperties(propsX);
//            fail("Should throw exception if no 'repoLocation' prop.");
//        } catch (Exception e) {
//        }
//
//        // Good properties.
//        try {
//            repo.setProperties(computeAcctProps);
//        } catch (Exception e) {
//            fail("Should not throw exception: " + e.getMessage());
//        }
//    }
//
//    public void testUpdateComputeAcct() throws Exception {
//        verifyInitialRepoContents();
//        ComputeAcct acct = repo.findComputeAcct(id1);
//        assertNotNull(acct);
//
//        // check original id.
//        String idOrig = acct.getInstanceId();
//        assertNotNull(idOrig);
//        assertEquals(idOrig, instanceId1);
//
//        // update id.
//        acct.setInstanceId(instanceIdNEW);
//        repo.saveComputeAcct(acct);
//
//        // check update.
//        assertTrue(repo.getNumComputeAccts() == NUM_ACCTS);
//        acct = repo.findComputeAcct(id1);
//        assertNotNull(acct);
//
//        String idUpdated = acct.getInstanceId();
//        assertNotNull(idUpdated);
//        assertEquals(idUpdated, instanceIdNEW);
//
//        // update id.
//        acct.setInstanceId(null);
//        repo.saveComputeAcct(acct);
//
//        // check update.
//        assertTrue(repo.getNumComputeAccts() == NUM_ACCTS);
//        acct = repo.findComputeAcct(id1);
//        assertNotNull(acct);
//
//        String idUpdated2 = acct.getInstanceId();
//        assertTrue(idUpdated2 == null);
//
//    }
//
//    @Test
//    public void testSaveComputeAcct() throws Exception {
//        verifyInitialRepoContents();
//
//        ComputeAcct acct = createComputeAcct();
//
//        verifyNewAcctNotExist(acct.getId());
//
//        repo.saveComputeAcct(acct);
//
//        verifyNewAcctAdded(acct.getId());
//    }
//
//    private ComputeAcct createComputeAcct() {
//        ComputeAcct acct = new ComputeAcct();
//        acct.setId(idNEW);
//        acct.setProps(props0);
//        acct.setNamespace(namespace0);
//        acct.setComputeProviderId(computeProviderId);
//        acct.setInstanceId(instanceIdNEW);
//
//        Credential credentialNEW = new Credential();
//        credentialNEW.setUsername(usernameNEW);
//        credentialNEW.setPassword(passwordNEW);
//
//        acct.setComputeCredential(credentialNEW);
//        return acct;
//    }
//
//    private void verifyNewAcctNotExist(String acctId) {
//        try {
//            repo.findComputeAcct(acctId);
//            fail("Exception should have thrown!!");
//        } catch (Exception e) {
//        }
//
//    }
//
//    private void verifyNewAcctAdded(String acctId) throws Exception {
//        int numAccts = repo.getNumComputeAccts();
//        assertTrue("There should be " + NUM_ACCTS + 1 + " accts: " + numAccts,
//                   numAccts == NUM_ACCTS + 1);
//
//        assertNotNull(repo.findComputeAcct(id0));
//        assertNotNull(repo.findComputeAcct(id1));
//        assertNotNull(repo.findComputeAcct(id2));
//
//        ComputeAcct acct = repo.findComputeAcct(acctId);
//        assertNotNull(acct);
//        assertNotNull(acct.getInstanceId());
//        assertEquals(acct.getInstanceId(), instanceIdNEW);
//    }

}
