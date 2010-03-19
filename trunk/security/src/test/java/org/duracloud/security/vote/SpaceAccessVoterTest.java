package org.duracloud.security.vote;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;

/**
 * @author Andrew Woods
 *         Date: Mar 19, 2010
 */
public class SpaceAccessVoterTest {
    @Before
    public void setUp() {
        // Add your code here
    }

    @After
    public void tearDown() {
        // Add your code here
    }

    @Test
    public void testVote() {
        SpaceAccessVoter voter = new SpaceAccessVoter();

        Authentication auth = null;
        Object resource = null;
        ConfigAttributeDefinition config = null;
        int decision = voter.vote(auth, resource, config);
        // TODO: complete this test
    }
}
