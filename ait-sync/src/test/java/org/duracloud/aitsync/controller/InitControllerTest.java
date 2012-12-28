package org.duracloud.aitsync.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.duracloud.aitsync.domain.Configuration;
import org.duracloud.aitsync.service.ConfigManager;
import org.duracloud.aitsync.service.RestUtils;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class InitControllerTest {

    @Test
    public void testInit() throws IOException {
        InputStream is =
            InitControllerTest.class.getResourceAsStream("/test-config.xml");
        HttpServletRequest request =
            EasyMock.createMock(HttpServletRequest.class);
        RestUtils restUtils = EasyMock.createMock(RestUtils.class);
        EasyMock.expect(restUtils.getInputStream(EasyMock.anyObject(HttpServletRequest.class)))
                .andReturn(is);

        ConfigManager configManager = EasyMock.createMock(ConfigManager.class);
        configManager.initialize(EasyMock.isA(Configuration.class));
        EasyMock.expectLastCall();
        EasyMock.replay(request, restUtils, configManager);
        InitController ic = new InitController(restUtils, configManager);
        ic.init();
        EasyMock.verify(request, restUtils);
    }

}
