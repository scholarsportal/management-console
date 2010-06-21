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
