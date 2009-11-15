
package org.duracloud.duradmin.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.domain.MetadataItem;
import org.duracloud.duradmin.util.MetadataUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RemoveMetadataController
        extends MetadataController {

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView handleMetadataItem(HttpServletRequest request,
                                              HttpServletResponse response,
                                              MetadataItem metadataItem,
                                              BindException errors)
            throws Exception {

        Map<String, String> metadata = getMetadata(metadataItem);
        if (MetadataUtils.remove(metadataItem.getName(), metadata) != null) {
            setMetadata(metadata, metadataItem);
            log.info(formatLogMessage("removed", metadataItem));
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("success");
        mav.setViewName("jsonView");
        return mav;
    }

}