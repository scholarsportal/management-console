package org.duracloud.duradmin.control;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * Some utility methods for working with spaces in the control layer. 
 * 
 *
 * @author Daniel Bernstein
 * @version $Id$
 */
public class SpacesHelper {
    protected static ModelAndView prepareContentsView(HttpServletRequest request,String spaceId, ContentStore store)
    throws Exception, ContentStoreException {
        ControllerUtils.checkSpaceId(spaceId);
        Space space = new Space();
        ContentItemList contentItemList = getContentItemList(request, spaceId, store); 
        String firstIndex = request.getParameter("fi");
        if(StringUtils.hasText(firstIndex)){
            contentItemList.setFirstResultIndex(Integer.parseInt(firstIndex));
        }
        SpaceUtil.populateSpace(space, store.getSpace(spaceId));
        ModelAndView mav = new ModelAndView();
        mav.addObject("space", space);
        mav.addObject("contentItemList", contentItemList);
        return mav;
    }

    private static ContentItemList getContentItemList(HttpServletRequest request, String spaceId, ContentStore store) {
        HttpSession session = request.getSession();
        ContentItemList list =  (ContentItemList)session.getAttribute("content-list-"+spaceId);
        if(list == null){
            list = new ContentItemList(spaceId, store);
            session.setAttribute("content-list-"+spaceId, list);
        }
        return list;
    }
}
