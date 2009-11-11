package org.duracloud.duradmin.control;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ScrollableContentItemList;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
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
        
        ScrollableContentItemList contentItemList = getContentItemList(request, spaceId); 
        
        SpaceUtil.populateSpace(space, store.getSpace(spaceId, contentItemList.getContentIdFilterString(), contentItemList.getFirstResultIndex(), contentItemList.getMaxResultsPerPage()));
        ModelAndView mav = new ModelAndView();
        mav.addObject("space", space);
        return mav;
    }

    private static ScrollableContentItemList getContentItemList(HttpServletRequest request,String spaceId) {
        HttpSession session = request.getSession();
        ScrollableContentItemList list =  (ScrollableContentItemList)session.getAttribute("content-list-"+spaceId);
        if(list == null){
            list = new ScrollableContentItemList();
            session.setAttribute("content-list-"+spaceId, list);
        }
        return list;
    }
}
