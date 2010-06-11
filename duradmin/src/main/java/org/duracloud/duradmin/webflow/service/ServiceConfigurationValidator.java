
package org.duracloud.duradmin.webflow.service;

import org.duracloud.serviceconfig.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.validation.ValidationContext;

public class ServiceConfigurationValidator {

    private Logger log = LoggerFactory.getLogger(ServiceConfigurationValidator.class);

    public void validateConfigureService(ServiceInfo service,
                                         ValidationContext context) {
        log.info("validating " + service);
        log.info("not implemented !");
    }
}
