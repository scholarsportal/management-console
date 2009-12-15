package org.duracloud.servicesutil.util.internal;

import org.duracloud.services.ComputeService;
import org.duracloud.services.common.error.ServiceException;
import org.duracloud.servicesutil.util.ServiceStatusReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Dec 14, 2009
 */
public class ServiceStatusReporterImpl implements ServiceStatusReporter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<ComputeService> duraServices;

    public ComputeService.ServiceStatus getStatus(String serviceId)
        throws Exception {
        log.debug("Status for: '" + serviceId + "'");

        ComputeService.ServiceStatus status = null;
        for (ComputeService service : getDuraServices()) {
            log.debug("service in list: '" + service.getServiceId() + "'");

            if (serviceId.equalsIgnoreCase(service.getServiceId())) {
                status = service.getServiceStatus();
                log.debug("status found: '" + status.name() + "");
                break;
            }
        }

        if (null == status) {
            throw new ServiceException("Service not found: " + serviceId);
        }

        return status;
    }

    public List<ComputeService> getDuraServices() {
        return duraServices;
    }

    public void setDuraServices(List<ComputeService> duraServices) {
        this.duraServices = duraServices;
    }

}