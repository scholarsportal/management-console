/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
