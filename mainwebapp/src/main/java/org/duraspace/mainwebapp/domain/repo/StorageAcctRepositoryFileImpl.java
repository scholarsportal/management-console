
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

import org.duraspace.mainwebapp.domain.model.StorageAcct;

/**
 * This class is a temporary File-based implementation of the
 * StorageAcctRepository interface.
 * <p>
 * It is a READ-ONLY datastore backed by an XML file found in the location
 * [REPO_LOCATION] provided through setProperties().
 * </p>
 *
 * @author Andrew Woods
 */
public class StorageAcctRepositoryFileImpl
        implements StorageAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    public static final String REPO_LOCATION = "repoLocation";

    private String repoLocation;

    private List<StorageAcct> storageAccts;

    /**
     * This method is not supported by this implementation. {@inheritDoc}
     */
    public void saveStorageAcct(StorageAcct acct) throws Exception {
        throw new UnsupportedOperationException("File Database is Read-Only!");
    }

    /**
     * {@inheritDoc}
     */
    public List<StorageAcct> findStorageAccts(String customerId)
            throws Exception {
        List<StorageAcct> retrievedAccts = new ArrayList<StorageAcct>();
        Iterator<StorageAcct> acctItr = getStorageAccts().iterator();
        while (acctItr.hasNext()) {
            StorageAcct currentAcct = acctItr.next();
            if (currentAcct.hasOwner(customerId)) {
                retrievedAccts.add(currentAcct);
            }
        }

        if (retrievedAccts.isEmpty()) {
            throw new NoSuchElementException("Storage Acct not found for: '"
                    + customerId + "'");
        }
        return retrievedAccts;
    }

    @SuppressWarnings("unchecked")
    private List<StorageAcct> getStorageAccts() {
        // Load storage accounts if this is the first time used.
        if (storageAccts == null) {
            InputStream in =
                    this.getClass().getClassLoader()
                            .getResourceAsStream(repoLocation);
            if (in != null) {
                XStream xstream = getXStream();
                storageAccts = (List<StorageAcct>) xstream.fromXML(in);
            } else {
                log.warn("Unable to find repo at: '" + repoLocation + "'");
            }
        }

        // Initialize customer accounts if the repository is empty.
        if (storageAccts == null) {
            storageAccts = new ArrayList<StorageAcct>();
        }
        return storageAccts;
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("storageAcct", StorageAcct.class);
        xstream.alias("storageAccts", List.class);
        xstream.useAttributeFor("ownerId", String.class);
        xstream.useAttributeFor("isPrimary", boolean.class);
        return xstream;
    }

    @SuppressWarnings("unused")
    private void persistUpdate() throws FileNotFoundException {

        XStream xstream = getXStream();
        String xml = xstream.toXML(getStorageAccts());

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

    /**
     * {@inheritDoc}
     */
    public void setProperties(Properties props) throws Exception {
        String loc = props.getProperty(REPO_LOCATION);
        if (loc == null) {
            throw new NoSuchElementException("'" + REPO_LOCATION
                    + "' property not found!");
        }
        repoLocation = loc;
    }

}
