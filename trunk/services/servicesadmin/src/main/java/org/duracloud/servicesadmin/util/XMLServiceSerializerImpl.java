
package org.duracloud.servicesadmin.util;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duraspace.common.util.SerializationUtil;

public class XMLServiceSerializerImpl
        implements ServiceSerializer {

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    public String serialize(List<ComputeService> services) throws Exception {
        List<ComputeServiceBean> beans = new ArrayList<ComputeServiceBean>();
        for (ComputeService service : services) {
            beans.add(new ComputeServiceBean(service.describe()));
        }
        return SerializationUtil.serializeList(beans);
    }

}
