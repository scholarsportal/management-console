
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;

public class CustomerAcctRepositoryDBImpl
        implements CustomerAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeAcct computeAcct;

    public CustomerAcct findCustomerAcct(Credential cred) {
        CustomerAcct acct = new CustomerAcct();

        return acct;
    }

    public ComputeAcct getComputeAcct() {
        return computeAcct;
    }

    public void setComputeAcct(ComputeAcct computeAcct) {
        this.computeAcct = computeAcct;
    }

    public void saveCustomerAcct(CustomerAcct acct) {
        // TODO Auto-generated method stub

    }

    public void setProperties(Properties props) throws Exception {
        // TODO Auto-generated method stub

    }

    public List<String> getCustomerAcctIds() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNumCustomerAccts() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

}
