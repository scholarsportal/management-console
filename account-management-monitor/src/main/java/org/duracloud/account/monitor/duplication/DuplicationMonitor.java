/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.duplication;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.monitor.common.BaseMonitor;
import org.duracloud.account.monitor.duplication.domain.DuplicationInfo;
import org.duracloud.account.monitor.duplication.domain.DuplicationReport;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.storage.util.StorageProviderUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages the actual monitoring of content duplication across
 * DuraCloud accounts with multiple storage providers.
 *
 * @author Bill Branan
 *         Date: 4/16/13
 */
public class DuplicationMonitor extends BaseMonitor {

    public static final String ALL_SPACES = "ALL";
	private static final String PORT = "443";
	private static final String CONTEXT = "durastore";
    private static final List<String> ADMIN_SPACES =
        Arrays.asList("x-duracloud-admin", "x-service-out");

    private Map<String, String> dupHosts;

    public DuplicationMonitor(DuracloudAccountRepo acctRepo,
                              DuracloudInstanceRepo instanceRepo,
                              DuracloudServerImageRepo imageRepo,
                              Map<String, String> dupHosts) {
        this.log = LoggerFactory.getLogger(DuplicationMonitor.class);
        super.init(acctRepo, instanceRepo, imageRepo);
        this.dupHosts = dupHosts;
    }

    /**
     * This method performs the duplication checks. These checks compare
     * the number of content items in identically named spaces.
     *
     * @return DuplicationReport report
     */
    public DuplicationReport monitorDuplication() {
        log.info("starting duplication monitor");
        DuplicationReport report = new DuplicationReport();

        for(String host : dupHosts.keySet()) {
            DuplicationInfo info = new DuplicationInfo(host);
            try {
                // Connect to storage providers
                ContentStoreManager storeManager = getStoreManager(host);
                ContentStore primary = storeManager.getPrimaryContentStore();
                String primaryStoreId = primary.getStoreId();
                List<ContentStore> secondaryList =
                    getSecondaryStores(storeManager, primaryStoreId);

                // Get primary space listing and count
                List<String> primarySpaces = getSpaces(host, primary);
                countSpaces(host, info, primary, primarySpaces, true);

                // Get space listing and space counts for secondary providers
                for(ContentStore secondary : secondaryList) {
                    List<String> secondarySpaces = getSpaces(host, secondary);
                    if(primarySpaces.size() != secondarySpaces.size()) {
                        info.addIssue("The spaces listings do not match " +
                                      "between primary and secondary " +
                                      "provider: " +
                                      secondary.getStorageProviderType());
                    }
                    // Determine item count for secondary provider spaces
                    countSpaces(host, info, secondary, secondarySpaces, false);
                }

                // Compare the space counts between providers
                compareSpaces(primaryStoreId, info);
            } catch (Exception e) {
                String error = e.getClass() + " exception encountered while " +
                    "running dup monitor for host " + host +
                    ". Exception message: " + e.getMessage();
                log.error(error);
                info.addIssue(error);
            } finally {
                report.addDupInfo(host, info);
            }
        }

        return report;
    }

    /*
     * Create the store manager to connect to this DuraCloud account instance
     */
    private ContentStoreManager getStoreManager(String host)
        throws DBNotFoundException {
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(host, PORT, CONTEXT);
        Credential credential = getRootCredential(getAccount(host));
        storeManager.login(credential);
        return storeManager;
    }

    /*
     * Get the secondary storage providers.
     */
    protected List<ContentStore> getSecondaryStores(ContentStoreManager storeManager,
                                                    String primaryStoreId)
        throws ContentStoreException {
        Map<String, ContentStore> stores =
            new HashMap(storeManager.getContentStores());
        List<ContentStore> secondaryStores = new ArrayList<>();

        for(ContentStore store : stores.values()) {
            if(!store.getStoreId().equals(primaryStoreId)) {
                secondaryStores.add(store);
            }
        }
        return secondaryStores;
    }

    /*
     * Determine the spaces which need to be compared. If a space list is
     * provided for a given account, use that list, otherwise compare
     * all user spaces.
     */
    protected List<String> getSpaces(String host, ContentStore store)
        throws ContentStoreException {
        List<String> spaceList;
        String spaces = dupHosts.get(host);
        if(spaces.equals(ALL_SPACES)) { // Need to compare all spaces
            spaceList = new ArrayList(store.getSpaces());
            spaceList.removeAll(ADMIN_SPACES);
        } else { // Only compare specific set of spaces
            String[] spacesToCompare = spaces.split(",");
            spaceList = Arrays.asList(spacesToCompare);
        }
        return spaceList;
    }

    /*
     * Perform a content count for all spaces in the list for the given account
     */
    protected void countSpaces(String host,
                               DuplicationInfo info,
                               ContentStore store,
                               List<String> spaces,
                               boolean primary) {
        String storeId = store.getStoreId();
        String storeType = store.getStorageProviderType();
        for(String spaceId : spaces) {
            boolean doCount = false;
            // Handle spaces which include space ID, formatted like: spaceId:storeId
            if(spaceId.indexOf(":") > -1) {
                String[] spaceAndStoreId = spaceId.split(":");
                spaceId = spaceAndStoreId[0];
                if(primary || storeId.equals(spaceAndStoreId[1])) {
                    doCount  = true;
                }
            } else {
                doCount = true;
            }

            if(doCount) {
                countSpace(host, spaceId, storeId, storeType, info, store);
            }
        }
    }

    private void countSpace(String host,
                            String spaceId,
                            String storeId,
                            String storeType,
                            DuplicationInfo info,
                            ContentStore store) {
        try {
            log.info("Counting space '" + spaceId + "' in store " +
                      storeType + " for host " + host + " ...");
            long count = getSpaceCount(store, spaceId);
            log.info("Count for space '" + spaceId + "' in store " +
                     storeType + " for host " + host + ": " + count);
            info.addSpaceCount(storeId, spaceId, count);
        } catch(ContentStoreException e) {
            String error = "ContentStoreException encountered " +
                "attempting to get count of space " + spaceId +
                " for duplication check of host " + host +
                ". Exception message: " + e.getMessage();
            log.error(error);
            info.addIssue(error);
            info.addSpaceCount(storeId, spaceId, -1);
        }
    }

    /*
     * Count the number of content items in a space in a DuraCloud account
     */
    private long getSpaceCount(ContentStore store, String spaceId)
        throws ContentStoreException {
        return StorageProviderUtil.count(store.getSpaceContents(spaceId));
    }

    /*
     * Compare the counted number of space items between storage providers
     */
    protected void compareSpaces(String primaryStoreId, DuplicationInfo info) {
        String spaces = dupHosts.get(info.getHost());
        Map<String, Long> primarySpaces = info.getSpaceCounts(primaryStoreId);
        for(String storeId : info.getStoreIds()) {
            Map<String, Long> secondarySpaces = info.getSpaceCounts(storeId);

            for(String spaceId : primarySpaces.keySet()) {
                // Determine if a space comparison should occur
                boolean doCompare = false;
                if(null == spaces || spaces.equals(ALL_SPACES)) {
                    doCompare = true;
                } else {
                    String[] spacesToCompare = spaces.split(",");
                    List<String> spaceList = Arrays.asList(spacesToCompare);
                    if(spaceList.contains(spaceId)) {
                        doCompare = true;
                    } else { // Check space and store IDs
                        for(String spacesSpaceId : spaceList) {
                            String[] spaceAndStoreId = spacesSpaceId.split(":");
                            if(spaceId.equals(spaceAndStoreId[0]) &&
                               storeId.equals(spaceAndStoreId[1])) {
                                doCompare = true;
                            }
                        }
                    }
                }

                // Do the comparison
                if(doCompare) {
                    Long primaryCount = primarySpaces.get(spaceId);
                    Long secondaryCount = secondarySpaces.get(spaceId);

                    if(null == secondaryCount) {
                        info.addIssue("The secondary provider (ID=" + storeId +
                                      ") is missing space: " + spaceId);
                    } else if(!primaryCount.equals(secondaryCount)) {
                        info.addIssue("The content item counts for the space " +
                            spaceId + " do not match between primary and secondary " +
                            "providers. Primary count: " + primaryCount +
                            ". Secondary (ID=" + storeId + ") " +
                            "count: " + secondaryCount + ".");
                    }
                }
            }
        }
    }

}
