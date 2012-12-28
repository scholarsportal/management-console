package org.duracloud.aitsync.controller;

import java.util.ArrayList;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.domain.MappingForm;
import org.duracloud.aitsync.mapping.MappingManager;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/18/2012
 * 
 */

public class MappingControllerTest {
    private MappingController controller;
    private MappingManager mappingManager;

    @Before
    public void setUp() throws Exception {
        mappingManager = EasyMock.createMock(MappingManager.class);
        controller = new MappingController(mappingManager);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddMapping() throws Exception {
        BindingResult result = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(result.hasErrors()).andReturn(false);
        MappingForm mapping = new MappingForm();
        mappingManager.addMapping(EasyMock.isA(Mapping.class));
        EasyMock.expectLastCall();

        EasyMock.replay(mappingManager, result);
        ModelAndView m = controller.add(mapping, result);
        Assert.assertNotNull(m);
        EasyMock.verify(mappingManager, result);
    }

    @Test
    public void testRemoveMapping() {
        EasyMock.expect(mappingManager.removeMapping(EasyMock.anyLong()))
                .andReturn(new Mapping());
        EasyMock.expectLastCall();
        EasyMock.replay(mappingManager);
        ModelAndView m = controller.remove(1l);
        Assert.assertNotNull(m);
        EasyMock.verify(mappingManager);
    }

    @Test
    public void testGetMappings() {
        EasyMock.expect(mappingManager.getMappings())
                .andReturn(new ArrayList<Mapping>());
        EasyMock.expectLastCall();
        EasyMock.replay(mappingManager);
        ModelAndView m = controller.getMappings();
        Assert.assertNotNull(m);
        EasyMock.verify(mappingManager);
    }

}
