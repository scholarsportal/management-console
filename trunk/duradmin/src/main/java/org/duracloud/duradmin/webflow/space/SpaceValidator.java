package org.duracloud.duradmin.webflow.space;

import org.duracloud.duradmin.domain.Space;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.StringUtils;

public class SpaceValidator {
    public void validateDefineSpace(Space space, ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        String spaceId = space.getSpaceId();
        if (spaceId == null || !StringUtils.hasLength(spaceId.trim())) {
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                defaultText("Space is required.").build());
        } 
        
        //TODO check that space does not already exist in repository;
    }
}
