
package org.duraspace.mainwebapp.domain.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactoryImpl;

public class ComputeAcctRepositoryFileImpl
        implements ComputeAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    public static final String REPO_LOCATION = "repoLocation";

    private String repoLocation;

    private List<ComputeAcct> computeAccts;

    public ComputeAcct findComputeAcct(String acctId) throws Exception {
        ComputeAcct retrievedAcct = null;
        Iterator<ComputeAcct> acctItr = getComputeAccts().iterator();
        while (acctItr.hasNext()) {
            ComputeAcct currentAcct = acctItr.next();
            if (currentAcct.authenticates(acctId)) {
                retrievedAcct = currentAcct;
                break;
            }
        }

        if (retrievedAcct == null) {
            throw new NoSuchElementException("Compute Acct not found for: '"
                    + acctId + "'");
        }
        return retrievedAcct;
    }

    public void saveComputeAcct(ComputeAcct acct) throws Exception {

        if (getComputeAccts().remove(acct)) {
            log.info("Acct: '" + acct + "' is being updated.");
        }

        getComputeAccts().add(acct);

        // HAVE TURNED OFF PERSISTING TO DISK FOR NOW.
        //        persistUpdate();

    }

    private void persistUpdate() throws FileNotFoundException {

        XStream xstream = getXStream();
        String xml = xstream.toXML(getComputeAccts());

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

        xstream.omitField(ComputeAcct.class, "log");
        xstream.omitField(ComputeProviderFactoryImpl.class, "log");
        xstream.alias("computeAcct", ComputeAcct.class);
        xstream.alias("computeAccts", List.class);
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
    private List<ComputeAcct> getComputeAccts() {
        // Load customer accounts if this is the first time used.
        if (computeAccts == null) {
            InputStream in =
                    this.getClass().getClassLoader()
                            .getResourceAsStream(repoLocation);
            if (in != null) {
                XStream xstream = getXStream();
                computeAccts = (List<ComputeAcct>) xstream.fromXML(in);
            } else {
                log.warn("Unable to find repo: '" + repoLocation + "'");
            }
        }

        //        // Load customer accounts if this is the first time used.
        //        if (computeAccts == null) {
        //            XStream xstream = getXStream();
        //            try {
        //                computeAccts =
        //                        (List<ComputeAcct>) xstream
        //                                .fromXML(new FileInputStream(new File(repoLocation)));
        //            } catch (FileNotFoundException e) {
        //                log.warn(e);
        //            }
        //        }

        // Initialize customer accounts if the repository is empty.
        if (computeAccts == null) {
            computeAccts = new ArrayList<ComputeAcct>();
        }
        return computeAccts;
    }

    public List<String> getComputeAcctIds() throws Exception {
        List<String> ids = new ArrayList<String>();

        Iterator<ComputeAcct> acctItr = getComputeAccts().iterator();
        while (acctItr.hasNext()) {
            ids.add(acctItr.next().getId());
        }

        return ids;
    }

    public int getNumComputeAccts() throws Exception {
        return getComputeAccts().size();
    }

}
