package org.duracloud.duradmin.control;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.domain.Space;
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
    protected static ModelAndView prepareContentsView(String spaceId, ContentStore store)
    throws Exception, ContentStoreException {
        if (!StringUtils.hasText(spaceId)) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }
        Space space = new Space();
        SpaceUtil.populateSpace(space, store.getSpace(spaceId));
        ModelAndView mav = new ModelAndView();
        mav.addObject("baseURL", store.getBaseURL());
        mav.addObject("storeID", store.getStoreId());
        mav.addObject("space", space);
        mav.addObject("title", space.getSpaceId());
        return mav;
    }
}
