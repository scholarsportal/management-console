/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudServerImageRepo extends BaseRepo<ServerImage> {

    /**
     * Discovers and returns the Server Image which is considered the latest,
     * indicating that it is the preferred image to use when starting a new
     * instance.
     *
     * @return the latest Server Image
     * @throws DBNotFoundException
     */
    public ServerImage findLatest();

}
