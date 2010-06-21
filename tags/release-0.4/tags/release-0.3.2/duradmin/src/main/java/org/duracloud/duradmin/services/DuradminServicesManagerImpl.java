package org.duracloud.duradmin.services;

import org.duracloud.duradmin.config.DuradminConfig;

/**
 * @author Andrew Woods
 *         Date: Mar 25, 2010
 */
public class DuradminServicesManagerImpl extends org.duracloud.client.ServicesManagerImpl {

    public DuradminServicesManagerImpl() {
        super(DuradminConfig.getDuraServiceHost(),
              DuradminConfig.getDuraServicePort(),
              DuradminConfig.getDuraServiceContext());
    }
}
