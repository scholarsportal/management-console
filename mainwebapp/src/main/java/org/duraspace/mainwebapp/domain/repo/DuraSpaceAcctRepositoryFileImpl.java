
package org.duraspace.mainwebapp.domain.repo;


public class DuraSpaceAcctRepositoryFileImpl {
//        implements DuraSpaceAcctRepository {
//
//    protected final Logger log = Logger.getLogger(getClass());
//
//    public static final String REPO_LOCATION = "repoLocation";
//
//    private String repoLocation;
//
//    private List<DuraSpaceAcct> duraAccts;
//
//    public DuraSpaceAcct findDuraSpaceAcct(Credential cred) throws Exception {
//        DuraSpaceAcct retrievedAcct = null;
//        Iterator<DuraSpaceAcct> acctItr = getDuraAccts().iterator();
//        while (acctItr.hasNext()) {
//            DuraSpaceAcct currentAcct = acctItr.next();
//            if (currentAcct.authenticates(cred)) {
//                retrievedAcct = currentAcct;
//                break;
//            }
//        }
//
//        if (retrievedAcct == null) {
//            throw new NoSuchElementException("DuraSpace Acct not found for: '"
//                    + cred + "'");
//        }
//        return retrievedAcct;
//    }
//
//    public void saveDuraSpaceAcct(DuraSpaceAcct acct) throws Exception {
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
//        xstream.alias("customerAcct", DuraSpaceAcct.class);
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
//    private List<DuraSpaceAcct> getDuraAccts() {
//        // Load customer accounts if this is the first time used.
//        if (duraAccts == null) {
//            AutoCloseInputStream in =
//                    new AutoCloseInputStream(this.getClass().getClassLoader()
//                            .getResourceAsStream(repoLocation));
//            try {
//                XStream xstream = getXStream();
//                duraAccts = (List<DuraSpaceAcct>) xstream.fromXML(in);
//            } catch (Exception e) {
//                log.warn("Unable to find repo at: '" + repoLocation + "'");
//            }
//        }
//
//        // Initialize customer accounts if the repository is empty.
//        if (duraAccts == null) {
//            duraAccts = new ArrayList<DuraSpaceAcct>();
//        }
//        return duraAccts;
//    }
//
//    public int getNumCustomerAccts() throws Exception {
//        return getDuraAccts().size();
//    }
//
//    public DuraSpaceAcct findDuraAcctById(int id) throws Exception {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
