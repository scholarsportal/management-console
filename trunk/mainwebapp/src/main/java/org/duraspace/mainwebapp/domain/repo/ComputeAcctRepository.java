
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;
import java.util.Properties;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;

public interface ComputeAcctRepository {

    public ComputeAcct findComputeAcct(String acctId) throws Exception;

    public int getNumComputeAccts() throws Exception;

    public List<String> getComputeAcctIds() throws Exception;

    public void saveComputeAcct(ComputeAcct acct) throws Exception;

    public void setProperties(Properties props) throws Exception;

}
