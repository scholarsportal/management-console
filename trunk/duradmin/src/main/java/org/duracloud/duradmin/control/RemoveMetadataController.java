
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradmin.contentstore.ContentItemList;
import org.duracloud.duradmin.contentstore.ContentItemListCache;
import org.duracloud.duradmin.domain.MetadataItem;
import org.duracloud.duradmin.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RemoveMetadataController
        extends MetadataController {

    protected final Logger log = LoggerFactory.getLogger(RemoveMetadataController.class);

    @Override
    protected ModelAndView handleMetadataItem(HttpServletRequest request,
                                              HttpServletResponse response,
                                              MetadataItem metadataItem,
                                              BindException errors)
            throws Exception {

        Map<String, String> metadata = getMetadata(metadataItem);
        if (MetadataUtils.remove(metadataItem.getName(), metadata) != null) {
            setMetadata(metadata, metadataItem);
            //mark content item list for update if a spaces tag
            if(!StringUtils.hasText(metadataItem.getContentId())){
                ContentItemList list = ContentItemListCache.get(request, metadataItem.getSpaceId(), getContentStoreProvider());
                list.markForUpdate();
            }
            log.info(formatLogMessage("removed", metadataItem));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("success");
        mav.setViewName("jsonView");
        return mav;
    }

}