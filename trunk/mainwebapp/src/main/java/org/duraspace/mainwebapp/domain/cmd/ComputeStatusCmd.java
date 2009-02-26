
package org.duraspace.mainwebapp.domain.cmd;

public class ComputeStatusCmd {

    private String cmd;

    private String computeAcctId;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getComputeAcctId() {
        return computeAcctId;
    }

    public void setComputeAcctId(String computeAcctId) {
        this.computeAcctId = computeAcctId;
    }

}
