package org.duracloud.mainwebapp.domain.model;

import org.duracloud.common.model.Credential;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.computeprovider.mgmt.ComputeProviderFactory;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerAcctTest {

    private DuraCloudAcct acct;

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

    private final ComputeProviderType MOCK_PROVIDER = ComputeProviderType.RACKSPACE_CLOUDSERVERS;

    private final String MOCK_PROVIDER_CLASSNAME =
            "org.duracloud.serviceprovider.mgmt.mock.MockComputeProviderImpl";

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

        acct = new DuraCloudAcct();
        acct.setComputeAcctId(computeAcctId);
        acct.setDuracloudCredential(cred);
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
