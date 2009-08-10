
package org.duracloud.servicesutil.client;

import java.io.File;

import java.util.Map;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;

import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUploadClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RestHttpHelper rester;

    private ServiceSerializer serializer;

    public HttpResponse postServiceBundle(String url, File file)
            throws Exception {
        log.debug("URL: " + url + ", FILE: " + file);

        Part[] parts = {new FilePart(file.getName(), file)};

        return getRester().multipartPost(url, parts);
    }

    public HttpResponse deleteServiceBundle(String url, String bundleId)
            throws Exception {
        log.debug("URL: " + url + ", BUNDLE-ID: " + bundleId);

        ComputeServiceBean bean = new ComputeServiceBean(bundleId);
        String requestContent = getSerializer().serialize(bean);
        Map<String, String> headers = null;

        return getRester().post(url, requestContent, headers);
    }

    public HttpResponse getServiceListing(String url) throws Exception {
        log.debug("URL: " + url);
        return getRester().get(url);
    }

    public RestHttpHelper getRester() {
        return rester;
    }

    public void setRester(RestHttpHelper rester) {
        this.rester = rester;
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
