/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Daniel Bernstein
 */
public class CIDRRangeValidatorTest {

    @Test
    public void test() {
        CIDRRangeValidator v = new CIDRRangeValidator();
        //ipv4 range
        assertTrue(v.isValid("127.0.0.1/32", null));
        assertTrue(v.isValid("127.0.0.1", null));
        assertFalse(v.isValid("127.0.0.1/", null));
        assertFalse(v.isValid("xxxx", null));

        //succeeds using proper delimiter
        assertTrue(v.isValid("127.0.0.1/32;198.164.1.1/32", null));
        //fails using improper delimiters
        assertFalse(v.isValid("127.0.0.1/32 198.164.1.1/32", null));
        assertFalse(v.isValid("127.0.0.1/32,198.164.1.1/32", null));

        //fails when one address range is invalid
        assertFalse(v.isValid("127.0.0.1/32;198.164.1.1/", null));

    }

}
