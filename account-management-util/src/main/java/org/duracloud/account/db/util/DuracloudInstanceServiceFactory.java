/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import org.duracloud.account.db.model.DuracloudInstance;

/**
 * @author Andrew Woods
 *         Date: 4/10/11
 */
public interface DuracloudInstanceServiceFactory {

    /**
     * This method provides an DuracloudInstanceService wrapping the arg
     * instance.
     *
     * @param instance to be wrapped and exposed as a service
     * @return DuracloudInstanceService
     */
    public DuracloudInstanceService getInstance(DuracloudInstance instance);
}
