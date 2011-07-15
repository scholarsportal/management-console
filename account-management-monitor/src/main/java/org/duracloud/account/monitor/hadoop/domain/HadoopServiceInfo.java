/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.domain;

import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.duracloud.common.util.DateUtil;

import java.util.Date;

/**
 * This class holds the state of a single Hadoop service run.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopServiceInfo {

    private String name;
    private String state;
    private Date startTime;
    private Date stopTime;

    public HadoopServiceInfo(String name,
                             String state,
                             Date startTime,
                             Date stopTime) {
        this.name = name;
        this.state = state;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    /**
     * This method returns the state of the service (e.g. RUNNING, COMPLETED)
     *
     * @return state
     */
    public HadoopUtil.STATE getState() {
        return HadoopUtil.STATE.fromString(state);
    }

    /**
     * This method returns the name of the service.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns true if the elapsed time of the service run exceeds
     * the arg number of days.
     *
     * @param numDays against which the check is performed
     * @return true if the elapsed time exceeds the arg number of days
     */
    public boolean elapsedExceedsDays(int numDays) {
        if (null != startTime) {
            long millisPerDay = 1000 * 60 * 60 * 24;
            return elapsedMillis() > (numDays * millisPerDay);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hadoop Service (");
        sb.append(name);
        sb.append(") ");
        sb.append(state);

        if (null != startTime) {
            sb.append(" started: ");
            sb.append(DateUtil.convertToStringMid(startTime.getTime()));

            sb.append(" elapsed: ");
            sb.append(getElapsedTime());
        }

        return sb.toString();
    }

    private String getElapsedTime() {
        final long SECOND = 1000;
        final long MINUTE = 60 * SECOND;
        final long HOUR = 60 * MINUTE;
        final long DAY = 24 * HOUR;

        long elapsed = elapsedMillis();

        long days = elapsed / DAY;
        long daysMillis = days * DAY;

        long hours = (elapsed - daysMillis) / HOUR;
        long hoursMillis = hours * HOUR;

        long mins = (elapsed - daysMillis - hoursMillis) / MINUTE;
        long minsMillis = mins * MINUTE;

        long secs = (elapsed - daysMillis - hoursMillis - minsMillis) / SECOND;

        StringBuilder sb = new StringBuilder();
        sb.append(days);
        sb.append(days == 1 ? "-day" : "-days");
        sb.append(" ");
        sb.append(hours);
        sb.append(hours == 1 ? "-hour" : "-hours");
        sb.append(" ");
        sb.append(mins);
        sb.append(mins == 1 ? "-min" : "-mins");
        sb.append(" ");
        sb.append(secs);
        sb.append(secs == 1 ? "-sec" : "-secs");

        return sb.toString();
    }

    private long elapsedMillis() {
        Date endTime = null == stopTime ? new Date() : stopTime;
        return endTime.getTime() - startTime.getTime();
    }

}
