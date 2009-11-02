
package org.duracloud.services.util;

import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.services.beans.ComputeServiceBean;

public interface ServiceSerializer {

    public abstract String serialize(List<ComputeService> services)
            throws Exception;

    public abstract List<ComputeServiceBean> deserializeList(String xml);

    public abstract String serialize(ComputeServiceBean bean) throws Exception;

    public abstract ComputeServiceBean deserializeBean(String xml);

}
