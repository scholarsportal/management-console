package org.duracloud.account.config;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
/**
 * This class defines and extended version of the MVC Annotation configuration.
 * @author Daniel Bernstein
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
        ConfigurableWebBindingInitializer initializer = 
            (ConfigurableWebBindingInitializer) adapter.getWebBindingInitializer();
        
        PropertyEditorRegistrar propertyEditorRegistrar = new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                //Trim strings before setting values on all form beans.
                registry.registerCustomEditor(Object.class, new StringTrimmerEditor(true));
            }
        };

        initializer.setPropertyEditorRegistrar(propertyEditorRegistrar);
        return adapter;
    }
}