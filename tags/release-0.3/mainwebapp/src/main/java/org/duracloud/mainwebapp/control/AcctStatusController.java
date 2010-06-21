package org.duracloud.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duracloud.mainwebapp.domain.cmd.AcctStatusCmd;
import org.duracloud.mainwebapp.domain.cmd.ComputeAcctWrapper;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class AcctStatusController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private DuraCloudAcctManager duraAcctManager;

    public AcctStatusController() {
        setCommandClass(AcctStatusCmd.class);
        setCommandName("instanceCmd");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest arg0,
                                  HttpServletResponse arg1,
                                  Object command,
                                  BindException arg3) throws Exception {

//        AcctStatusCmd params = (AcctStatusCmd) command;
//
//        Credential duraCred = new Credential();
//        duraCred.setUsername(params.getUsername());
//        duraCred.setPassword(params.getPassword());
//
//        log.info("user cmd : " + params.getCmd());
//        log.info("user cred: " + duraCred);
//
//        ComputeAcctWrapper inputToView = new ComputeAcctWrapper();
//        inputToView.setComputeAccts(getDuraAcctManager()
//                .findComputeAccounts(duraCred));
        ComputeAcctWrapper inputToView = null;

        return new ModelAndView("acctUpdate/computeStatus",
                                "input",
                                inputToView);
    }

    public DuraCloudAcctManager getDuraAcctManager() {
        return duraAcctManager;
    }

    public void setDuraAcctManager(DuraCloudAcctManager duraAcctManager) {
        this.duraAcctManager = duraAcctManager;
    }

}