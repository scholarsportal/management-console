package org.duracloud.mainwebapp.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.common.util.ExceptionUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class DuraHandlerExceptionResolver
        extends SimpleMappingExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {
        Map<String, String> errors = new HashMap<String, String>();

        String message = ex.getMessage();
        String stack = ExceptionUtil.getStackTraceAsString(ex);

        errors.put("message", message);
        errors.put("stack", stack);

        return new ModelAndView("error", "errors", errors);
    }

}
