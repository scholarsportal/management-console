
package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServiceStarterImpl
        implements ServiceStarter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<ComputeService> duraServices;   

    /**
     * {@inheritDoc}
     */
    public void start(String serviceId) {
        for(ComputeService service : duraServices) {
            try {
                String id = service.getServiceId();
                if(serviceId.equals(id) || serviceId.contains(id)) {
                    log.info("Starting Service: " + serviceId);
                    service.start();
                    break;
                }
            } catch(Exception e) {
                log.error("Error starting service: " + service.toString());
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