package org.duracloud.duradmin.spaces.controller;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ContentItemController implements Controller {

    protected final Logger log = Logger.getLogger(getClass());

	private ContentStoreManager contentStoreManager;

    
    public ContentStoreManager getContentStoreManager() {
		return contentStoreManager;
	}


	public void setContentStoreManager(ContentStoreManager contentStoreManager) {
		this.contentStoreManager = contentStoreManager;
	}

	private ServicesManager servicesManager;
	
    
    
	public ServicesManager getServicesManager() {
		return servicesManager;
	}


	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}


	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ContentItem ci = new ContentItem();
		String method = request.getMethod();

		if(method == "GET"){
			return handleGet(request,response);
		}

		return new ModelAndView("jsonView", "contentItem",ci);

	}
	
	


	private ModelAndView handleGet(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ContentItem ci = create(request);
        ContentStore c = contentStoreManager.getContentStore(ci.getStoreId());

		URL url = new URL(request.getRequestURL().toString());
		String baseURL = url.getProtocol() + "://" + url.getHost() + ":" +(url.getPort() != 80 ? url.getPort() : "") + request.getContextPath();
        SpaceUtil.populateContentItem(
        							  baseURL,
        							  ci,
                                      ci.getSpaceId(),
                                      ci.getContentId(),
                                      c,
                                      getServicesManager());
		
        return new ModelAndView("jsonView", "contentItem",ci);
	}


	private ContentItem create(HttpServletRequest request) {
		ContentItem ci = new ContentItem();
		ci.setStoreId(request.getParameter("storeId"));	
		ci.setSpaceId(request.getParameter("spaceId"));
		ci.setContentId(request.getParameter("contentId"));
		ControllerUtils.checkContentItemId(ci.getSpaceId(), ci.getContentId());
		return ci;
	}


}