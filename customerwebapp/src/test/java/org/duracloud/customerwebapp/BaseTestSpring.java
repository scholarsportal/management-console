package org.duracloud.customerwebapp;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;


public class BaseTestSpring extends AbstractDependencyInjectionSpringContextTests {

    /*
     * This class is not yet used.
     * ...will be for integration testing.
     *
     */

    @Override
    protected String[] getConfigLocations()
    {
        setAutowireMode(AbstractDependencyInjectionSpringContextTests.AUTOWIRE_BY_NAME);
        return new String[]{"file:src/main/webapp/WEB-INF/config/duracloud-app-config.xml"};
    }

}
