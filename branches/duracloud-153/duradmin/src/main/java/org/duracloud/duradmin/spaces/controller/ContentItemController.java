package org.duracloud.duradmin.spaces.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ServicesManager;
import org.duracloud.controller.AbstractRestController;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.error.ContentStoreException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ContentItemController extends  AbstractRestController<ContentItem> {

    protected final Logger log = Logger.getLogger(getClass());

	private ContentStoreManager contentStoreManager;
	
	public ContentItemController(){
		super(null);
		setValidator(new Validator(){
			@Override
			public boolean supports(Class clazz) {
				return clazz == ContentItem.class;
			}
			
			@Override
			public void validate(Object target, Errors errors) {
				ContentItem command = (ContentItem)target;

		        if (!StringUtils.hasText(command.getStoreId())) {
		            errors.rejectValue("storeId","required");
		        }

				if (!StringUtils.hasText(command.getSpaceId())) {
		            errors.rejectValue("spaceId","required");
		        }

		        if (!StringUtils.hasText(command.getContentId())) {
		            errors.rejectValue("contentId","required");
		        }
			}
		});

	}
    
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

	
	

	@Override
	protected ModelAndView get(HttpServletRequest request,
			HttpServletResponse response, ContentItem ci,
			BindException errors) throws Exception {
		ContentItem contentItem = new ContentItem();
        SpaceUtil.populateContentItem(
        							  getBaseURL(request),
        							  contentItem,
                                      ci.getSpaceId(),
                                      ci.getContentId(),
                                      getContentStore(ci),
                                      getServicesManager());
        return createModel(contentItem);
	}
	
	
	@Override
	protected ModelAndView post(HttpServletRequest request,
			HttpServletResponse response, ContentItem ci,
			BindException errors) throws Exception {
        ContentStore contentStore = getContentStore(ci);
        
        ContentUploadHelper.executeUploadTask(request, ci,contentStore);
        ContentItem result = new ContentItem();
        SpaceUtil.populateContentItem(getBaseURL(request),result, ci.getSpaceId(), ci.getContentId(),contentStore, servicesManager);
        return createModel(ci);
	}
	
	private String getBaseURL(HttpServletRequest request) throws MalformedURLException{
		URL url = new URL(request.getRequestURL().toString());
		String baseURL = url.getProtocol() + "://" + url.getHost() + ":" +(url.getPort() != 80 ? url.getPort() : "") + request.getContextPath();
		return baseURL;
	}

	private ModelAndView createModel(ContentItem ci){
        return new ModelAndView("jsonView", "contentItem",ci);
	}
	
	protected ContentStore getContentStore(ContentItem contentItem) throws ContentStoreException{
		return contentStoreManager.getContentStore(contentItem.getStoreId());
	}



	private ContentItem create(HttpServletRequest request) {
		ContentItem ci = new ContentItem();
		ci.setStoreId(request.getParameter("storeId"));	
		ci.setSpaceId(request.getParameter("spaceId"));
		ci.setContentId(request.getParameter("contentId"));
		return ci;
	}


}