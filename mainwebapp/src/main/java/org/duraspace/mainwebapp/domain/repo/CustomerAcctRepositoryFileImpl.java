
package org.duraspace.mainwebapp.domain.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;

public class CustomerAcctRepositoryFileImpl
        implements CustomerAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    public static final String REPO_LOCATION = "repoLocation";

    private String repoLocation;

    private List<CustomerAcct> custAccts;

    public CustomerAcct findCustomerAcct(Credential cred) throws Exception {
        CustomerAcct retrievedAcct = null;
        Iterator<CustomerAcct> acctItr = getCustAccts().iterator();
        while (acctItr.hasNext()) {
            CustomerAcct currentAcct = acctItr.next();
            if (currentAcct.authenticates(cred)) {
                retrievedAcct = currentAcct;
                break;
            }
        }

        if (retrievedAcct == null) {
            throw new NoSuchElementException("Customer Acct not found for: '"
                    + cred + "'");
        }
        return retrievedAcct;
    }

    public void saveCustomerAcct(CustomerAcct acct) throws Exception {

        if (getCustAccts().remove(acct)) {
            log.info("Acct: '" + acct + "' is being updated.");
        }

        getCustAccts().add(acct);

        // HAVE TURNED OFF PERSISTING TO DISK FOR NOW.
        //        persistUpdate();

    }

    private void persistUpdate() throws FileNotFoundException {

        XStream xstream = getXStream();
        String xml = xstream.toXML(getCustAccts());

        FileOutputStream out = new FileOutputStream(new File(repoLocation));
        try {
            out.write(xml.getBytes());
        } catch (IOException e) {
            log.error(e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                log.warn(e);
            }
        }
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());

        xstream.omitField(ComputeProviderFactory.class, "log");
        xstream.alias("customerAcct", CustomerAcct.class);
        xstream.alias("customerAccts", List.class);
        xstream.alias("user", User.class);
        return xstream;
    }

    public void setProperties(Properties props) throws Exception {
        String loc = props.getProperty(REPO_LOCATION);
        if (loc == null) {
            throw new NoSuchElementException("'" + REPO_LOCATION
                    + "' property not found!");
        }
        repoLocation = loc;
    }

    @SuppressWarnings("unchecked")
    private List<CustomerAcct> getCustAccts() {
        // Load customer accounts if this is the first time used.
        if (custAccts == null) {
            AutoCloseInputStream in =
                    new AutoCloseInputStream(this.getClass().getClassLoader()
                            .getResourceAsStream(repoLocation));
            try {
                XStream xstream = getXStream();
                custAccts = (List<CustomerAcct>) xstream.fromXML(in);
            } catch (Exception e) {
                log.warn("Unable to find repo at: '" + repoLocation + "'");
            }
        }

        // Initialize customer accounts if the repository is empty.
        if (custAccts == null) {
            custAccts = new ArrayList<CustomerAcct>();
        }
        return custAccts;
    }

    public int getNumCustomerAccts() throws Exception {
        return getCustAccts().size();
    }

}
