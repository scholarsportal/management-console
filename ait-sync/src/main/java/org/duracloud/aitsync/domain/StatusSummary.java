package org.duracloud.aitsync.domain;

import org.duracloud.aitsync.service.SyncManager;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
@XStreamAlias(value="stateSummary")
public class StatusSummary {
    private SyncManager.State state;

    public StatusSummary(SyncManager.State state) {
        super();
        this.state = state;
    }

    public SyncManager.State getState() {
        return state;
    }

}
