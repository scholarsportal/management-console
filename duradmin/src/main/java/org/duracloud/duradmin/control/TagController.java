
package org.duracloud.duradmin.control;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.duracloud.duradmin.domain.Tag;
import org.duracloud.duradmin.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public abstract class TagController
        extends MetadataController{
    
    public TagController() {
        setCommandClass(Tag.class);
        setCommandName("tag");
    }
    

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Tag tag = (Tag)command;
        
        if(StringUtils.isEmptyOrAllWhiteSpace(tag.getSpaceId())){
            throw new IllegalArgumentException("spaceId is invalid.");
        }
        
        return handleTag(request, response, tag, errors);
    }
    
    
    abstract protected ModelAndView handleTag(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Tag tag,
                                              BindException errors) throws Exception;


    protected void log(Log log, String command, Tag tag) {
        String contentId = tag.getContentId();
        String contentString = (!StringUtils.isEmptyOrAllWhiteSpace(contentId)) ? ": " + contentId : "";
        log.info(MessageFormat.format("successfully {0} from space {1} {2}", command, tag.getSpaceId(), contentString));
    }


}