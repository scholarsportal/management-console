package org.duracloud.servicesutil.util;

import org.duracloud.services.ComputeService;

/**
 * @author Andrew Woods
 *         Date: Dec 14, 2009
 */
public interface ServiceStatusReporter {

    /**
     * This method returns the status as defined in:
     * ComputeService.ServiceStatus
     * for the arg service.
     *
     * @param serviceId of service to status
     * @return status of service
     * @exception Exception when sevice with arg serviceId not found
     */
    public abstract ComputeService.ServiceStatus getStatus(String serviceId)
        throws Exception;
}
