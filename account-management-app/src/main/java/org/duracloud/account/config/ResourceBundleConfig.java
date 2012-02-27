package org.duracloud.account.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class ResourceBundleConfig {
    
    @Bean 
    public MessageSource messageSource(){
        ResourceBundleMessageSource m = new ResourceBundleMessageSource();
        m.setBasenames("global",
                       "root");
        return m;
    }
}
