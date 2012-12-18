package org.duracloud.aitsync;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.springframework.web.servlet.view.tiles2.SpringBeanPreparerFactory;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;
import org.springframework.web.servlet.view.xml.MarshallingView;

@Configuration
public class ViewConfig {
    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        ContentNegotiatingViewResolver r = new ContentNegotiatingViewResolver();
        r.setDefaultContentType(MediaType.APPLICATION_JSON);
        r.setOrder(1);
        r.setIgnoreAcceptHeader(false);

        Map<String, String> mts = new HashMap<String, String>();
        mts.put("json", MediaType.APPLICATION_JSON_VALUE);
        mts.put("xml", MediaType.APPLICATION_XML_VALUE);
        r.setMediaTypes(mts);
        List<View> dvs = new LinkedList<View>();

        MarshallingView view = new MarshallingView();
        XStreamMarshaller xsm = new XStreamMarshaller();
        xsm.setAnnotatedClasses(new Class[] { StatusSummary.class });
        view.setMarshaller(xsm);
        dvs.add(view);
        dvs.add(new MappingJacksonJsonView());

        r.setDefaultViews(dvs);

        return r;
    }

    @Bean
    public ViewResolver viewResolver() {
        UrlBasedViewResolver r = new UrlBasedViewResolver();
        r.setViewClass(TilesView.class);
        r.setOrder(2);
        return r;
    }

    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tc = new TilesConfigurer();
        tc.setDefinitions(new String[] { "/WEB-INF/defs/general.xml" });

        tc.setPreparerFactoryClass(SpringBeanPreparerFactory.class);
        return tc;
    }

    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver r = new InternalResourceViewResolver();
        r.setViewClass(JstlView.class);
        r.setPrefix("/WEB-INF/jsp/");
        r.setSuffix(".jsp");
        return r;
    }
}
