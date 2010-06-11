
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.contentstore.ContentItemListCache;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentsController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(ContentsController.class);

    private String successView;

    public ContentsController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ControllerUtils.checkSpaceId(spaceId);
        ContentItemListCache.refresh(request, spaceId, getContentStoreProvider());
        ContentItemList contentItemList = ContentItemListCache.get(request, spaceId, getContentStoreProvider());
        
        
        String maxPerPage = request.getParameter("mpp");
        if (StringUtils.hasText(maxPerPage)) {
            contentItemList.setMaxResultsPerPage(Integer.parseInt(maxPerPage));
        }
        
        String viewFilter = request.getParameter("viewFilter");
        if(viewFilter !=null){
            contentItemList.setViewFilter(viewFilter);
        }
        
        
        String action = space.getAction();
        if (StringUtils.hasText(action)) {
            if (action.equals("n")) {
                contentItemList.next();
            } else if (action.equals("p")) {
                contentItemList.previous();
            } else {
                contentItemList.first();
            }
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("contentItemList", contentItemList);
        mav.addObject("space", contentItemList.getSpace());
        mav.setViewName(getSuccessView());
        return mav;
    }

    public String getSuccessView() {
        return successView;
    }


    public void setSuccessView(String successView) {
        this.successView = successView;
    }

}