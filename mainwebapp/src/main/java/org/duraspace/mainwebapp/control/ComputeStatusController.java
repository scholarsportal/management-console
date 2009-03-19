
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.cmd.ComputeAcctWrapper;
import org.duraspace.mainwebapp.domain.cmd.ComputeStatusCmd;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.mainwebapp.mgmt.ComputeAcctManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class ComputeStatusController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeAcctManager computeManager;

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

        String cmd = params.getCmd();
        int computeAcctId = params.getComputeAcctIdAsInt();
        String timer = calculateTimer(params.getTimer());

        log.info("user cmd       : " + cmd);
        log.info("compute acct id: " + computeAcctId);

        ComputeAcctWrapper inputToView = executeCommand(cmd, computeAcctId);
        inputToView.setTimer(timer);

        return new ModelAndView("acctUpdate/computeStatus",
                                "input",
                                inputToView);
    }

    private ComputeAcctWrapper executeCommand(String cmd, int computeAcctId)
            throws Exception {

        ComputeAcct acct = null;
        ComputeAcctWrapper wrapper = new ComputeAcctWrapper();

        wrapper.setComputeProvider(getComputeProvider(computeAcctId));

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
            acct = computeManager.findComputeAccountAndLoadCredential(computeAcctId);
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

    private ComputeProvider getComputeProvider(int computeAcctId)
            throws Exception {
        return getComputeManager()
                .findComputeProviderForComputeAcct(computeAcctId);
    }

    public ComputeAcctManager getComputeManager() {
        return computeManager;
    }

    public void setComputeManager(ComputeAcctManager computeManager) {
        this.computeManager = computeManager;
    }

}