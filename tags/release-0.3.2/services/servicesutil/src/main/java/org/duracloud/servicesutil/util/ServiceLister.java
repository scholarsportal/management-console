
package org.duracloud.servicesutil.util;

import java.util.List;

import org.duracloud.services.ComputeService;

public interface ServiceLister {

    public abstract List<ComputeService> getDuraServices();

}
