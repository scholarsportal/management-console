
package org.duracloud.duradmin.view;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreSelector;

/**
 * A view preparer to be invoked by every page in the application.  
 *
 * @author Daniel Bernstein
 * @version $Id$
 */
public class BaseViewPreparer
        implements ViewPreparer {

    private Log log = LogFactory.getLog(getClass());
    
    private ContentStoreSelector contentStoreSelector;
    
    public ContentStoreSelector getContentStoreSelector() {
        return contentStoreSelector;
    }
    
    public void setContentStoreSelector(ContentStoreSelector contentStoreSelector) {
        this.contentStoreSelector = contentStoreSelector;
    }

    public void execute(TilesRequestContext tilesRequestContext,
                        AttributeContext attributeContext) {

        try{

            attributeContext.putAttribute("mainMenu", new Attribute(MainMenu
                    .instance()), true);
            log.debug("main menu attribute set");
            
            String currentUrl = (String)tilesRequestContext.getRequestScope().get("currentUrl");
            attributeContext.putAttribute("currentUrl", new Attribute(currentUrl), true);
    
            log.debug("currentUrl attribute set:" + currentUrl);
        
            List<ContentStore> contentStores = contentStoreSelector.getContentStores();
            
            attributeContext.putAttribute(
                              "contentStores",
                              new Attribute(contentStores), true);

            log.debug("contentStores attribute set: " + contentStores);

            String selectedStorageProviderId = contentStoreSelector.getSelectedId();
            
            attributeContext.putAttribute(
                                          "selectedStoreId",
                                          new Attribute(selectedStorageProviderId), true);

        }catch(ContentStoreException ex){
            log.error("failed to complete execution of BaseViewPreparer: " 
                      + ex.getMessage());
            
            
        }


    }
}
