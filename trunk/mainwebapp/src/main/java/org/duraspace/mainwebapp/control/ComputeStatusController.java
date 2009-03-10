
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.cmd.ComputeAcctWrapper;
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
        String timer = calculateTimer(params.getTimer());

        log.info("user cmd       : " + params.getCmd());
        log.info("compute acct id: " + computeAcctId);

        ComputeAcctWrapper inputToView =
                executeCommand(params.getCmd(), computeAcctId);
        inputToView.setTimer(timer);

        return new ModelAndView("acctUpdate/computeStatus",
                                "input",
                                inputToView);
    }

    private ComputeAcctWrapper executeCommand(String cmd, String computeAcctId)
            throws Exception {

        ComputeAcct acct = null;
        ComputeAcctWrapper wrapper = new ComputeAcctWrapper();

        if (cmd.equals("Start")) {
            log.info("Starting instance...");
            acct = computeManager.startComputeInstance(computeAcctId);
        } else if (cmd.equals("Stop")) {
            log.info("Stopping instance...");
            acct = computeManager.stopComputeInstance(computeAcctId);
        } else if (cmd.equals("View Compute Console")) {
            log.info("Initializing instance...");
            acct = computeManager.initializeComputeApp(computeAcctId);
            wrapper.setComputeAppInitialized(true);
        } else {
            log.info("Refreshing instance...");
            acct = computeManager.findComputeAccount(computeAcctId);
        }

        wrapper.setComputeAcct(acct);
        return wrapper;
    }

    private String calculateTimer(String arg) {
        StringBuilder timer = new StringBuilder();
        String tail = "...";
        if (hasTail(arg, tail)) {
            int lessCount = previousCount(arg, tail) - 1;
            timer.append(arg + lessCount);
        } else {
            timer.append("9");
        }
        timer.append(tail);
        return timer.toString();
    }

    private boolean hasTail(String arg, String tail) {
        return (arg != null && arg.endsWith(tail) && arg.length() > tail
                .length());
    }

    private int previousCount(String arg, String tail) {
        return Character.getNumericValue(arg.charAt(arg.length()
                - tail.length() - 1));
    }

    public ComputeManager getComputeManager() {
        return computeManager;
    }

    public void setComputeManager(ComputeManager computeManager) {
        this.computeManager = computeManager;
    }

}