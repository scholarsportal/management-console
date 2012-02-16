/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.ServerDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts the Account table data from versions 0.4.1 and prior to the
 * split Account and ServerDetails table data for 0.5.0 and beyond.
 *
 * @author: Bill Branan
 * Date: Feb 10, 2012
 */
public class DbConverter {

    public static final String ACCOUNT_INFO_FILENAME = "AccountInfo.xml";
    public static final String SERVER_DETAILS_FILENAME = "ServerDetails.xml";

    private final Logger log = LoggerFactory.getLogger(DbConverter.class);

    private File workDir;

    public DbConverter(File workDir) {
        this.workDir = workDir;
    }

    public void run() {
        System.out.println("Running DB Converter using work directory: " +
                           workDir.getAbsolutePath());
        List<OldAccountInfo> oldAccounts = readOldFile();
        writeNewFiles(oldAccounts);
    }

    protected List<OldAccountInfo> readOldFile() {
        String fileName = ACCOUNT_INFO_FILENAME;
        File inputFile = new File(workDir, fileName);

        String xml = readFromFile(inputFile);
        return (List<OldAccountInfo>)deserialize(xml);
    }

    private String readFromFile(File inFile) {
        try {
        return FileUtils.readFileToString(inFile, "UTF-8");
        } catch(IOException e) {
            throw new RuntimeException("Could not read from file " + inFile +
                                       " due to error " + e.getMessage());
        }
    }

    protected void writeNewFiles(List<OldAccountInfo> oldAccounts) {
        List<AccountInfo> newAccounts = new ArrayList<AccountInfo>();
        List<ServerDetails> newServerDetails = new ArrayList<ServerDetails>();

        for(OldAccountInfo oldAccount : oldAccounts) {
            int id = oldAccount.getId();
            AccountInfo newAccount = new AccountInfo(
                id,
                oldAccount.getSubdomain(),
                oldAccount.getAcctName(),
                oldAccount.getOrgName(),
			    oldAccount.getDepartment(),
                -1,
                id,
                -1,
                oldAccount.getStatus(),
                AccountType.FULL,
                oldAccount.getCounter());
            newAccounts.add(newAccount);

            ServerDetails newDetails = new ServerDetails(
                id,
                oldAccount.getComputeProviderAccountId(),
                oldAccount.getPrimaryStorageProviderAccountId(),
                oldAccount.getSecondaryStorageProviderAccountIds(),
                oldAccount.getSecondaryServiceRepositoryIds(),
                oldAccount.getServicePlan());
            newServerDetails.add(newDetails);
        }

        String newAccountInfoSerialized = serialize(newAccounts);
        String newServerDetailsSerialized = serialize(newServerDetails);

        writeToFile(newAccountInfoSerialized, ACCOUNT_INFO_FILENAME);
        writeToFile(newServerDetailsSerialized, SERVER_DETAILS_FILENAME);
    }

    private void writeToFile(String serialized, String fileName) {
        File outFile = new File(workDir, fileName);
        try {
            FileUtils.writeStringToFile(outFile, serialized, "UTF-8");
        } catch(IOException e) {
            throw new RuntimeException("Could not write to file " +
                                       outFile.getAbsolutePath() +
                                       " due to error " + e.getMessage());
        }
    }

    private Object deserialize(String xml) {
        return getReadXStream().fromXML(xml);
    }

    private XStream getReadXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias(AccountInfo.class.getSimpleName(), OldAccountInfo.class);
        xstream.alias(ServerDetails.class.getSimpleName(), ServerDetails.class);
        return xstream;
    }

    private String serialize(Object obj) {
        return getWriteXStream().toXML(obj);
    }

    private XStream getWriteXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.NO_REFERENCES);        
        xstream.alias(AccountInfo.class.getSimpleName(), AccountInfo.class);
        xstream.alias(ServerDetails.class.getSimpleName(), ServerDetails.class);
        return xstream;
    }

}
