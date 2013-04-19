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
                ContentStore secondary =
                    getSecondaryStore(storeManager, primary.getStoreId());

                // Get list of spaces to compare
                List<String> primarySpaces = getSpaces(host, primary);
                List<String> secondarySpaces = getSpaces(host, secondary);

                if(primarySpaces.size() != secondarySpaces.size()) {
                    info.addIssue("The spaces listings do not match " +
                                  "between primary and secondary providers.");
                }

                // Determine item count for each space in both providers
                countSpaces(host, info, primary, primarySpaces, true);
                countSpaces(host, info, secondary, secondarySpaces, false);

                // Compare the space counts between providers
                compareSpaces(info);
                report.addDupInfo(host, info);
            } catch (Exception e) {
                String error = e.getClass() + " exception encountered while " +
                    "running dup monitor for host " + host +
                    ". Exception message: " + e.getMessage();
                log.error(error);
                info.addIssue(error);
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
     * Get the secondary storage provider. Note that it is expected that
     * each account has only 2 storage providers, primary and one secondary.
     */
    protected ContentStore getSecondaryStore(ContentStoreManager storeManager,
                                             String primaryStoreId)
        throws ContentStoreException {
        Map<String, ContentStore> stores =
            new HashMap(storeManager.getContentStores());
        if(stores.keySet().size() != 2) {
            String error = "Expecting exactly 2 storage providers " +
                           "(one primary and one secondary) " +
                           ". Instead found: " + stores.size();
            throw new RuntimeException(error);
        }

        stores.remove(primaryStoreId);
        return stores.values().iterator().next();
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
        for(String spaceId : spaces) {
            try {
                long count = getSpaceCount(store, spaceId);
                if(primary) {
                    info.addPrimarySpace(spaceId, count);
                } else {
                    info.addSecondarySpace(spaceId, count);
                }
            } catch(ContentStoreException e) {
                String error = "ContentStoreException encountered " +
                    "attempting to get count of space " + spaceId +
                    " for duplication check of host " + host +
                    ". Exception message: " + e.getMessage();
                log.error(error);
                info.addIssue(error);
                if(primary) {
                    info.addPrimarySpace(spaceId, -1);
                } else {
                    info.addSecondarySpace(spaceId, -1);
                }
            }
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
    protected void compareSpaces(DuplicationInfo info) {
        Map<String, Long> primarySpaces = info.getPrimarySpaceCounts();
        Map<String, Long> secondarySpaces = info.getSecondarySpaceCounts();

        for(String spaceId : primarySpaces.keySet()) {
            Long primaryCount = primarySpaces.get(spaceId);
            Long secondaryCount = secondarySpaces.get(spaceId);

            if(null == secondaryCount) {
                info.addIssue("The secondary provider is missing space: " +
                              spaceId);
            } else if(!primaryCount.equals(secondaryCount)) {
                info.addIssue("The content item counts for the space " +
                    spaceId + " do not match between primary and secondary " +
                    "providers. Primary count: " + primaryCount +
                    ". Secondary count: " + secondaryCount + ".");
            }
        }
    }

}
