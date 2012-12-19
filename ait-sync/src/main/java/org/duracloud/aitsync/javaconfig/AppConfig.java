package org.duracloud.aitsync.javaconfig;

import org.duracloud.aitsync.service.ConfigManager;
import org.duracloud.aitsync.service.ConfigManagerImpl;
import org.duracloud.aitsync.service.MappingManager;
import org.duracloud.aitsync.service.MappingManagerImpl;
import org.duracloud.aitsync.service.RestUtils;
import org.duracloud.aitsync.service.RestUtilsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan( basePackages = {"org.duracloud.aitsync.*"} )
public class AppConfig extends WebMvcConfigurationSupport {

    @Bean
    public RestUtils restUtils(){
        return new RestUtilsImpl();
    }

    @Bean 
    public ConfigManager configManager(){
        return new ConfigManagerImpl();
    }
    
    @Bean
    public MappingManager mappingManager(ConfigManager configManager){
        return new MappingManagerImpl(configManager);
    }
}
