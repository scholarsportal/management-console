
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.ContentMetadata;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

public class ContentItemDataController
        extends BaseCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    public ContentItemDataController() {
        setCommandClass(ContentItem.class);
        setCommandName("contentItem");
    }

    @Override
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {
        // TODO Auto-generated method stub
        super.initBinder(request, binder);
    }
    

    
    
    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();
        ControllerUtils.checkContentRequestParams(spaceId, contentId);
        ContentStore store = getContentStore();
        Map<String,String> contentMetadata = store.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = SpaceUtil.populateContentMetadata(contentMetadata);
        contentItem.setMetadata(metadata);
        
        ModelAndView mav = new ModelAndView();
        mav.setViewName("jsonView");
        mav.getModel().clear();
        mav.addObject(contentItem);
        return mav;
    }

}