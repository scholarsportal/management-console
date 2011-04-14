/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.Console;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: 4/13/11
 */
public class DbUtilFiller {

    private DuracloudRepoMgr repoMgr;
    private Scanner scanner;

    public DbUtilFiller(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;

        Console console = System.console();
        if (null == console) {
            throw new DuraCloudRuntimeException("This VM has no console!");
        }
        this.scanner = new Scanner(console.reader());
    }

    /*
     * For tests only!
     */
    protected DbUtilFiller(DuracloudRepoMgr repoMgr, Scanner scanner) {
        this.repoMgr = repoMgr;
        this.scanner = scanner;
    }

    public void fill() {
        System.out.println("\nWelcome to the interactive data fill portion " +
            "of the database utility! The following questions will allow you " +
            "to enter the information needed to activate a DuraCloud " +
            "account. You will be able to review your changes before they " +
            "are committed to the database.\n");

        // Retrieve account via subdomain
        DuracloudAccountRepo acctRepo = repoMgr.getAccountRepo();
        AccountInfo account = null;
        while(null == account) {
            System.out.print("Please Enter Subdomain: ");
            String subdomain = readInput();
            try {
                account = acctRepo.findBySubdomain(subdomain);
            } catch(DBNotFoundException e) {
                System.out.println("The subdomain entered (" + subdomain +
                    ") cannot be found in the database. Please try again.\n");
            }
        }

        // Check account status
        if(account.getStatus().equals(AccountInfo.AccountStatus.ACTIVE)) {
            System.out.print("The account with subdomain " +
                             account.getSubdomain() +
                             " is already in the active state. Continue [Y/N]? ");
            String input = readInput();
            if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
                System.out.println("The current value of each field will be " +
                                   "displayed in braces.");
            } else {
                return;
            }
        }

        // Get storage and compute account information
        DuracloudStorageProviderAccountRepo storageRepo =
            repoMgr.getStorageProviderAccountRepo();
        DuracloudComputeProviderAccountRepo computeRepo =
            repoMgr.getComputeProviderAccountRepo();

        StorageProviderAccount primaryStorageAcct;
        Set<StorageProviderAccount> secondaryStorageAccts;
        ComputeProviderAccount computeAcct;
        try {
            primaryStorageAcct = storageRepo.findById(
                account.getPrimaryStorageProviderAccountId());

            Set<Integer> secondaryStorageIds =
                account.getSecondaryStorageProviderAccountIds();
            secondaryStorageAccts = new HashSet<StorageProviderAccount>();
            for(int storageId : secondaryStorageIds) {
                secondaryStorageAccts.add(storageRepo.findById(storageId));
            }

            computeAcct =
                computeRepo.findById(account.getComputeProviderAccountId());
        } catch(DBNotFoundException e) {
            String err = "Unable to retrieve storage and compute accounts " +
                         "from the database due to: " + e.getMessage();
            throw new DuraCloudRuntimeException(err);
        }

        // Accept updates to primary storage account
        System.out.println("\n-- Primary Storage Provider (" +
                               primaryStorageAcct.getProviderType() + ") --");
        primaryStorageAcct.setUsername(
            getInput("Enter Username", primaryStorageAcct.getUsername()));
        primaryStorageAcct.setPassword(
            getInput("Enter Password", primaryStorageAcct.getPassword()));

        // Accept updates to secondary storage accounts
        for(StorageProviderAccount secStorageAcct : secondaryStorageAccts) {
            System.out.println("\n-- Secondary Storage Provider (" +
                                   secStorageAcct.getProviderType() + ") --");
            secStorageAcct.setUsername(
                getInput("Enter Username", secStorageAcct.getUsername()));
            secStorageAcct.setPassword(
                getInput("Enter Password", secStorageAcct.getPassword()));
        }

        // Accept updates to compute account
        System.out.println("\n-- Compute Provider (" +
                               computeAcct.getProviderType() + ") --");
        System.out.print("  Use same username/password as primary " +
                         "storage account [Y/N]? ");
        String input = readInput();
        if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
            computeAcct.setUsername(primaryStorageAcct.getUsername());
            computeAcct.setPassword(primaryStorageAcct.getPassword());
        } else {
            computeAcct.setUsername(
                getInput("Enter Username", computeAcct.getUsername()));
            computeAcct.setPassword(
                getInput("Enter Password", computeAcct.getPassword()));
        }
        computeAcct.setElasticIp(
            getInput("Enter Elastic IP", computeAcct.getElasticIp()));
        computeAcct.setKeypair(
            getInput("Enter Key Pair Name", computeAcct.getKeypair()));
        computeAcct.setSecurityGroup(
            getInput("Enter Security Group Name",
                     computeAcct.getSecurityGroup()));

        // Review
        System.out.println("\n-- Review --");

        System.out.println("Primary Storage Provider (" +
                               primaryStorageAcct.getProviderType() + "):");
        System.out.println("  Username: " + primaryStorageAcct.getUsername());
        System.out.println("  Password: " + primaryStorageAcct.getPassword());

        for(StorageProviderAccount secStorageAcct : secondaryStorageAccts) {
            System.out.println("Secondary Storage Provider (" +
                                   secStorageAcct.getProviderType() + "):");
            System.out.println("  Username: " + secStorageAcct.getUsername());
            System.out.println("  Password: " + secStorageAcct.getPassword());
        }

        System.out.println("Compute Provider (" +
                               computeAcct.getProviderType() + "):");
        System.out.println("  Username: " + computeAcct.getUsername());
        System.out.println("  Password: " + computeAcct.getPassword());
        System.out.println("  Elastic IP: " + computeAcct.getElasticIp());
        System.out.println("  Key Pair: " + computeAcct.getKeypair());
        System.out.println("  Security Group: " + computeAcct.getSecurityGroup());

        System.out.print("Continue with saving changes [Y/N]? ");
        input = readInput();
        if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
            try {
                storageRepo.save(primaryStorageAcct);
                for(StorageProviderAccount secStorageAcct :
                    secondaryStorageAccts) {
                    storageRepo.save(secStorageAcct);
                }
                computeRepo.save(computeAcct);

                if(!account.getStatus().equals(
                    AccountInfo.AccountStatus.ACTIVE)) {
                    account.setStatus(AccountInfo.AccountStatus.ACTIVE);
                    acctRepo.save(account);
                }

                System.out.println("Changes saved successfully. " +
                                   "Account is now ACTIVE.");
            } catch(DBConcurrentUpdateException e) {
                String err = "Failure encountered attempting to update the " +
                    "database. Some of your changes could not be saved, " +
                    "please rerun this tool to ensure all data is stored " +
                    "correctly. Original error " + e.getMessage();
                throw new DuraCloudRuntimeException(err);
            }
        } else {
            System.out.println("Changes cancelled.");
        }

    }

    protected String getInput(String display, String origValue) {
        if(origValue.equals("TBD")) {
            System.out.print("  " + display + ": ");
        } else {
            System.out.print("  " + display + " [" + origValue +  "]: ");
        }

        String input = readInput();
        if(null == input || input.equals("")) {
            return origValue;
        } else {
            return input;
        }
    }

    protected String readInput() {
        return scanner.nextLine().trim();
    }

}
