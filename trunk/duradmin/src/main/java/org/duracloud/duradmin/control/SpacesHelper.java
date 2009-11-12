package org.duracloud.duradmin.control;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.duradmin.web.util.Page;
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
    protected static ModelAndView prepareContentsView(HttpServletRequest request,String spaceId, ContentStoreProvider contentStoreProvider)
    throws Exception, ContentStoreException {
        ControllerUtils.checkSpaceId(spaceId);
        Space space = new Space();
        ContentItemList contentItemList = getContentItemList(request, spaceId, contentStoreProvider); 
        
        String firstIndex = request.getParameter("fi");
        if(StringUtils.hasText(firstIndex)){
            contentItemList.setFirstResultIndex(Integer.parseInt(firstIndex));
        }

        String maxPerPage = request.getParameter("mpp");
        if(StringUtils.hasText(maxPerPage)){
            contentItemList.setMaxResultsPerPage(Integer.parseInt(maxPerPage));
        }

        
        SpaceUtil.populateSpace(space, contentStoreProvider.getContentStore().getSpace(spaceId));
        ModelAndView mav = new ModelAndView();
        mav.addObject("space", space);
        mav.addObject("contentItemList", contentItemList);
        mav.addObject("pages", calculatePages(contentItemList));
        return mav;
    }
    
    private static List<Page> calculatePages(ContentItemList contentItemList) {
        long firstIndex = contentItemList.getFirstResultIndex();
        long pageSize = contentItemList.getMaxResultsPerPage();
        long currentPage = (firstIndex / pageSize)+1;
        int showPages = 20;
        List<Page> pages = new LinkedList<Page>();
        
        //calculate range of indices
        //based on formula: always show 10 pages.

        //if within 5 pages of beginning of results, 
        //start at zero
        long beginIndex = firstIndex -(showPages/2*pageSize);
        if(beginIndex < 0){
            beginIndex = 0;
        }
        
        long lastIndex = Math.min((showPages*pageSize)+beginIndex-1,contentItemList.getResultCount());
        
        while(true){
            long page = beginIndex / pageSize+1;
            pages.add(new Page(page, beginIndex, page == currentPage));
            beginIndex+=pageSize;
            if(beginIndex >= lastIndex){
                break;
            }
        }
        
        return pages;
    }

    private static ContentItemList getContentItemList(HttpServletRequest request, String spaceId, ContentStoreProvider contentStoreProvider) {
        HttpSession session = request.getSession();
        ContentItemList list =  (ContentItemList)session.getAttribute("content-list-"+spaceId);
        if(list == null){
            list = new ContentItemList(spaceId, contentStoreProvider);
            session.setAttribute("content-list-"+spaceId, list);
        }
        return list;
    }
}
