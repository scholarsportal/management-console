/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.duracloud.account.db.amazonsimple.converter.BaseDomainConverter.DELIM;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class BaseDomainConverterTest {

    private static final int testId = 100;
    private static Set<Integer> testIds = null;

    @BeforeClass
    public static void initialize() throws Exception {
        testIds = new HashSet<Integer>();
        testIds.add(new Integer(1));
        testIds.add(new Integer(2));
    }

    @Test
    public void testAsString() {
        BaseDomainConverter converter = getConverter();

        assertEquals("100", converter.asString(testId));

        String instanceIdsString = converter.idsAsString(testIds);
        assertEquals(2, instanceIdsString.split(DELIM).length);
        assertTrue(instanceIdsString.contains("1"));
        assertTrue(instanceIdsString.contains("2"));
    }

    @Test
    public void testFromString() {
        BaseDomainConverter converter = getConverter();

        String instIdString = "1" + DELIM + "2";
        Set<Integer> instIdSet = converter.idsFromString(instIdString);
        assertEquals(testIds, instIdSet);
    }

    private BaseDomainConverter getConverter() {
        return new BaseConverter();
    }

    private class BaseConverter extends BaseDomainConverter {
    }
}
