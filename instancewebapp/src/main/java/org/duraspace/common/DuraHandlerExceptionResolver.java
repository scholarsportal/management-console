package org.duraspace.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class DuraHandlerExceptionResolver extends SimpleMappingExceptionResolver
{

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
	{
		Map<String, String> errors = new HashMap<String, String>();
		errors.put("error", ex.getMessage());
		return new ModelAndView("error", errors);
	}

}
