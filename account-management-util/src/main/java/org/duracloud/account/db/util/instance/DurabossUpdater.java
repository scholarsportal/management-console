/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance;

import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.common.web.RestHttpHelper;

/**
 * This interface defines the contract for classes that manage updating the
 * DuraBoss.
 *
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public interface DurabossUpdater {

    /**
     * This method performs the actions required for starting DuraBoss actions.
     *
     * @param host           of DuraBoss
     * @param durabossConfig of DuraBoss
     * @param restHelper     for connecting to DuraBoss
     */
    public void startDuraboss(String host,
                              DurabossConfig durabossConfig,
                              RestHttpHelper restHelper);

    /**
     * This method performs the actions required for stopping DuraBoss actions.
     *
     * @param host           of DuraBoss
     * @param durabossConfig of DuraBoss
     * @param restHelper     for connecting to DuraBoss
     */
    public void stopDuraboss(String host,
                             DurabossConfig durabossConfig,
                             RestHttpHelper restHelper);
}
