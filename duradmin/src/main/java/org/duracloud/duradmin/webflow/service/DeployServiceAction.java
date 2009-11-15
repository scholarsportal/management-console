
package org.duracloud.duradmin.webflow.service;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.binding.message.MessageContext;

public class DeployServiceAction
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(DeployServiceAction.class);

    public boolean execute(ServiceInfo service, MessageContext messageContext)
            throws Exception {
        return true;
    }
}