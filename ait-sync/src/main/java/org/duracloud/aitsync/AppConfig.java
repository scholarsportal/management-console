package org.duracloud.aitsync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan( basePackages = {"org.duracloud.archiveit.ingest"} )
public class AppConfig extends WebMvcConfigurationSupport {

    @Bean
    public RestUtils restUtils(){
        return new RestUtilsImpl();
    }
}
