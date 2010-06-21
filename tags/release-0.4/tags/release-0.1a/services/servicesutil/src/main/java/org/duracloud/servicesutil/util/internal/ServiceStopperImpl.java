
package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServiceStopperImpl
        implements ServiceStopper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<ComputeService> duraServices;

    /**
     * {@inheritDoc}
     */
    public void stop(String serviceId) {
        for(ComputeService service : duraServices) {
            try {
                String id = service.getServiceId();
                if(serviceId.equals(id) || serviceId.contains(id)) {
                    log.info("Stopping Service: " + serviceId);
                    service.stop();
                    break;
                }
            } catch(Exception e) {
                log.error("Error stopping service: " + service.toString());
            }
        }
    }

    public List<ComputeService> getDuraServices() {
        return duraServices;
    }

    public void setDuraServices(List<ComputeService> duraServices) {
        this.duraServices = duraServices;
    }

}