/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.config;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * This class defines and extended version of the MVC Annotation configuration.
 *
 * @author Daniel Bernstein
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

    @Bean
    public DefaultFormattingConversionService defaultFormattingConversionService() {
        return new ApplicationFormattingConversionService();
    }

    @Bean
    ConversionService defaultConversionService() {
        return new DefaultConversionService(defaultFormattingConversionService());
    }
}
