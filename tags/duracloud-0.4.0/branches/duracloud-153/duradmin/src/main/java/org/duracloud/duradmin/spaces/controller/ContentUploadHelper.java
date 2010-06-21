package org.duracloud.duradmin.spaces.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.duracloud.client.ContentStore;
import org.duracloud.controller.UploadManager;
import org.duracloud.duradmin.domain.ContentItem;

public class ContentUploadHelper {

	public static void executeUploadTask(HttpServletRequest request, ContentItem contentItem, ContentStore contentStore) throws Exception{
		ContentItemUploadTask task = new ContentItemUploadTask(contentItem, contentStore);
		getManager(request).addUploadTask(task);
		task.execute();
	}
	
	public static UploadManager getManager(HttpServletRequest request){
		String key = UploadManager.class.getName();
		ServletContext c = request.getSession().getServletContext();
		
		UploadManager u = (UploadManager)c.getAttribute(key);
		
		if(u == null){
			u = new UploadManager();
			c.setAttribute(key, u);
		}
		return u;
	}

	
}
