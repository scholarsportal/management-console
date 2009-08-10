
package org.duracloud.servicesadmin.util;

import javax.servlet.http.HttpServletRequest;

import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHelper {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestHelper.class);

    private ServiceSerializer serializer;

    public String getServiceIdParameter(HttpServletRequest request)
            throws Exception {
        int len = request.getContentLength();
        if (len < 1) {
            String msg = "Error: on service-id parameter was provided.";
            log.error(msg);
            throw new Exception(msg);
        }

        byte[] buffer = new byte[len];
        request.getInputStream().readLine(buffer, 0, len);

        String content = new String(buffer);

        ComputeServiceBean bean = getSerializer().deserializeBean(content);
        return bean.getServiceName();
    }

    public ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }

    public void setSerializer(ServiceSerializer serializer) {
        this.serializer = serializer;
    }
}
