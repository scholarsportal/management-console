
package org.duracloud.servicesutil.util.internal;

import org.duracloud.servicesutil.util.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopServiceStarterImpl
        implements ServiceStarter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    public void start(String serviceId) {
        log.info("Service started: " + serviceId + " ...not really");
    }

}
