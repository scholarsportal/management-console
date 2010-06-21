/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

public class DuraCloudAcctRepositoryFileImpl {
//        implements DuraCloudAcctRepository {
//
//    protected final Logger log = Logger.getLogger(getClass());
//
//    public static final String REPO_LOCATION = "repoLocation";
//
//    private String repoLocation;
//
//    private List<DuraCloudAcct> duraAccts;
//
//    public DuraCloudAcct findDuraCloudAcct(Credential cred) throws Exception {
//        DuraCloudAcct retrievedAcct = null;
//        Iterator<DuraCloudAcct> acctItr = getDuraAccts().iterator();
//        while (acctItr.hasNext()) {
//            DuraCloudAcct currentAcct = acctItr.next();
//            if (currentAcct.authenticates(cred)) {
//                retrievedAcct = currentAcct;
//                break;
//            }
//        }
//
//        if (retrievedAcct == null) {
//            throw new NoSuchElementException("DuraCloud Acct not found for: '"
//                    + cred + "'");
//        }
//        return retrievedAcct;
//    }
//
//    public void saveDuraCloudAcct(DuraCloudAcct acct) throws Exception {
//
//        if (getDuraAccts().remove(acct)) {
//            log.info("Acct: '" + acct + "' is being updated.");
//        }
//
//        getDuraAccts().add(acct);
//
//        // HAVE TURNED OFF PERSISTING TO DISK FOR NOW.
//        //        persistUpdate();
//
//    }
//
//    private void persistUpdate() throws FileNotFoundException {
//
//        XStream xstream = getXStream();
//        String xml = xstream.toXML(getDuraAccts());
//
//        FileOutputStream out = new FileOutputStream(new File(repoLocation));
//        try {
//            out.write(xml.getBytes());
//        } catch (IOException e) {
//            log.error(e);
//        } finally {
//            try {
//                out.flush();
//                out.close();
//            } catch (IOException e) {
//                log.warn(e);
//            }
//        }
//    }
//
//    private XStream getXStream() {
//        XStream xstream = new XStream(new DomDriver());
//
//        xstream.omitField(ComputeProviderFactory.class, "log");
//        xstream.alias("customerAcct", DuraCloudAcct.class);
//        xstream.alias("customerAccts", List.class);
//        xstream.alias("user", User.class);
//        return xstream;
//    }
//
//    public void setProperties(Properties props) throws Exception {
//        String loc = props.getProperty(REPO_LOCATION);
//        if (loc == null) {
//            throw new NoSuchElementException("'" + REPO_LOCATION
//                    + "' property not found!");
//        }
//        repoLocation = loc;
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<DuraCloudAcct> getDuraAccts() {
//        // Load customer accounts if this is the first time used.
//        if (duraAccts == null) {
//            AutoCloseInputStream in =
//                    new AutoCloseInputStream(this.getClass().getClassLoader()
//                            .getResourceAsStream(repoLocation));
//            try {
//                XStream xstream = getXStream();
//                duraAccts = (List<DuraCloudAcct>) xstream.fromXML(in);
//            } catch (Exception e) {
//                log.warn("Unable to find repo at: '" + repoLocation + "'");
//            }
//        }
//
//        // Initialize customer accounts if the repository is empty.
//        if (duraAccts == null) {
//            duraAccts = new ArrayList<DuraCloudAcct>();
//        }
//        return duraAccts;
//    }
//
//    public int getNumCustomerAccts() throws Exception {
//        return getDuraAccts().size();
//    }
//
//    public DuraCloudAcct findDuraAcctById(int id) throws Exception {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
