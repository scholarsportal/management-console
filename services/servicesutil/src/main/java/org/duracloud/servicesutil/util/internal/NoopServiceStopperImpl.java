
package org.duracloud.servicesutil.util.internal;

import org.duracloud.servicesutil.util.ServiceStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopServiceStopperImpl
        implements ServiceStopper {

    private final Logger log = LoggerFactory.getLogger(NoopServiceStopperImpl.class);

    /**
     * {@inheritDoc}
     */
    public void stop(String serviceId) {
        log.info("Service stopped: " + serviceId + " ...not really");
    }

}
