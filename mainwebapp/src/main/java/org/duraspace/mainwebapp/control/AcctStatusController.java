
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.AcctStatusCmd;
import org.duraspace.mainwebapp.domain.cmd.ComputeAcctWrapper;
import org.duraspace.mainwebapp.mgmt.ComputeManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class AcctStatusController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeManager computeManager;

    public AcctStatusController() {
        setCommandClass(AcctStatusCmd.class);
        setCommandName("instanceCmd");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest arg0,
                                  HttpServletResponse arg1,
                                  Object command,
                                  BindException arg3) throws Exception {

        AcctStatusCmd params = (AcctStatusCmd) command;

        Credential cred = new Credential();
        cred.setUsername(params.getUsername());
        cred.setPassword(params.getPassword());

        log.info("user cmd : " + params.getCmd());
        log.info("user cred: " + cred);

        ComputeAcctWrapper inputToView = new ComputeAcctWrapper();
        inputToView.setComputeAcct(computeManager.findComputeAccount(cred));

        return new ModelAndView("acctUpdate/computeStatus",
                                "input",
                                inputToView);
    }

    public ComputeManager getComputeManager() {
        return computeManager;
    }

    public void setComputeManager(ComputeManager computeManager) {
        this.computeManager = computeManager;
    }

}