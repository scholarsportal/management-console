
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.cmd.ComputeStatusCmd;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.mgmt.ComputeManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class ComputeStatusController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeManager computeManager;

    public ComputeStatusController() {
        setCommandClass(ComputeStatusCmd.class);
        setCommandName("instanceCmd");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest arg0,
                                  HttpServletResponse arg1,
                                  Object command,
                                  BindException arg3) throws Exception {

        ComputeStatusCmd params = (ComputeStatusCmd) command;

        String computeAcctId = params.getComputeAcctId();

        log.info("user cmd       : " + params.getCmd());
        log.info("compute acct id: " + computeAcctId);

        ComputeAcct computeAcct =
                executeCommand(params.getCmd(), computeAcctId);

        return new ModelAndView("acctUpdate/computeStatus",
                                "computeAcct",
                                computeAcct);
    }

    private ComputeAcct executeCommand(String cmd, String computeAcctId)
            throws Exception {
        ComputeAcct acct = null;
        if (cmd.equals("Start")) {
            log.info("Starting instance...");
            acct = computeManager.startComputeInstance(computeAcctId);
        } else if (cmd.equals("Stop")) {
            log.info("Stopping instance...");
            acct = computeManager.stopComputeInstance(computeAcctId);
        } else {
            log.info("Refreshing instance...");
            acct = computeManager.findComputeAccount(computeAcctId);
        }
        return acct;
    }

    public ComputeManager getComputeManager() {
        return computeManager;
    }

    public void setComputeManager(ComputeManager computeManager) {
        this.computeManager = computeManager;
    }

}