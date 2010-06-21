package org.duracloud.duradmin.spaces.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class SpacesController implements Controller {

    protected final Logger log = Logger.getLogger(getClass());
    
    public ContentStoreManager getContentStoreManager() {
		return contentStoreManager;
	}


	public void setContentStoreManager(ContentStoreManager contentStoreManager) {
		this.contentStoreManager = contentStoreManager;
	}


	private ContentStoreManager contentStoreManager;
    
    
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if("json".equals(request.getParameter("f"))){
			String storeId = request.getParameter("storeId");	
	        ContentStore c = contentStoreManager.getContentStore(storeId);
	        ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("spaces",c.getSpaces());
			return mav;
		}else{
	        ModelAndView mav = new ModelAndView("spaces-manager");
	        mav.addObject("contentStores",contentStoreManager.getContentStores().values());
	        return mav;
		}
		
	}


}