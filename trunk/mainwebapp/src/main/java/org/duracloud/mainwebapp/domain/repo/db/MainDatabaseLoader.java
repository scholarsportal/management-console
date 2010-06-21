/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo.db;

import org.duracloud.common.model.Credential;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.domain.model.ComputeProvider;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.model.StorageProvider;
import org.duracloud.mainwebapp.domain.model.User;
import org.duracloud.mainwebapp.domain.repo.ComputeProviderRepository;
import org.duracloud.mainwebapp.domain.repo.StorageProviderRepository;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MainDatabaseLoader {

    protected static final Logger log =
            LoggerFactory.getLogger(MainDatabaseLoader.class);

    private DuraCloudAcctManager duraCloudAcctManager;

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
                    + "<entry key=\"imageId\">ami-210ee948</entry>"
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
        loadDuraCloudAccts();
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
        userIds.put(BILL, getDuraCloudAcctManager().saveUser(bill));
        log.info("saved user: " + bill);
        userIds.put(CHRIS, getDuraCloudAcctManager().saveUser(chris));
        userIds.put(BRAD, getDuraCloudAcctManager().saveUser(brad));
        userIds.put(ANDREW, getDuraCloudAcctManager().saveUser(andrew));
        userIds.put(SANDY, getDuraCloudAcctManager().saveUser(sandy));
        userIds.put(MICHELLE, getDuraCloudAcctManager().saveUser(michelle));
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

        getDuraCloudAcctManager().saveAddressForUser(addrBill,
                                                     userIds.get(BILL));
        getDuraCloudAcctManager().saveAddressForUser(addrChris,
                                                     userIds.get(CHRIS));
        getDuraCloudAcctManager().saveAddressForUser(addrBrad,
                                                     userIds.get(BRAD));
        getDuraCloudAcctManager().saveAddressForUser(addrAndrew,
                                                     userIds.get(ANDREW));
        getDuraCloudAcctManager().saveAddressForUser(addrSandy,
                                                     userIds.get(SANDY));
        getDuraCloudAcctManager().saveAddressForUser(addrMichelle,
                                                     userIds.get(MICHELLE));
    }

    private void loadAuthorities() {
        // do nothing.
    }

    private void loadCredentials() throws Exception {

        String password = "duracloud";
        Credential credBill = new Credential(BILL, password);
        Credential credChris = new Credential(CHRIS, password);
        Credential credBrad = new Credential(BRAD, password);
        Credential credAndrew = new Credential(ANDREW, password);
        Credential credSandy = new Credential(SANDY, password);
        Credential credMichelle = new Credential(MICHELLE, password);

        getDuraCloudAcctManager().saveCredentialForUser(credBill,
                                                        userIds.get(BILL));
        getDuraCloudAcctManager().saveCredentialForUser(credChris,
                                                        userIds.get(CHRIS));
        getDuraCloudAcctManager().saveCredentialForUser(credBrad,
                                                        userIds.get(BRAD));
        getDuraCloudAcctManager().saveCredentialForUser(credAndrew,
                                                        userIds.get(ANDREW));
        getDuraCloudAcctManager().saveCredentialForUser(credSandy,
                                                        userIds.get(SANDY));
        getDuraCloudAcctManager().saveCredentialForUser(credMichelle,
                                                        userIds.get(MICHELLE));
    }

    private void loadDuraCloudAccts() throws Exception {
        DuraCloudAcct acctBillChris = new DuraCloudAcct();
        acctBillChris.setAccountName("Duracloud: B&C");

        DuraCloudAcct acctBradAndrew = new DuraCloudAcct();
        acctBradAndrew.setAccountName("Duracloud: B&A");

        DuraCloudAcct acctSandyMichelle = new DuraCloudAcct();
        acctSandyMichelle.setAccountName("Duracloud: M&S");

        duraAcctIds.put(BILL, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctBillChris, userIds.get(BILL)));
        duraAcctIds.put(BILL, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctBillChris, userIds.get(CHRIS)));
        duraAcctIds.put(BRAD, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctBradAndrew, userIds.get(BRAD)));
        duraAcctIds.put(BRAD, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctBradAndrew, userIds.get(ANDREW)));
        duraAcctIds.put(SANDY, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctSandyMichelle, userIds.get(SANDY)));
        duraAcctIds.put(SANDY, getDuraCloudAcctManager()
                .saveDuraAcctForUser(acctSandyMichelle, userIds.get(MICHELLE)));
    }

    private void loadComputeProviders() throws Exception {

        for (ComputeProviderType providerType : ComputeProviderType.values()) {
            ComputeProvider provider = new ComputeProvider();
            provider.setProviderName(providerType.toString());
            provider.setProviderType(providerType);
            provider.setUrl(providerType.getUrl());

            computeProviderIds.put(provider.getProviderName(),
                                   getComputeProviderRepository()
                                           .saveComputeProvider(provider));
        }
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
        acctSandyMichelle.setComputeProviderType(ComputeProviderType.RACKSPACE_CLOUDSERVERS);
        acctSandyMichelle.setComputeProviderId(computeProviderIds
                .get(ComputeProviderType.RACKSPACE_CLOUDSERVERS.toString()));
        acctSandyMichelle.setDuraAcctId(duraAcctIds.get(SANDY));

        int acctIdBillChris =
                getDuraCloudAcctManager().saveComputeAcct(acctBillChris);
        int acctIdBradAndrew =
                getDuraCloudAcctManager().saveComputeAcct(acctBradAndrew);
        int acctIdSandyMichelle =
                getDuraCloudAcctManager().saveComputeAcct(acctSandyMichelle);

        getDuraCloudAcctManager().saveCredentialForComputeAcct(amazonCred,
                                                               acctIdBillChris);
        getDuraCloudAcctManager()
                .saveCredentialForComputeAcct(amazonCred, acctIdBradAndrew);
        getDuraCloudAcctManager()
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
        providerSun.setProviderName(StorageProviderType.RACKSPACE.toString());
        providerSun.setProviderType(StorageProviderType.RACKSPACE);
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
        storageProviderIds.put(StorageProviderType.RACKSPACE.toString(),
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
        acctSandyMichelle.setStorageProviderType(StorageProviderType.RACKSPACE);
        acctSandyMichelle.setStorageProviderId(storageProviderIds
                .get(StorageProviderType.RACKSPACE.toString()));
        acctSandyMichelle.setDuraAcctId(duraAcctIds.get(SANDY));

        int acctIdBillChris =
                getDuraCloudAcctManager().saveStorageAcct(acctBillChris);
        int acctIdBradAndrew =
                getDuraCloudAcctManager().saveStorageAcct(acctBradAndrew);
        int acctIdSandyMichelle =
                getDuraCloudAcctManager().saveStorageAcct(acctSandyMichelle);

        getDuraCloudAcctManager().saveCredentialForStorageAcct(amazonCred,
                                                               acctIdBillChris);
        getDuraCloudAcctManager()
                .saveCredentialForStorageAcct(amazonCred, acctIdBradAndrew);
        getDuraCloudAcctManager()
                .saveCredentialForStorageAcct(amazonCred, acctIdSandyMichelle);
    }

    public DuraCloudAcctManager getDuraCloudAcctManager() {
        return duraCloudAcctManager;
    }

    public void setDuraCloudAcctManager(DuraCloudAcctManager duraCloudAcctManager) {
        this.duraCloudAcctManager = duraCloudAcctManager;
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
