
package org.duraspace.mainwebapp.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.serviceprovider.domain.ComputeProviderType;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;

import static org.junit.Assert.assertTrue;

public class CustomerAcctTest {

    private DuraSpaceAcct acct;

    private final String computeAcctId = "test-compute-acct-id";

    private Credential cred;

    private final String username = "username";

    private final String password = "password";

    private List<User> users;

    private User userA;

    private User userB;

    private final String firstnameA = "firstnameA";

    private final String firstnameB = "firstnameB";

    private final String lastnameA = "lastnameA";

    private final String lastnameB = "lastnameB";

    private final ComputeProviderType MOCK_PROVIDER = ComputeProviderType.SUN;

    private final String MOCK_PROVIDER_CLASSNAME =
            "org.duraspace.serviceprovider.mgmt.mock.MockComputeProviderImpl";

    @Before
    public void setUp() throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put(MOCK_PROVIDER.toString(), MOCK_PROVIDER_CLASSNAME);
        ComputeProviderFactory.setIdToClassMap(map);

        cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(password);

        userA = new User();
        userB = new User();

        userA.setFirstname(firstnameA);
        userB.setFirstname(firstnameB);

        userA.setLastname(lastnameA);
        userB.setLastname(lastnameB);

        acct = new DuraSpaceAcct();
        acct.setComputeAcctId(computeAcctId);
        acct.setDuraspaceCredential(cred);
        acct.setUsers(users);

    }

    @After
    public void tearDown() throws Exception {

        acct = null;
        cred = null;
        userA = null;
        userB = null;

    }

    @Test
    public void testAuthenticates() throws Exception {
        Credential credentialBAD = new Credential();
        credentialBAD.setUsername("junk");
        credentialBAD.setPassword("junk");

        assertTrue(!acct.authenticates(credentialBAD));

        Credential credentialGOOD = new Credential();
        credentialGOOD.setUsername(username);
        credentialGOOD.setPassword(password);

        assertTrue(acct.authenticates(credentialGOOD));

        assertTrue(!acct.authenticates(null));
    }

}
