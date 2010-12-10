/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudImageRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.common.error.DuraCloudCheckedException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.EncryptionUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class AmazonSimpleDBRepoMgr implements DuracloudRepoMgr {

    private final Logger log = LoggerFactory.getLogger(AmazonSimpleDBRepoMgr.class);

    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;
    private DuracloudRightsRepo rightsRepo;
    private DuracloudInstanceRepo instanceRepo; // not used yet
    private DuracloudImageRepo imageRepo; // not used yet

    private IdUtil idUtil;

    private final String DOMAIN_PREFIX;

    public AmazonSimpleDBRepoMgr(IdUtil idUtil) {
        this(idUtil, null);
    }

    public AmazonSimpleDBRepoMgr(IdUtil idUtil, String prefix) {
        this.idUtil = idUtil;
        this.DOMAIN_PREFIX = prefix;
    }

    @Override
    public void initialize(InputStream xml) throws DBException {
        log.debug("initializing");

        Credential credential;
        try {
            credential = readCredential(xml);

        } catch (Exception e) {
            String msg = "Error initializing: " + e.getMessage();
            log.error(msg, e);
            throw new DBException(msg, e);
        }

        doInitialize(credential);
    }

    private void doInitialize(Credential credential) {
        AmazonSimpleDBClientMgr dbClientMgr = new AmazonSimpleDBClientMgr(
            credential.getUsername(),
            credential.getPassword());

        if (null != DOMAIN_PREFIX) {
            String userTable = DOMAIN_PREFIX + "_USER_DOMAIN";
            String acctTable = DOMAIN_PREFIX + "_ACCT_DOMAIN";
            String rightsTable = DOMAIN_PREFIX + "_RIGHTS_DOMAIN";
            userRepo = new DuracloudUserRepoImpl(dbClientMgr, userTable);
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr, acctTable);
            rightsRepo = new DuracloudRightsRepoImpl(dbClientMgr, rightsTable);

        } else {
            userRepo = new DuracloudUserRepoImpl(dbClientMgr);
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr);
            rightsRepo = new DuracloudRightsRepoImpl(dbClientMgr);
        }

        idUtil.initialize(userRepo, accountRepo, rightsRepo);
    }

    private Credential readCredential(InputStream xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(xml);
        Element credential = doc.getRootElement();

        String encUsername = credential.getChildText("username");
        String encPassword = credential.getChildText("password");
        if (null == encUsername || null == encPassword) {
            String msg = "Error initializing: username and/or password null.";
            log.error(msg);
            throw new DuraCloudCheckedException(msg);
        }

        EncryptionUtil encryptUtil = new EncryptionUtil();
        String username = encryptUtil.decrypt(encUsername);
        String password = encryptUtil.decrypt(encPassword);
        return new Credential(username, password);
    }

    @Override
    public DuracloudUserRepo getUserRepo() {
        checkInitialized(this.userRepo, "DuracloudUserRepo");
        return this.userRepo;
    }

    @Override
    public DuracloudAccountRepo getAccountRepo() {
        checkInitialized(this.accountRepo, "DuracloudAccountRepo");
        return this.accountRepo;
    }

    @Override
    public DuracloudRightsRepo getRightsRepo() {
        checkInitialized(this.rightsRepo, "DuracloudRightsRepo");
        return this.rightsRepo;
    }

    @Override
    public DuracloudInstanceRepo getInstanceRepo() {
        checkInitialized(this.instanceRepo, "DuracloudInstanceRepo");
        return this.instanceRepo;
    }

    @Override
    public DuracloudImageRepo getImageRepo() {
        checkInitialized(this.imageRepo, "DuracloudImageRepo");
        return this.imageRepo;
    }

    @Override
    public IdUtil getIdUtil() {
        checkInitialized(this.idUtil, "IdUtil");
        return this.idUtil;
    }

    private void checkInitialized(Object member, String name) {
        if (null == member) {
            String msg = name + " is not initialized";
            log.error(msg);
            throw new DBUninitializedException(msg);
        }
    }
}
