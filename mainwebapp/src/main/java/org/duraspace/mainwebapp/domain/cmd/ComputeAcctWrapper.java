
package org.duraspace.mainwebapp.domain.cmd;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;

public class ComputeAcctWrapper {

    private ComputeAcct computeAcct;

    private ComputeProvider computeProvider;

    private String timer;

    private boolean isComputeAppInitialized;

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public boolean isComputeAppInitialized() {
        return isComputeAppInitialized;
    }

    public void setComputeAppInitialized(boolean isComputeAppInitialized) {
        this.isComputeAppInitialized = isComputeAppInitialized;
    }

    public ComputeAcct getComputeAcct() {
        return computeAcct;
    }

    public void setComputeAcct(ComputeAcct computeAcct) {
        this.computeAcct = computeAcct;
    }

    public ComputeProvider getComputeProvider() {
        return computeProvider;
    }

    public void setComputeProvider(ComputeProvider computeProvider) {
        this.computeProvider = computeProvider;
    }

}
