/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.error.DBNotFoundException;

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
     * @throws DBNotFoundException
     */
    public DuracloudInstanceService getInstance(DuracloudInstance instance)
        throws DBNotFoundException;
}
