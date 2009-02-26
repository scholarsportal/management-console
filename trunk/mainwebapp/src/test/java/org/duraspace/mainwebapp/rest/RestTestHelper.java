/* The contents of this file were copied and modified from the
 * Fedora repository project (http://fedora-commons.org) and
 * as such are subject to the Fedora license agreement.
 */

package org.duraspace.mainwebapp.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * Provides helper methods for REST tests
 *
 * @author Bill Branan
 */
public class RestTestHelper {

    private enum Method {GET, POST, PUT, DELETE};

    public HttpResponse get(String url) throws Exception {
        return executeRequest(url, Method.GET, null);
    }

    public HttpResponse delete(String url) throws Exception {
        return executeRequest(url, Method.DELETE, null);
    }

    public HttpResponse post(String url, String requestContent) throws Exception {
        return executeRequest(url, Method.POST, requestContent);
    }

    public HttpResponse put(String url, String requestContent) throws Exception {
        return executeRequest(url, Method.PUT, requestContent);
    }

    private HttpResponse executeRequest(String url,
                                        Method method,
                                        String requestContent) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("URL must be a non-empty value");
        }

        HttpMethod httpMethod = null;

        if(method.equals(Method.GET)) {
            httpMethod = new GetMethod(url);
        }else if(method.equals(Method.DELETE)) {
            httpMethod = new DeleteMethod(url);
        }else if(method.equals(Method.POST)) {
            EntityEnclosingMethod postMethod = new PostMethod(url);
            if (requestContent != null) {
                postMethod.setRequestEntity(
                    new StringRequestEntity(requestContent, "text/xml", "utf-8"));
            }
            httpMethod = postMethod;
        } else if(method.equals(Method.PUT)) {
            EntityEnclosingMethod putMethod = new PutMethod(url);
            if (requestContent != null) {
                putMethod.setRequestEntity(
                    new StringRequestEntity(requestContent, "text/xml", "utf-8"));
            }
            httpMethod = putMethod;
        }

        HttpClient client = new HttpClient();
        client.executeMethod(httpMethod);
        return new HttpResponse(httpMethod);
    }

    class HttpResponse {

        private final int statusCode;
        private final String responseBody;
        private final Header[] responseHeaders;
        private final Header[] responseFooters;

        HttpResponse(HttpMethod method) throws IOException {
            statusCode = method.getStatusCode();
            responseHeaders = method.getResponseHeaders();
            responseFooters = method.getResponseFooters();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(method.getResponseBodyAsStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            responseBody = builder.toString();
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
