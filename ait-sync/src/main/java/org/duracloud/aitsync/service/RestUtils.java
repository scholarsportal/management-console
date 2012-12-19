package org.duracloud.aitsync.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 *
 */
public interface RestUtils {
    public InputStream getInputStream(HttpServletRequest request)
        throws IOException;
    
    public void setStatus(HttpServletResponse response, HttpStatus status);
}
