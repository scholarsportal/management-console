
package org.duracloud.duradmin.integration.selenium;



public class AddSpacesTest
        extends SpaceTestBase{

    
    public void setUp() throws Exception {
        super.setUp();
        navigateToSpacesPage();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddAndRemoveSpace() throws Exception {
        String spaceId = addSpace();
        removeSpace(spaceId);

    }
}
