/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsyncClient;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.File;
import java.io.IOException;

/**
 * This class creates and caches a client for the Amazon SimpleDB.
 *
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class AmazonSimpleDBClientMgr {

    private AmazonSimpleDBAsync db;

    public AmazonSimpleDBClientMgr(String credentialsFilename) {
        File file = new File(credentialsFilename);
        try {
            db = new AmazonSimpleDBAsyncClient(new PropertiesCredentials(file));

        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    public AmazonSimpleDBClientMgr(String accessKey, String secretKey) {
        db = new AmazonSimpleDBAsyncClient(new BasicAWSCredentials(accessKey,
                                                                   secretKey));
    }

    public AmazonSimpleDBAsync getClient() {
        return db;
    }
}
