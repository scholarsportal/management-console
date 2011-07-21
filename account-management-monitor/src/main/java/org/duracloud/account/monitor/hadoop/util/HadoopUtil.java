/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util;

import org.duracloud.account.monitor.hadoop.domain.HadoopServiceInfo;
import org.duracloud.account.monitor.error.HadoopNotActivatedException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This interface defines the contract for a utility for querying the Hadoop
 * resources of a given storage account.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public interface HadoopUtil {

    /**
     * This enum defines the states of a Hadoop service, and translates between
     * these states and the ones used internally by Hadoop.
     */
    public enum STATE {
        RUNNING(new String[]{"STARTING",
                             "BOOTSTRAPPING",
                             "RUNNING",
                             "WAITING"}),
        COMPLETED(new String[]{"SHUTTING_DOWN",
                               "COMPLETED",
                               "TERMINATED",
                               "FAILED"}),
        UNKNOWN(new String[]{});

        private List<String> flowStates;

        private STATE(String[] flowStates) {
            this.flowStates = Arrays.asList(flowStates);
        }

        public List<String> asJobFlowStates() {
            return flowStates;
        }

        public static STATE fromString(String text) {
            for (STATE value : values()) {
                for (String state : value.flowStates) {
                    if (state.equals(text)) {
                        return value;
                    }
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * This method returns the services created after the arg date and of the
     * arg state.
     *
     * @param date  of services
     * @param state of services
     * @return set of service info
     */
    public Set<HadoopServiceInfo> getServicesCreatedAfter(Date date,
                                                          STATE state);

    /**
     * This method verifies that this HadoopUtil object is ready to use.
     *
     * @throws HadoopNotActivatedException if the Hadoop framework service has
     *                                     not been activated for the account
     */
    public void verifyActivated() throws HadoopNotActivatedException;
}
