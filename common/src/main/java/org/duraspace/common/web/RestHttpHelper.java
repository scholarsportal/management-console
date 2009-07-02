/* The contents of this file were copied and modified from the
 * Fedora repository project (http://fedora-commons.org) and
 * as such are subject to the Fedora license agreement.
 */

package org.duraspace.common.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * Provides helper methods for REST tests
 *
 * @author Bill Branan
 */
public class RestHttpHelper {

    private enum Method {GET, POST, PUT, HEAD, DELETE};

    public HttpResponse get(String url) throws Exception {
        return executeRequest(url, Method.GET, null, false, null);
    }

    public HttpResponse head(String url) throws Exception {
        return executeRequest(url, Method.HEAD, null, false, null);
    }

    public HttpResponse delete(String url) throws Exception {
        return executeRequest(url, Method.DELETE, null, false, null);
    }

    public HttpResponse post(String url,
                             String requestContent,
                             boolean formData,
                             Map<String, String> headers) throws Exception {
        return executeRequest(url, Method.POST, requestContent, formData, headers);
    }

    public HttpResponse put(String url,
                            String requestContent,
                            boolean formData,
                            Map<String, String> headers) throws Exception {
        return executeRequest(url, Method.PUT, requestContent, formData, headers);
    }

    private HttpResponse executeRequest(String url,
                                        Method method,
                                        String requestContent,
                                        boolean formData,
                                        Map<String, String> headers) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("URL must be a non-empty value");
        }

        HttpMethod httpMethod = null;
        String mimeType = "text/xml";
        if(formData) {
            mimeType = "application/x-www-form-urlencoded";
        }

        if(method.equals(Method.GET)) {
            httpMethod = new GetMethod(url);
        }else if(method.equals(Method.HEAD)) {
            httpMethod = new HeadMethod(url);
        }else if(method.equals(Method.DELETE)) {
            httpMethod = new DeleteMethod(url);
        }else if(method.equals(Method.POST)) {
            EntityEnclosingMethod postMethod = new PostMethod(url);
            if (requestContent != null) {
                postMethod.setRequestEntity(
                    new StringRequestEntity(requestContent, mimeType, "utf-8"));
                String contentLength = String.valueOf(requestContent.length());
                postMethod.setRequestHeader("Content-Length", contentLength);
            }
            httpMethod = postMethod;
        } else if(method.equals(Method.PUT)) {
            EntityEnclosingMethod putMethod = new PutMethod(url);
            if (requestContent != null) {
                putMethod.setRequestEntity(
                    new StringRequestEntity(requestContent, mimeType, "utf-8"));
            }
            httpMethod = putMethod;
        }

        if(headers != null && headers.size() > 0) {
            addHeaders(httpMethod, headers);
        }

        HttpClient client = new HttpClient();
        client.executeMethod(httpMethod);
        return new HttpResponse(httpMethod);
    }

    private void addHeaders(HttpMethod httpMethod, Map<String, String> headers) {
        Iterator<String> headerIt = headers.keySet().iterator();
        while(headerIt.hasNext()) {
            String headerName = headerIt.next();
            String headerValue = headers.get(headerName);
            if(headerName != null && headerValue != null) {
                httpMethod.addRequestHeader(headerName, headerValue);
            }
        }
    }

    public class HttpResponse {

        private final int statusCode;
        private final String responseBody;
        private final Header[] responseHeaders;
        private final Header[] responseFooters;

        HttpResponse(HttpMethod method) throws IOException {
            statusCode = method.getStatusCode();
            responseHeaders = method.getResponseHeaders();
            responseFooters = method.getResponseFooters();

            InputStream responseStream = method.getResponseBodyAsStream();
            if(responseStream != null) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(responseStream));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                responseBody = builder.toString();
            } else {
                // No response body will be available for HEAD requests
                responseBody = null;
            }
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public Header[] getResponseHeaders() {
            return responseHeaders;
        }

        public Header[] getResponseFooters() {
            return responseFooters;
        }

        public Header getResponseHeader(String headerName) {
            for (Header header : responseHeaders) {
                if (header.getName().equalsIgnoreCase(headerName)) {
                    return header;
                }
            }
            return null;
        }
    }
}
