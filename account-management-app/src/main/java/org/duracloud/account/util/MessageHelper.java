package org.duracloud.account.util;

import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {

    public Message createMessageSuccess(MessageSource messageSource,
                                        String code,
                                        Object[] args) {
        MessageResolver resolver =
            new MessageBuilder().code(code).args(args).info().build();

        return resolver.resolveMessage(messageSource,
                                       LocaleContextHolder.getLocale());
    }

}
