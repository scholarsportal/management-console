/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.cmd;

public class ComputeStatusCmd {

    private String cmd;

    private Integer computeAcctId;

    private String timer;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public void setComputeAcctId(int computeAcctId) {
        this.computeAcctId = computeAcctId;
    }

    public String getComputeAcctId() {
        return computeAcctId.toString();
    }

    public int getComputeAcctIdAsInt() {
        return computeAcctId;
    }
    public void setComputeAcctId(String computeAcctId) {
        this.computeAcctId = Integer.parseInt(computeAcctId);
    }
}
