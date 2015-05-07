package org.duracloud.account.annotation;

import static junit.framework.Assert.*;

import org.junit.Test;

public class CIDRRangeValidatorTest {

    @Test
    public void test() {
        CIDRRangeValidator v = new CIDRRangeValidator();
        
        /**
         * Thanks to Mark Hatton for the CIDR range regex
         * http://blog.markhatton.co.uk/2011/03/15/regular-expressions-for-ip-addresses-cidr-ranges-and-hostnames/
         */
        //ipv4 range
        assertTrue(v.isValid("127.0.0.1/32", null));
        assertFalse(v.isValid("127.0.0.1", null));
        //succeeds using proper delimiter
        assertTrue(v.isValid("127.0.0.1/32;198.164.1.1/32", null));
        //fails using improper delimiters
        assertFalse(v.isValid("127.0.0.1/32 198.164.1.1/32", null));
        assertFalse(v.isValid("127.0.0.1/32,198.164.1.1/32", null));

        //fails when one address range is invalid
        assertFalse(v.isValid("127.0.0.1/32;198.164.1.1", null));

    }

}
