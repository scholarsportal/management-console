
package org.duraspace.mainwebapp.domain.repo.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.Address;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.StorageProvider;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepository;
import org.duraspace.mainwebapp.domain.repo.StorageProviderRepository;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.duraspace.serviceprovider.domain.ComputeProviderType;
import org.duraspace.storage.domain.StorageProviderType;

public class MainDatabaseLoader {

    protected static final Logger log =
            Logger.getLogger(MainDatabaseLoader.class);

    private DuraSpaceAcctManager duraSpaceAcctManager;

    private ComputeProviderRepository computeProviderRepository;

    private StorageProviderRepository storageProviderRepository;

    private final Map<String, Integer> userIds;

    private final Map<String, Integer> duraAcctIds;

    private final Map<String, Integer> computeProviderIds;

    private final Map<String, Integer> storageProviderIds;

    private final Credential amazonCred;

    private final String BILL = "bill";

    private final String CHRIS = "chris";

    private final String BRAD = "brad";

    private final String ANDREW = "andrew";

    private final String SANDY = "sandy";

    private final String MICHELLE = "michelle";

    private final String EC2_PROPS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                    + "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"
                    + "<properties>"
                    + "<entry key=\"provider\">amazon-ec2</entry>"
                    + "<entry key=\"signatureMethod\">HmacSHA1</entry>"
                    + "<entry key=\"keyname\">awoods-keypair</entry>"
                    + "<entry key=\"imageId\">ami-9ad532f3</entry>"
                    + "<entry key=\"minInstanceCount\">1</entry>"
                    + "<entry key=\"maxInstanceCount\">1</entry>"
                    + "<entry key=\"maxAsyncThreads\">35</entry>"
                    + "<entry key=\"webappProtocol\">http</entry>"
                    + "<entry key=\"webappPort\">8080</entry>"
                    + "<entry key=\"webappName\">/instancewebapp</entry>"
                    + "</properties>";

    public MainDatabaseLoader() {
        this(new Credential("username", "password"));
    }

    public MainDatabaseLoader(Credential amazonCred) {
        this.amazonCred = amazonCred;
        userIds = new HashMap<String, Integer>();
        duraAcctIds = new HashMap<String, Integer>();
        computeProviderIds = new HashMap<String, Integer>();
        storageProviderIds = new HashMap<String, Integer>();
    }

    public void loadTestData() throws Exception {
        loadUsers();
        loadAddresses();
        loadAuthorities();
        loadCredentials();
        loadDuraSpaceAccts();
        loadStorageProviders();
        loadStorageAccts();
        loadComputeProviders();
        loadComputeAccts();
    }

    private void loadUsers() throws Exception {
        User bill = new User();
        bill.setLastname("B");
        bill.setFirstname("Bill");
        bill.setEmail(BILL + "@email.com");

        User chris = new User();
        chris.setLastname("W");
        chris.setFirstname("Chris");
        chris.setEmail(CHRIS + "@email.com");

        User brad = new User();
        brad.setLastname("M");
        brad.setFirstname("Brad");
        brad.setEmail(BRAD + "@email.com");

        User andrew = new User();
        andrew.setLastname("W");
        andrew.setFirstname("Andrew");
        andrew.setEmail(ANDREW + "@email.com");

        User sandy = new User();
        sandy.setLastname("P");
        sandy.setFirstname("Sandy");
        sandy.setEmail(SANDY + "@email.com");

        User michelle = new User();
        michelle.setLastname("K");
        michelle.setFirstname("Michelle");
        michelle.setEmail(MICHELLE + "@email.com");

        log.info("about to save user: " + bill);
        userIds.put(BILL, getDuraSpaceAcctManager().saveUser(bill));
        log.info("saved user: " + bill);
        userIds.put(CHRIS, getDuraSpaceAcctManager().saveUser(chris));
        userIds.put(BRAD, getDuraSpaceAcctManager().saveUser(brad));
        userIds.put(ANDREW, getDuraSpaceAcctManager().saveUser(andrew));
        userIds.put(SANDY, getDuraSpaceAcctManager().saveUser(sandy));
        userIds.put(MICHELLE, getDuraSpaceAcctManager().saveUser(michelle));
    }

    private void loadAddresses() throws Exception {
        Address addrBill = new Address();
        addrBill.setStreet1("1st street");
        addrBill.setCity("Melbourne");
        addrBill.setState("FL");
        addrBill.setZip("11111");

        Address addrChris = new Address();
        addrChris.setStreet1("2nd street");
        addrChris.setCity("Rochester");
        addrChris.setState("NY");
        addrChris.setZip("22222");

        Address addrBrad = new Address();
        addrBrad.setStreet1("3rd street");
        addrBrad.setCity("Boston");
        addrBrad.setState("MA");
        addrBrad.setZip("33333");

        Address addrAndrew = new Address();
        addrAndrew.setStreet1("4th street");
        addrAndrew.setCity("Arlington");
        addrAndrew.setState("VA");
        addrAndrew.setZip("44444");

        Address addrSandy = new Address();
        addrSandy.setStreet1("5th street");
        addrSandy.setCity("Ithaca");
        addrSandy.setState("NY");
        addrSandy.setZip("55555");

        Address addrMichelle = new Address();
        addrMichelle.setStreet1("6th street");
        addrMichelle.setCity("Boston");
        addrMichelle.setState("MA");
        addrMichelle.setZip("33333");

        getDuraSpaceAcctManager().saveAddressForUser(addrBill,
                                                     userIds.get(BILL));
        getDuraSpaceAcctManager().saveAddressForUser(addrChris,
                                                     userIds.get(CHRIS));
        getDuraSpaceAcctManager().saveAddressForUser(addrBrad,
                                                     userIds.get(BRAD));
        getDuraSpaceAcctManager().saveAddressForUser(addrAndrew,
                                                     userIds.get(ANDREW));
        getDuraSpaceAcctManager().saveAddressForUser(addrSandy,
                                                     userIds.get(SANDY));
        getDuraSpaceAcctManager().saveAddressForUser(addrMichelle,
                                                     userIds.get(MICHELLE));
    }

    private void loadAuthorities() {
        // do nothing.
    }

    private void loadCredentials() throws Exception {

        String password = "duraspace";
        Credential credBill = new Credential(BILL, password);
        Credential credChris = new Credential(CHRIS, password);
        Credential credBrad = new Credential(BRAD, password);
        Credential credAndrew = new Credential(ANDREW, password);
        Credential credSandy = new Credential(SANDY, password);
        Credential credMichelle = new Credential(MICHELLE, password);

        getDuraSpaceAcctManager().saveCredentialForUser(credBill,
                                                        userIds.get(BILL));
        getDuraSpaceAcctManager().saveCredentialForUser(credChris,
                                                        userIds.get(CHRIS));
        getDuraSpaceAcctManager().saveCredentialForUser(credBrad,
                                                        userIds.get(BRAD));
        getDuraSpaceAcctManager().saveCredentialForUser(credAndrew,
                                                        userIds.get(ANDREW));
        getDuraSpaceAcctManager().saveCredentialForUser(credSandy,
                                                        userIds.get(SANDY));
        getDuraSpaceAcctManager().saveCredentialForUser(credMichelle,
                                                        userIds.get(MICHELLE));
    }

    private void loadDuraSpaceAccts() throws Exception {
        DuraSpaceAcct acctBillChris = new DuraSpaceAcct();
        acctBillChris.setAccountName("Duraspace: B&C");

        DuraSpaceAcct acctBradAndrew = new DuraSpaceAcct();
        acctBradAndrew.setAccountName("Duraspace: B&A");

        DuraSpaceAcct acctSandyMichelle = new DuraSpaceAcct();
        acctSandyMichelle.setAccountName("Duraspace: M&S");

        duraAcctIds.put(BILL, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctBillChris, userIds.get(BILL)));
        duraAcctIds.put(BILL, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctBillChris, userIds.get(CHRIS)));
        duraAcctIds.put(BRAD, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctBradAndrew, userIds.get(BRAD)));
        duraAcctIds.put(BRAD, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctBradAndrew, userIds.get(ANDREW)));
        duraAcctIds.put(SANDY, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctSandyMichelle, userIds.get(SANDY)));
        duraAcctIds.put(SANDY, getDuraSpaceAcctManager()
                .saveDuraAcctForUser(acctSandyMichelle, userIds.get(MICHELLE)));
    }

    private void loadComputeProviders() throws Exception {
        ComputeProvider providerAmazon = new ComputeProvider();
        providerAmazon.setProviderName(ComputeProviderType.AMAZON_EC2
                .toString());
        providerAmazon.setProviderType(ComputeProviderType.AMAZON_EC2);
        providerAmazon.setUrl("http://aws.amazon.com/ec2");

        ComputeProvider providerMS = new ComputeProvider();
        providerMS.setProviderName(ComputeProviderType.MICROSOFT_AZURE
                .toString());
        providerMS.setProviderType(ComputeProviderType.MICROSOFT_AZURE);
        providerMS.setUrl("http://www.microsoft.com/azure");

        ComputeProvider providerSun = new ComputeProvider();
        providerSun.setProviderName(ComputeProviderType.SUN.toString());
        providerSun.setProviderType(ComputeProviderType.SUN);
        providerSun.setUrl("http://www.sun.com/cloud");

        ComputeProvider providerUnknown = new ComputeProvider();
        providerUnknown.setProviderName(ComputeProviderType.UNKNOWN.toString());
        providerUnknown.setProviderType(ComputeProviderType.UNKNOWN);
        providerUnknown.setUrl("http://google.com");

        computeProviderIds.put(ComputeProviderType.AMAZON_EC2.toString(),
                               getComputeProviderRepository()
                                       .saveComputeProvider(providerAmazon));
        computeProviderIds.put(ComputeProviderType.MICROSOFT_AZURE.toString(),
                               getComputeProviderRepository()
                                       .saveComputeProvider(providerMS));
        computeProviderIds.put(ComputeProviderType.SUN.toString(),
                               getComputeProviderRepository()
                                       .saveComputeProvider(providerSun));
        computeProviderIds.put(ComputeProviderType.UNKNOWN.toString(),
                               getComputeProviderRepository()
                                       .saveComputeProvider(providerUnknown));
    }

    private void loadComputeAccts() throws Exception {
        ComputeAcct acctBillChris = new ComputeAcct();
        acctBillChris.setNamespace("namespaceBC");
        acctBillChris.setXmlProps(EC2_PROPS);
        acctBillChris.setComputeProviderType(ComputeProviderType.AMAZON_EC2);
        acctBillChris.setComputeProviderId(computeProviderIds
                .get(ComputeProviderType.AMAZON_EC2.toString()));
        acctBillChris.setDuraAcctId(duraAcctIds.get(BILL));

        ComputeAcct acctBradAndrew = new ComputeAcct();
        acctBradAndrew.setNamespace("namespaceBA");
        acctBradAndrew.setXmlProps(EC2_PROPS);
        acctBradAndrew.setComputeProviderType(ComputeProviderType.AMAZON_EC2);
        acctBradAndrew.setComputeProviderId(computeProviderIds
                .get(ComputeProviderType.AMAZON_EC2.toString()));
        acctBradAndrew.setDuraAcctId(duraAcctIds.get(BRAD));

        ComputeAcct acctSandyMichelle = new ComputeAcct();
        acctSandyMichelle.setNamespace("namespaceSM");
        acctSandyMichelle.setXmlProps(EC2_PROPS);
        acctSandyMichelle.setComputeProviderType(ComputeProviderType.SUN);
        acctSandyMichelle.setComputeProviderId(computeProviderIds
                .get(ComputeProviderType.SUN.toString()));
        acctSandyMichelle.setDuraAcctId(duraAcctIds.get(SANDY));

        int acctIdBillChris =
                getDuraSpaceAcctManager().saveComputeAcct(acctBillChris);
        int acctIdBradAndrew =
                getDuraSpaceAcctManager().saveComputeAcct(acctBradAndrew);
        int acctIdSandyMichelle =
                getDuraSpaceAcctManager().saveComputeAcct(acctSandyMichelle);

        getDuraSpaceAcctManager().saveCredentialForComputeAcct(amazonCred,
                                                               acctIdBillChris);
        getDuraSpaceAcctManager()
                .saveCredentialForComputeAcct(amazonCred, acctIdBradAndrew);
        getDuraSpaceAcctManager()
                .saveCredentialForComputeAcct(amazonCred, acctIdSandyMichelle);
    }

    private void loadStorageProviders() throws Exception {
        StorageProvider providerAmazon = new StorageProvider();
        providerAmazon
                .setProviderName(StorageProviderType.AMAZON_S3.toString());
        providerAmazon.setProviderType(StorageProviderType.AMAZON_S3);
        providerAmazon.setUrl("http://aws.amazon.com/s3");

        StorageProvider providerMS = new StorageProvider();
        providerMS.setProviderName(StorageProviderType.MICROSOFT_AZURE
                .toString());
        providerMS.setProviderType(StorageProviderType.MICROSOFT_AZURE);
        providerMS.setUrl("http://www.microsoft.com/azure");

        StorageProvider providerSun = new StorageProvider();
        providerSun.setProviderName(StorageProviderType.SUN.toString());
        providerSun.setProviderType(StorageProviderType.SUN);
        providerSun.setUrl("http://www.sun.com/cloud");

        StorageProvider providerUnknown = new StorageProvider();
        providerUnknown.setProviderName(StorageProviderType.UNKNOWN.toString());
        providerUnknown.setProviderType(StorageProviderType.UNKNOWN);
        providerUnknown.setUrl("http://google.com");

        storageProviderIds.put(StorageProviderType.AMAZON_S3.toString(),
                               getStorageProviderRepository()
                                       .saveStorageProvider(providerAmazon));
        storageProviderIds.put(StorageProviderType.MICROSOFT_AZURE.toString(),
                               getStorageProviderRepository()
                                       .saveStorageProvider(providerMS));
        storageProviderIds.put(StorageProviderType.SUN.toString(),
                               getStorageProviderRepository()
                                       .saveStorageProvider(providerSun));
        storageProviderIds.put(StorageProviderType.UNKNOWN.toString(),
                               getStorageProviderRepository()
                                       .saveStorageProvider(providerUnknown));

    }

    private void loadStorageAccts() throws Exception {
        StorageAcct acctBillChris = new StorageAcct();
        acctBillChris.setNamespace("namespaceBC");
        acctBillChris.setStorageProviderType(StorageProviderType.AMAZON_S3);
        acctBillChris.setStorageProviderId(storageProviderIds
                .get(StorageProviderType.AMAZON_S3.toString()));
        acctBillChris.setDuraAcctId(duraAcctIds.get(BILL));

        StorageAcct acctBradAndrew = new StorageAcct();
        acctBradAndrew.setNamespace("namespaceBA");
        acctBradAndrew.setStorageProviderType(StorageProviderType.AMAZON_S3);
        acctBradAndrew.setStorageProviderId(storageProviderIds
                .get(StorageProviderType.AMAZON_S3.toString()));
        acctBradAndrew.setDuraAcctId(duraAcctIds.get(BRAD));

        StorageAcct acctSandyMichelle = new StorageAcct();
        acctSandyMichelle.setNamespace("namespaceSM");
        acctSandyMichelle.setStorageProviderType(StorageProviderType.SUN);
        acctSandyMichelle.setStorageProviderId(storageProviderIds
                .get(StorageProviderType.SUN.toString()));
        acctSandyMichelle.setDuraAcctId(duraAcctIds.get(SANDY));

        int acctIdBillChris =
                getDuraSpaceAcctManager().saveStorageAcct(acctBillChris);
        int acctIdBradAndrew =
                getDuraSpaceAcctManager().saveStorageAcct(acctBradAndrew);
        int acctIdSandyMichelle =
                getDuraSpaceAcctManager().saveStorageAcct(acctSandyMichelle);

        getDuraSpaceAcctManager().saveCredentialForStorageAcct(amazonCred,
                                                               acctIdBillChris);
        getDuraSpaceAcctManager()
                .saveCredentialForStorageAcct(amazonCred, acctIdBradAndrew);
        getDuraSpaceAcctManager()
                .saveCredentialForStorageAcct(amazonCred, acctIdSandyMichelle);
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

    public ComputeProviderRepository getComputeProviderRepository() {
        return computeProviderRepository;
    }

    public void setComputeProviderRepository(ComputeProviderRepository computeProviderRepository) {
        this.computeProviderRepository = computeProviderRepository;
    }

    public StorageProviderRepository getStorageProviderRepository() {
        return storageProviderRepository;
    }

    public void setStorageProviderRepository(StorageProviderRepository storageProviderRepository) {
        this.storageProviderRepository = storageProviderRepository;
    }

}
