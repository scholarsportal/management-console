package org.duracloud.duradmin.webflow.content;

import org.duracloud.duradmin.domain.ContentItem;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.multipart.MultipartFile;

public class ContentItemValidator {
    public void validateDefineContentItem(ContentItem contentItem, ValidationContext context) {
        MultipartFile file = contentItem.getFile();
        if (file == null || file.isEmpty()) {
            MessageContext messages = context.getMessageContext();
            messages.addMessage(new MessageBuilder()
                                    .error()
                                    .source("file")
                                    .code("required").build());
        }
    }
}
