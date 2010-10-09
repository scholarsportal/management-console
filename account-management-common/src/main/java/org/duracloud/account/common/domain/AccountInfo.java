/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import org.duracloud.storage.domain.StorageProviderType;

import java.util.List;

/**
 * Read-only summary of salient account details.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountInfo {
    private String id;
    private String subDomain;
    private String acctName;
    private String orgName;
    private String department;

    private PaymentInfo paymentInfo;
    private DuracloudUser owner;
    private List<StorageProviderType> storageProviders;

}
