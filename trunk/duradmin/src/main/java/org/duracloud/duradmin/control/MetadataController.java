
package org.duracloud.duradmin.control;

import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.domain.MetadataItem;
import org.duracloud.duradmin.domain.Tag;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.MetadataUtils;
import org.duracloud.duradmin.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public abstract class MetadataController
        extends BaseCommandController {
    

    protected final Logger log = Logger.getLogger(getClass());

    public MetadataController() {
        setCommandClass(MetadataItem.class);
        setCommandName("metadata");
    }
    @Override
    protected final ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        MetadataItem metadataItem = (MetadataItem)command;
        ControllerUtils.checkSpaceId(metadataItem.getSpaceId());
        return handleMetadataItem(request, response, metadataItem, errors);
    }

    protected abstract ModelAndView handleMetadataItem(HttpServletRequest request,
                                        HttpServletResponse response,
                                        MetadataItem metadata,
                                        BindException errors) throws Exception;
    
    protected Map<String,String> getMetadata(MetadataItem metadataItem) throws ContentStoreException{
        return MetadataUtils.getMetadata(getContentStore(),metadataItem.getSpaceId(), metadataItem.getContentId());
    }
    

    protected void setMetadata(Map<String,String> metadata, MetadataItem metadataItem) throws ContentStoreException{
        MetadataUtils.setMetadata(getContentStore(), metadataItem.getSpaceId(), metadataItem.getContentId(), metadata);
    }

    protected String formatLogMessage(String command, MetadataItem metadataItem) {
        String contentId = metadataItem.getContentId();
        String contentString = (!StringUtils.isEmptyOrAllWhiteSpace(contentId)) ? ": " + contentId : "";
        return MessageFormat.format("successfully {0} from space {1} {2}", command, metadataItem.getSpaceId(), contentString);
    }
}