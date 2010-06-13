/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.webflow.space;

import junit.framework.Assert;

import org.duracloud.duradmin.contentstore.ContentStoreProviderTestBase;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.mock.MockValidationContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.validation.ValidationContext;

public class SpaceValidatorTest
        extends ContentStoreProviderTestBase {

    SpaceValidator v = new SpaceValidator();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        v.setContentStoreProvider(contentStoreProvider);
    }

    @Test
    public void checkInvalidSpaceIds() throws Exception {
        ValidationContext vc = null;
        Space space = new Space();

        //too short;
        space.setSpaceId("a");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //too long
        String tooLong = "";
        for (int i = 0; i < 66; i++) {
            tooLong += "t";
        }

        space.setSpaceId(tooLong);
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //ends with -
        space.setSpaceId("endswith-");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //doesn't start with letter or number

        space.setSpaceId("-startswithdash");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //has a period followed by a dash
        space.setSpaceId("hasaperiodfollowedbyadash.-here");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //contains uppercase
        space.setSpaceId("AAAAAAAA");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);

        //okay
        space.setSpaceId("a.0879dsfdsf-dfks.dsfdsf-da.");
        v.validateDefineSpace(space, vc = new MockValidationContext());
        Assert.assertEquals(0, vc.getMessageContext().getAllMessages().length);

    }

}
