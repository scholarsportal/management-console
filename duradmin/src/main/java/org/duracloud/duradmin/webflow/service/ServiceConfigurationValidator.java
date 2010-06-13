/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
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
