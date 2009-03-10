
package org.duraspace.mainwebapp.domain.cmd;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;

public class ComputeAcctWrapper {

    private ComputeAcct computeAcct;

    private String timer;

    private boolean isComputeAppInitialized;

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public ComputeAcct getComputeAcct() {
        return computeAcct;
    }

    public void setComputeAcct(ComputeAcct computeAcct) {
        this.computeAcct = computeAcct;
    }

    public boolean isComputeAppInitialized() {
        return isComputeAppInitialized;
    }

    public void setComputeAppInitialized(boolean isComputeAppInitialized) {
        this.isComputeAppInitialized = isComputeAppInitialized;
    }

}
