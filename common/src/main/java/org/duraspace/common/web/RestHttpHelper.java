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
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import org.duraspace.common.util.IOUtil;

/**
 * Provides helper methods for REST tests
 *
 * @author Bill Branan
 */
public class RestHttpHelper {

    private enum Method {GET, POST, PUT, HEAD, DELETE};

    private static final String XML_MIMETYPE = "text/xml";
    private static final String NO_MIMETYPE = "none";

    public HttpResponse get(String url) throws Exception {
        return executeRequest(url, Method.GET, null, null, null);
    }

    public HttpResponse head(String url) throws Exception {
        return executeRequest(url, Method.HEAD, null, null, null);
    }

    public HttpResponse delete(String url) throws Exception {
        return executeRequest(url, Method.DELETE, null, null, null);
    }

    public HttpResponse post(String url,
                             String requestContent,
                             Map<String, String> headers) throws Exception {
        String mimeType = XML_MIMETYPE;
        if (requestContent == null) {
            mimeType = NO_MIMETYPE;
        }
        return executeRequest(url,
                              Method.POST,
                              requestContent,
                              mimeType,
                              headers);
    }

    public HttpResponse post(String url,
                             String requestContent,
                             String mimeType,
                             Map<String, String> headers) throws Exception {
        return executeRequest(url,
                              Method.POST,
                              requestContent,
                              mimeType,
                              headers);
    }

    public HttpResponse post(String url,
                             InputStream requestContent,
                             String contentSize,
                             String mimeType,
                             Map<String, String> headers) throws Exception {
        return executeRequest(url,
                              Method.POST,
                              requestContent,
                              contentSize,
                              mimeType,
                              headers);
    }

    public HttpResponse put(String url,
                            String requestContent,
                            Map<String, String> headers) throws Exception {
        String mimeType = XML_MIMETYPE;
        if (requestContent == null) {
            mimeType = NO_MIMETYPE;
        }
        return executeRequest(url,
                              Method.PUT,
                              requestContent,
                              mimeType,
                              headers);
    }

    public HttpResponse put(String url,
                            String requestContent,
                            String mimeType,
                            Map<String, String> headers) throws Exception {
        return executeRequest(url,
                              Method.PUT,
                              requestContent,
                              mimeType,
                              headers);
    }

    public HttpResponse put(String url,
                            InputStream requestContent,
                            String contentSize,
                            String mimeType,
                            Map<String, String> headers) throws Exception {
        return executeRequest(url,
                              Method.PUT,
                              requestContent,
                              contentSize,
                              mimeType,
                              headers);
    }

    private HttpResponse executeRequest(String url,
                                        Method method,
                                        String requestContent,
                                        String mimeType,
                                        Map<String, String> headers)
            throws Exception {
        InputStream streamContent = null;
        String contentLength = null;
        if (requestContent != null) {
            streamContent = IOUtil.writeStringToStream(requestContent);
            contentLength = String.valueOf(requestContent.length());
        }

        return executeRequest(url,
                              method,
                              streamContent,
                              contentLength,
                              mimeType,
                              headers);
    }

    private HttpResponse executeRequest(String url,
                                        Method method,
                                        InputStream requestContent,
                                        String contentLength,
                                        String mimeType,
                                        Map<String, String> headers) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("URL must be a non-empty value");
        }

        HttpMethod httpMethod = null;
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
                    new InputStreamRequestEntity(requestContent, mimeType));
                postMethod.setRequestHeader("Content-Length", contentLength);
            }
            httpMethod = postMethod;
        } else if(method.equals(Method.PUT)) {
            EntityEnclosingMethod putMethod = new PutMethod(url);
            if (requestContent != null) {
                putMethod.setRequestEntity(
                    new InputStreamRequestEntity(requestContent, mimeType));
                putMethod.setRequestHeader("Content-Length", contentLength);
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
        private final InputStream responseStream;
        private final Header[] responseHeaders;
        private final Header[] responseFooters;

        HttpResponse(HttpMethod method) throws IOException {
            statusCode = method.getStatusCode();
            responseHeaders = method.getResponseHeaders();
            responseFooters = method.getResponseFooters();
            responseStream = method.getResponseBodyAsStream();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public InputStream getResponseStream() {
            return responseStream;
        }

        public String getResponseBody() throws IOException {
            if(responseStream != null) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(responseStream));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                responseStream.close();
                return builder.toString();
            } else {
                // No response body will be available for HEAD requests
                return null;
            }
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
