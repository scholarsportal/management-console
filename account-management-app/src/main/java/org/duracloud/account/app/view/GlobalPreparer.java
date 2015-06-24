/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.view;

import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.springframework.stereotype.Component;

/**
 * This preparer provides useful variables to jspx
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
@Component("globalPreparer")
public class GlobalPreparer implements ViewPreparer{

    @Override
    public void execute(TilesRequestContext tilesContext,
                        AttributeContext attributeContext) {
        
    }

}
