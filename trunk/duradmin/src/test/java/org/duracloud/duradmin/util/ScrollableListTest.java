
package org.duracloud.duradmin.util;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ScrollableListTest {

    private MockScrollableList scrollableList;

    @Before
    public void setUp() throws Exception {
        scrollableList = new MockScrollableList();
        scrollableList.setMaxResultsPerPage(10);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetResultList() {
        List<String> results = scrollableList.getResultList();
        assertNotNull(results);
        assertEquals("list-value-0", results.get(0));
    }

    @Test
    public void testNextPrevious() {
        assertEquals(false, scrollableList.isPreviousAvailable());
        assertEquals(true, scrollableList.isNextAvailable());
        scrollableList.next();
        assertEquals("list-value-10", scrollableList.getResultList().get(0));

        assertEquals(true, scrollableList.isPreviousAvailable());
        scrollableList.previous();
        assertEquals("list-value-0", scrollableList.getResultList().get(0));
        assertEquals(false, scrollableList.isPreviousAvailable());

    }

    @Test
    public void testLast() {
        for (int i = 0; i < 9; i++) {
            scrollableList.next();
        }

        assertEquals("list-value-90", scrollableList.getResultList().get(0));
        assertEquals(true, scrollableList.isNextAvailable());

        scrollableList.next();
        assertEquals(false, scrollableList.isNextAvailable());
        assertEquals(1, scrollableList.getResultList().size());
        scrollableList.next();
        assertEquals(true, scrollableList.isPreviousAvailable());
    }

    @Test
    public void testFirst() {
        for (int i = 0; i < 3; i++) {
            scrollableList.next();
        }

        assertNotSame("list-value-0", scrollableList.getResultList().get(0));
        scrollableList.first();
        assertEquals("list-value-0", scrollableList.getResultList().get(0));

    }

    private class MockScrollableList
            extends ScrollableList<String> {

        @Override
        protected List<String> getData(String currentMarker) throws DataRetrievalException {
            List<String> list = new LinkedList<String>();
            for (int i = 0; i < 101; i++) {
                list.add("list-value-" + i);
            }

            int maxResults = new Long(getMaxResultsPerPage()).intValue();
            if (currentMarker != null) {
                int index = list.indexOf(currentMarker);
                //if last element in list
                if (index == list.size() - 1) {
                    return new LinkedList<String>();
                } else {
                    return list.subList(index + 1, Math.min(index + 1
                            + maxResults, list.size()));
                }
            } else {
                if (isPreviousAvailable()) {
                    return new LinkedList<String>();
                } else {
                    return list.subList(0, Math.min(maxResults, list.size()));
                }
            }
        }

    }
}
