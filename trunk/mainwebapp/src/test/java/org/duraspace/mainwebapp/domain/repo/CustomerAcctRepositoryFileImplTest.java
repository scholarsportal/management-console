
package org.duraspace.mainwebapp.domain.repo;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.mainwebapp.domain.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerAcctRepositoryFileImplTest {

    private CustomerAcctRepository repo;

    private final String testRepoLoc =
            "src/test/java/org/duraspace/mainwebapp/domain/repo/test-repo.xml";

    private final int NUM_ACCTS = 3;

    private Properties custAcctProps;

    private CustomerAcct custAcct0;

    private CustomerAcct custAcct1;

    private CustomerAcct custAcct2;

    private final String computeAcctId0 = "computeAcctId0";

    private final String computeAcctId1 = "computeAcctId1";

    private final String computeAcctId2 = "computeAcctId2";

    private Credential credential0;

    private Credential credential1;

    private Credential credential2;

    private User userA;

    private User userB;

    private User userC;

    private List<User> users;

    private final String id0 = "id0";

    private final String id1 = "id1";

    private final String id2 = "id2";

    private final String username0 = "username0";

    private final String password0 = "password0";

    private final String username1 = "username1";

    private final String password1 = "password1";

    private final String username2 = "username2";

    private final String password2 = "password2";

    private final String usernameNEW = "usernameNEW";

    private final String passwordNEW = "passwordNEW";

    private final String firstnameA = "firstnameA";

    private final String lastnameA = "lastnameA";

    private final String firstnameB = "firstnameB";

    private final String lastnameB = "lastnameB";

    private final String firstnameC = "firstnameC";

    private final String lastnameC = "lastnameC";

    private final String firstnameNEW = "firstnameNEW";

    private final String lastnameNEW = "lastNameNEW";

    @Before
    public void setUp() throws Exception {

        credential0 = new Credential();
        credential1 = new Credential();
        credential2 = new Credential();
        userA = new User();
        userB = new User();
        userC = new User();
        users = new ArrayList<User>();
        custAcct0 = new CustomerAcct();
        custAcct1 = new CustomerAcct();
        custAcct2 = new CustomerAcct();

        repo = new CustomerAcctRepositoryFileImpl();
        custAcctProps = new Properties();
        createInitialRepository();
    }

    private void createInitialRepository() throws Exception {

        credential0.setUsername(username0);
        credential0.setPassword(password0);

        credential1.setUsername(username1);
        credential1.setPassword(password1);

        credential2.setUsername(username2);
        credential2.setPassword(password2);

        userA.setFirstname(firstnameA);
        userA.setLastname(lastnameA);

        userB.setFirstname(firstnameB);
        userB.setLastname(lastnameB);

        userC.setFirstname(firstnameC);
        userC.setLastname(lastnameC);

        users.add(userA);
        users.add(userB);
        users.add(userC);

        custAcct0.setComputeAcctId(computeAcctId0);
        custAcct0.setDuraspaceCredential(credential0);
        custAcct0.setUsers(users);
        custAcct0.setId(id0);

        custAcct1.setComputeAcctId(computeAcctId1);
        custAcct1.setDuraspaceCredential(credential1);
        custAcct1.setUsers(users);
        custAcct1.setId(id1);

        custAcct2.setComputeAcctId(computeAcctId2);
        custAcct2.setDuraspaceCredential(credential2);
        custAcct2.setUsers(users);
        custAcct2.setId(id2);

        // Build the actual repository.
        custAcctProps.setProperty(CustomerAcctRepositoryFileImpl.REPO_LOCATION,
                                  testRepoLoc);
        repo.setProperties(custAcctProps);

        repo.saveCustomerAcct(custAcct0);
        repo.saveCustomerAcct(custAcct1);
        repo.saveCustomerAcct(custAcct2);
    }

    @After
    public void tearDown() throws Exception {

        credential0 = null;
        credential1 = null;
        credential2 = null;
        userA = null;
        userB = null;
        userC = null;
        users = null;
        custAcct0 = null;
        custAcct1 = null;
        custAcct2 = null;

        repo = null;
        custAcctProps = null;

        removeTestRepository();
    }

    private void removeTestRepository() {
        File toRemove = new File(testRepoLoc);
        toRemove.delete();
    }

    @Test
    public void testGetCustomerAcct() throws Exception {
        verifyInitialRepoContents();

        Credential entryCred = new Credential();
        entryCred.setUsername(username0);
        entryCred.setPassword(password0);

        CustomerAcct acct = repo.findCustomerAcct(entryCred);
        assertNotNull(acct);

        Credential cred = acct.getDuraspaceCredential();
        assertNotNull(cred);

        String uname = cred.getUsername();
        String pword = cred.getPassword();
        assertNotNull(uname);
        assertNotNull(pword);
        assertTrue(uname.equals(username0));
        assertTrue(pword.equals(password0));

        String id = acct.getId();
        assertNotNull(id);
        assertTrue(id.equals(id0));

        List<User> users = acct.getUsers();
        assertNotNull(users);
        assertTrue(users.size() == 3);

        String caId = acct.getComputeAcctId();
        assertNotNull(caId);
        assertEquals(caId, computeAcctId0);

    }

    private void verifyInitialRepoContents() throws Exception {
        int numAccts = repo.getNumCustomerAccts();
        assertTrue("There should be " + NUM_ACCTS + " accts: " + numAccts,
                   numAccts == NUM_ACCTS);

        assertNotNull(repo.findCustomerAcct(credential0));
        assertNotNull(repo.findCustomerAcct(credential1));
        assertNotNull(repo.findCustomerAcct(credential2));

    }

    @Test
    public void testSaveCustomerAcct() throws Exception {
        verifyInitialRepoContents();

        CustomerAcct acct = createCustomerAcct();

        verifyNewAcctNotExist(acct.getDuraspaceCredential());

        repo.saveCustomerAcct(acct);

        verifyNewAcctAdded(acct.getDuraspaceCredential());
    }

    private CustomerAcct createCustomerAcct() {
        CustomerAcct acct = new CustomerAcct();

        Credential credentialNEW = new Credential();
        credentialNEW.setUsername(usernameNEW);
        credentialNEW.setPassword(passwordNEW);

        User user = new User();
        user.setFirstname(firstnameNEW);
        user.setLastname(lastnameNEW);

        List<User> users = new ArrayList<User>();
        users.add(user);

        acct.setComputeAcctId(computeAcctId0);
        acct.setDuraspaceCredential(credentialNEW);
        acct.setUsers(users);
        return acct;
    }

    private void verifyNewAcctNotExist(Credential cred) {
        try {
            repo.findCustomerAcct(cred);
            fail("Exception should have thrown!!");
        } catch (Exception e) {

        }

    }

    private void verifyNewAcctAdded(Credential cred) throws Exception {
        int numAccts = repo.getNumCustomerAccts();
        assertTrue("There should be " + NUM_ACCTS + 1 + " accts: " + numAccts,
                   numAccts == NUM_ACCTS + 1);

        assertNotNull(repo.findCustomerAcct(credential0));
        assertNotNull(repo.findCustomerAcct(credential1));
        assertNotNull(repo.findCustomerAcct(credential2));

        assertNotNull(repo.findCustomerAcct(cred));
    }

}
