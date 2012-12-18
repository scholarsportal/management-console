package org.duracloud.aitsync;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
@XStreamAlias(value="stateSummary")
public class StatusSummary {
    private State state;

    public StatusSummary(State state) {
        super();
        this.state = state;
    }

    public State getState() {
        return state;
    }

}
