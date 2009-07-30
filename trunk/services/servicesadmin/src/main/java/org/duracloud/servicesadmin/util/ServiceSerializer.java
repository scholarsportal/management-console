
package org.duracloud.servicesadmin.util;

import java.util.List;

import org.duracloud.services.ComputeService;

public interface ServiceSerializer {

    public abstract String serialize(List<ComputeService> services)
            throws Exception;

}
