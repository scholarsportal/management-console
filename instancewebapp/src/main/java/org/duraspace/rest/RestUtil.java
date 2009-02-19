package org.duraspace.rest;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.server.impl.model.HttpHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Utility class for REST operations.
 *
 * @author Bill Branan
 */
public class RestUtil {

    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Retrieves the contents of the HTTP Request.
     * @return InputStream from the request
     */
    public RequestContent getRequestContent(HttpServletRequest request,
                                            MediaType mediaType) {
        RequestContent rContent = new RequestContent();
        if(mediaType != null) {
            rContent.mimeType = mediaType.toString();
        }

        try {
            if (request.getContentLength() > 0) {
                rContent.contentStream = request.getInputStream();
            } else {
                String transferEncoding = request.getHeader("Transfer-Encoding");
                if (transferEncoding != null && transferEncoding.contains("chunked")) {
                    BufferedInputStream bis =
                        new BufferedInputStream(request.getInputStream());
                    bis.mark(2);
                    if (bis.read() > 0) {
                        bis.reset();
                        rContent.contentStream = bis;
                    }
                }
            }

            if (rContent.contentStream != null) {
                String multipartRelated = "multipart/related";
                String multipartForm = "multipart/form-data";
                if(mediaType != null) {
                    BodyPart bodyPart = null;
                    if (mediaType.isCompatible(HttpHelper.getContentType(multipartForm))) {
                        ByteArrayDataSource ds =
                            new ByteArrayDataSource(rContent.contentStream, multipartForm);
                        bodyPart = new MimeMultipart(ds).getBodyPart(0);

                    } else if (mediaType.isCompatible(HttpHelper.getContentType(multipartRelated))) {
                        ByteArrayDataSource ds =
                            new ByteArrayDataSource(rContent.contentStream, multipartRelated);
                        bodyPart = new MimeMultipart(ds).getBodyPart(1);
                    }

                    if(bodyPart != null) {
                        rContent.mimeType = bodyPart.getContentType();
                        rContent.size = bodyPart.getSize();
                        rContent.contentStream = bodyPart.getInputStream();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Could not retrieve content from request: " + e.getMessage());
            return null;
        }

        return rContent;
    }

    class RequestContent {
        InputStream contentStream = null;
        String mimeType = null;
        int size = 0;

        /**
         * @return the contentStream
         */
        public InputStream getContentStream() {
            return contentStream;
        }

        /**
         * @return the mimeType
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * @return the size
         */
        public int getSize() {
            return size;
        }
    }

}
