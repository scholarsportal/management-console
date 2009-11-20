
package org.duracloud.duradmin.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.client.ContentStoreException;
import org.duracloud.client.error.ServicesException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class SimpleExceptionResolver
        extends SimpleMappingExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {

        ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", ex.getMessage());

        if (!(ex instanceof ContentStoreException)
                && !(ex instanceof ServicesException)) {
            StringWriter stackTrace = new StringWriter();
            ex.printStackTrace(new PrintWriter(stackTrace));
            mav.addObject("stack", stackTrace.toString());
        }
        return mav;
    }
}
