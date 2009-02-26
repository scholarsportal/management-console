
package org.duraspace.mainwebapp.control;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.repo.CustomerAcctRepository;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class LoginController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private CustomerAcctRepository acctRepository;

    public LoginController() {
        setCommandClass(Credential.class);
        setCommandName("credential");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException bindException)
            throws Exception {
        Credential cred = (Credential) command;
        if (!credentialsValid(cred)) {
            log.error("credentials are invalid for: " + cred);
            throw new Exception("Invalid username/password.");
        }

        CustomerAcct custAcct = acctRepository.findCustomerAcct(cred);
        log.info("submitting user: " + custAcct.getDuraspaceCredential());

        return new ModelAndView(getSuccessView(), "custAcct", custAcct);
    }

    private boolean credentialsValid(Credential cred) {
        log.info("checking credential: " + cred.toString());
        return true;
    }

    public CustomerAcctRepository getAcctRepository() {
        return acctRepository;
    }

    public void setAcctRepository(CustomerAcctRepository acctRepository) {
        this.acctRepository = acctRepository;
    }

}