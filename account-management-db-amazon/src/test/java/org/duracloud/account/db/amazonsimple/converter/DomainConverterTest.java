/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple.converter;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

import java.util.List;

import org.duracloud.account.common.domain.Identifiable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public abstract class DomainConverterTest<T extends Identifiable> {

    private DomainConverter<T> converter;

    private T testItem;
    private List<Attribute> testAtts;

    @Before
    public void setUp() throws Exception {
        converter = createConverter();
        testItem = createTestItem();
        testAtts = createTestAttributes();
    }

    protected abstract DomainConverter<T> createConverter();

    protected abstract T createTestItem();

    protected abstract List<Attribute> createTestAttributes();

    protected abstract void verifyItem(T item);

    @Test
    public void testToAttributesAndIncrement() throws Exception {
        List<ReplaceableAttribute> atts = converter.toAttributesAndIncrement(
            testItem);
        Assert.assertNotNull(atts);

        Assert.assertEquals(testAtts.size(), atts.size());
        String name;
        String value;
        boolean isReplace;
        for (ReplaceableAttribute att : atts) {
            name = att.getName();
            value = att.getValue();
            isReplace = att.getReplace();

            boolean found = false;
            String testName;
            String testValue;
            for (Attribute testAtt : testAtts) {
                testName = testAtt.getName();
                testValue = testAtt.getValue();

                if (testName.equals(COUNTER_ATT)) {
                    testValue = padded(Integer.parseInt(testValue) + 1);
                }

                if (testName.equals(name)) {
                    Assert.assertEquals(testValue, value);
                    Assert.assertTrue(isReplace);
                    found = true;
                }
            }
            Assert.assertTrue("attribute not found: " + name, found);
        }

    }

    @Test
    public void testFromAttributes() throws Exception {
        T item = converter.fromAttributes(testAtts, testItem.getId());
        verifyItem(item);
    }

}
