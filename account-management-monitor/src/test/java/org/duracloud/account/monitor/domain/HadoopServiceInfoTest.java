/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.domain;

import org.duracloud.account.monitor.util.HadoopUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrew Woods
 *         Date: 7/12/11
 */
public class HadoopServiceInfoTest {

    private final static String NAME = "service-name";

    @Test
    public void testGetState() throws Exception {
        Date now = new Date();

        for (HadoopUtil.STATE state : HadoopUtil.STATE.values()) {
            for (String flowState : state.asJobFlowStates()) {
                HadoopServiceInfo info = new HadoopServiceInfo(NAME,
                                                               flowState,
                                                               now,
                                                               null);
                Assert.assertEquals(state, info.getState());
            }
        }
    }

    @Test
    public void testToStringNull() throws Exception {
        Date now = new Date();
        String flowState = HadoopUtil.STATE.RUNNING.name();
        HadoopServiceInfo info = new HadoopServiceInfo(NAME,
                                                       flowState,
                                                       now,
                                                       null);
        Assert.assertNotNull(info.toString());
    }

    @Test
    public void testElapsedExceedsDays() {
        int daysAgo = 5;
        Date date = getDateWithOffset(-daysAgo);
        String flowState = HadoopUtil.STATE.RUNNING.name();
        HadoopServiceInfo info = new HadoopServiceInfo(NAME,
                                                       flowState,
                                                       date,
                                                       null);

        for (int i = 0; i < daysAgo; ++i) {
            Assert.assertTrue("i=" + i, info.elapsedExceedsDays(i));
        }
        Assert.assertFalse(info.elapsedExceedsDays(daysAgo));
    }

    private Date getDateWithOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DATE, offset);
        calendar.roll(Calendar.HOUR, 1);
        return calendar.getTime();
    }

    @Test
    public void testToString() throws Exception {
        doTestToString(7, 0, 0, 0);
        doTestToString(7, 7, 0, 0);
        doTestToString(7, 7, 7, 0);
        doTestToString(7, 7, 7, 7);
        doTestToString(0, 7, 7, 7);
        doTestToString(0, 0, 7, 7);
        doTestToString(0, 0, 0, 7);
        doTestToString(0, 0, 0, 0);
    }

    private void doTestToString(int daysAgo,
                                int hoursAgo,
                                int minsAgo,
                                int secsAgo) throws Exception {
        Calendar ending = juneTenth2011();
        Calendar beginning = (Calendar) ending.clone();

        beginning.roll(Calendar.DATE, -daysAgo);
        beginning.roll(Calendar.HOUR, -hoursAgo);
        beginning.roll(Calendar.MINUTE, -minsAgo);
        beginning.roll(Calendar.SECOND, -secsAgo);

        String flowState = HadoopUtil.STATE.RUNNING.name();
        HadoopServiceInfo info = new HadoopServiceInfo(NAME,
                                                       flowState,
                                                       beginning.getTime(),
                                                       ending.getTime());

        String text = info.toString();
        Assert.assertNotNull(text);

        String elapsed = text.substring(text.indexOf("elapsed: "));

        final String dayTag = "-day";
        final String hourTag = "-hour";
        final String minTag = "-min";
        final String secTag = "-sec";

        int daysIndex = elapsed.indexOf(dayTag);
        int hoursIndex = elapsed.indexOf(hourTag);
        int minsIndex = elapsed.indexOf(minTag);
        int secsIndex = elapsed.indexOf(secTag);

        verifyUnit(elapsed, 0, daysIndex, daysAgo);
        verifyUnit(elapsed, daysIndex, hoursIndex, hoursAgo);
        verifyUnit(elapsed, hoursIndex, minsIndex, minsAgo);
        verifyUnit(elapsed, minsIndex, secsIndex, secsAgo);
    }

    private void verifyUnit(String text,
                            int startIndex,
                            int endIndex,
                            int expected) {
        final String space = " ";
        int spaceIndex = text.indexOf(space, startIndex);
        String unit = text.substring(spaceIndex + 1, endIndex);
        Assert.assertNotNull(unit);
        Assert.assertEquals(text, expected, Integer.parseInt(unit));
    }

    private Calendar juneTenth2011() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1310579045000L); // start with a fixed time.

        calendar.roll(Calendar.SECOND, 10);
        calendar.roll(Calendar.MINUTE, 10);
        calendar.roll(Calendar.HOUR_OF_DAY, 10);
        calendar.roll(Calendar.DAY_OF_MONTH, 10);
        calendar.roll(Calendar.MONTH, Calendar.JUNE);
        calendar.roll(Calendar.YEAR, 2011);
        return calendar;
    }
}
