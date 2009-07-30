
package org.duracloud.servicesutil.client;

import java.io.File;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUploadClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RestHttpHelper rester;

    public HttpResponse postServiceBundle(String url, File file)
            throws Exception {
        log.debug("URL: " + url + ", FILE: " + file);

        Part[] parts = {new FilePart(file.getName(), file)};

        return getRester().multipartPost(url, parts);
    }

    public RestHttpHelper getRester() {
        return rester;
    }

    public void setRester(RestHttpHelper rester) {
        this.rester = rester;
    }

}
