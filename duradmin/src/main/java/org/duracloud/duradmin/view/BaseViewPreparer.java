
package org.duracloud.duradmin.view;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;

public class BaseViewPreparer
        implements ViewPreparer {

    @Override
    public void execute(TilesRequestContext tilesRequestContext,
                        AttributeContext attributeContext) {
        attributeContext.putAttribute("mainMenu", new Attribute(MainMenu
                .instance()), true);

    }
}
