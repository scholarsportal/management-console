/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.cmd;

import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.domain.model.ComputeProvider;

public class ComputeAcctWrapper {

    private ComputeAcct computeAcct;

    private ComputeProvider computeProvider;

    private String timer;

    private boolean isComputeAppInitialized;

    private String spacesURL;

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

    public String getSpacesURL() {
        return spacesURL;
    }

    public void setSpacesURL(String spacesURL) {
        this.spacesURL = spacesURL;
    }

}
