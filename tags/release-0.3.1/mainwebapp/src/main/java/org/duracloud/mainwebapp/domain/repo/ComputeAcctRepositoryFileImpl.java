package org.duracloud.mainwebapp.domain.repo;


public class ComputeAcctRepositoryFileImpl{
//        implements ComputeAcctRepository {
//
//    protected final Logger log = Logger.getLogger(getClass());
//
//    public static final String REPO_LOCATION = "repoLocation";
//
//    private String repoLocation;
//
//    private List<ComputeAcct> computeAccts;
//
//    public ComputeAcct findComputeAcct(String acctId) throws Exception {
//        ComputeAcct retrievedAcct = null;
//        Iterator<ComputeAcct> acctItr = getComputeAccts().iterator();
//        while (acctItr.hasNext()) {
//            ComputeAcct currentAcct = acctItr.next();
//            if (currentAcct.authenticates(acctId)) {
//                retrievedAcct = currentAcct;
//                break;
//            }
//        }
//
//        if (retrievedAcct == null) {
//            throw new NoSuchElementException("Compute Acct not found for: '"
//                    + acctId + "'");
//        }
//        return retrievedAcct;
//    }
//
//    public void saveComputeAcct(ComputeAcct acct) throws Exception {
//
//        if (getComputeAccts().remove(acct)) {
//            log.info("Acct: '" + acct + "' is being updated.");
//        }
//
//        getComputeAccts().add(acct);
//
//        // HAVE TURNED OFF PERSISTING TO DISK FOR NOW.
//        //        persistUpdate();
//
//    }
//
//    private void persistUpdate() throws FileNotFoundException {
//
//        XStream xstream = getXStream();
//        String xml = xstream.toXML(getComputeAccts());
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
//        xstream.omitField(ComputeAcct.class, "log");
//        xstream.omitField(ComputeProviderFactory.class, "log");
//        xstream.alias("computeAcct", ComputeAcct.class);
//        xstream.alias("computeAccts", List.class);
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
//    private List<ComputeAcct> getComputeAccts() {
//        // Load customer accounts if this is the first time used.
//        if (computeAccts == null) {
//            AutoCloseInputStream in =
//                    new AutoCloseInputStream(this.getClass().getClassLoader()
//                            .getResourceAsStream(repoLocation));
//            try {
//                XStream xstream = getXStream();
//                computeAccts = (List<ComputeAcct>) xstream.fromXML(in);
//            } catch (Exception e) {
//                log.warn("Unable to find repo: '" + repoLocation + "'");
//            }
//        }
//
//        // Initialize customer accounts if the repository is empty.
//        if (computeAccts == null) {
//            computeAccts = new ArrayList<ComputeAcct>();
//        }
//        return computeAccts;
//    }
//
//    public int getNumComputeAccts() throws Exception {
//        return getComputeAccts().size();
//    }

}
