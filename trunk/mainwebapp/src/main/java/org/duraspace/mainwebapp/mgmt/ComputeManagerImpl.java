
package org.duraspace.mainwebapp.mgmt;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.CustomerAcctRepository;

public class ComputeManagerImpl
        implements ComputeManager {

    protected final Logger log = Logger.getLogger(getClass());

    private CustomerAcctRepository customerAcctRepository;

    private ComputeAcctRepository computeAcctRepository;

    /**
     * {@inheritDoc}
     */
    public CustomerAcct startComputeInstance(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();

        startComputeInstance(computeAcctId);

        return custAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct startComputeInstance(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        // Make sure instance is not already running.
        if (compAcct.isInstanceRunning() || compAcct.isInstanceBooting()) {
            throw new Exception("Instance for compute-acct: '" + computeAcctId
                    + "' is already running");
        }

        // Start instance if ok.
        compAcct.startInstance();

        // Persist instance-id to compute-acct.
        getComputeAcctRepository().saveComputeAcct(compAcct);

        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerAcct stopComputeInstance(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();

        stopComputeInstance(computeAcctId);

        return custAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct stopComputeInstance(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        // Stop instance.
        compAcct.stopInstance();

        // Remove instance-id from compute-acct.
        getComputeAcctRepository().saveComputeAcct(compAcct);

        return compAcct;
    }


    /**
     * {@inheritDoc}
     */
    public ComputeAcct findComputeAccount(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        return compAcct;
    }

    public ComputeAcct findComputeAccount(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct =
                getComputeAcctRepository().findComputeAcct(computeAcctId);
        return compAcct;
    }

    public CustomerAcctRepository getCustomerAcctRepository() {
        return customerAcctRepository;
    }

    public void setCustomerAcctRepository(CustomerAcctRepository customerAcctRepo) {
        this.customerAcctRepository = customerAcctRepo;
    }

    public ComputeAcctRepository getComputeAcctRepository() {
        return computeAcctRepository;
    }

    public void setComputeAcctRepository(ComputeAcctRepository computeAcctRepository) {
        this.computeAcctRepository = computeAcctRepository;
    }

}
