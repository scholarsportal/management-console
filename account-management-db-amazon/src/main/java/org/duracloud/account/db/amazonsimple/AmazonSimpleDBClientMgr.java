/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import java.io.File;
import java.io.IOException;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import org.duracloud.common.error.DuraCloudRuntimeException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

/**
 * This class creates and caches a client for the Amazon SimpleDB.
 *
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class AmazonSimpleDBClientMgr {

    private AmazonSimpleDB db;

    public AmazonSimpleDBClientMgr(String credentialsFilename) {
        File file = new File(credentialsFilename);
        try {
            db = new AmazonSimpleDBClient(new PropertiesCredentials(file));

        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    public AmazonSimpleDBClientMgr(String accessKey, String secretKey) {
        db = new AmazonSimpleDBClient(new BasicAWSCredentials(accessKey,
                                                              secretKey));
    }

    public AmazonSimpleDB getClient() {
        return db;
    }
}
