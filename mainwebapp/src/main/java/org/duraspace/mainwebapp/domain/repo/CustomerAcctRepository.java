
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;
import java.util.Properties;

import org.duraspace.mainwebapp.domain.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;

public interface CustomerAcctRepository {

    public CustomerAcct findCustomerAcct(Credential cred) throws Exception;

    public int getNumCustomerAccts() throws Exception;

    public List<String> getCustomerAcctIds() throws Exception;

    public void saveCustomerAcct(CustomerAcct acct) throws Exception;

    public void setProperties(Properties props) throws Exception;

}
