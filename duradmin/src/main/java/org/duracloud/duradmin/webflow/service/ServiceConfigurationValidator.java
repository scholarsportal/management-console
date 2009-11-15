
package org.duracloud.duradmin.webflow.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.binding.validation.ValidationContext;

public class ServiceConfigurationValidator {

    private Log log = LogFactory.getLog(getClass());

    public void validateConfigureService(ServiceInfo service,
                                         ValidationContext context) {
        log.info("validating " + service);
        log.info("not implemented !");
    }
}
