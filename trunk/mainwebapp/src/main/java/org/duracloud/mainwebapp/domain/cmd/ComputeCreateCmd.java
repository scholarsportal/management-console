/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.cmd;

import org.duracloud.common.model.Credential;

public class ComputeCreateCmd {

    private int duraAcctId;

    private String computeAcctNamespace;

    private String computeProviderType;

    private Credential computeCred;

    private String cmd;

    private String imageId;

    public int getDuraAcctId() {
        return duraAcctId;
    }

    public void setDuraAcctId(int duraAcctId) {
        this.duraAcctId = duraAcctId;
    }

    public Credential getComputeCred() {
        return computeCred;
    }

    public void setComputeCred(Credential computeCred) {
        this.computeCred = computeCred;
    }

    public String getComputeProviderType() {
        return computeProviderType;
    }

    public void setComputeProviderType(String computeProviderType) {
        this.computeProviderType = computeProviderType;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getComputeAcctNamespace() {
        return computeAcctNamespace;
    }

    public void setComputeAcctNamespace(String computeAcctNamespace) {
        this.computeAcctNamespace = computeAcctNamespace;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

}
