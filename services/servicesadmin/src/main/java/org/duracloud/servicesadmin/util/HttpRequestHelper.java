
package org.duracloud.servicesadmin.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHelper {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestHelper.class);

    private ServiceSerializer serializer;

    private static final String SERVLET_PREFIX = "configure/";

    public String getConfigIdFromRestURL(HttpServletRequest request)
            throws Exception {
        String pathInfo = request.getPathInfo();
        log.debug("getConfigIdFromRestURL, pathInfo: '" + pathInfo + "'");

        int index = pathInfo.indexOf(SERVLET_PREFIX);
        if (index == -1) {
            StringBuilder msg = new StringBuilder();
            msg.append("Unable to find SERVLET_PREFIX [");
            msg.append(SERVLET_PREFIX + "]");
            msg.append("in URL [" + pathInfo + "]");
            log.error(msg.toString());
            throw new Exception(msg.toString());
        }
        String configId = pathInfo.substring(index + SERVLET_PREFIX.length());
        log.debug("getConfigIdFromRestURL, found configId: '" + configId + "'");
        return configId;
    }

    public Map<String, String> getConfigProps(HttpServletRequest request)
            throws Exception {

        String content = getRequestContent(request);
        log.debug("getConfigProps(request) content: '" + content + "'");
        Map<String, String> props = SerializationUtil.deserializeMap(content);

        return props;
    }

    public String getServiceIdParameter(HttpServletRequest request)
            throws Exception {

        String content = getRequestContent(request);
        log.debug("getServiceIdParameter(request) content: '" + content + "'");
        ComputeServiceBean bean = getSerializer().deserializeBean(content);
        return bean.getServiceName();
    }

    private String getRequestContent(HttpServletRequest request)
            throws Exception {
        int len = request.getContentLength();
        if (len < 1) {
            String msg = "Error: No request content was provided.";
            log.error(msg);
            throw new Exception(msg);
        }

        byte[] buffer = new byte[len];
        int bytesRead = 0;
        while (bytesRead != -1) {
            bytesRead +=
                    request.getInputStream().readLine(buffer, bytesRead, len);
        }
        return new String(buffer);
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
