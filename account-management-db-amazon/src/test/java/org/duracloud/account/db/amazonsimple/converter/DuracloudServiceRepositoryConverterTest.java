/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.ServiceRepository;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.HOST_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.PASSWORD_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.SERVICE_REPOSITORY_TYPE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.SPACE_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.VERSION_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter.USERNAME_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class DuracloudServiceRepositoryConverterTest extends DomainConverterTest<ServiceRepository> {

    private static final int id = 0;
    private static final ServiceRepository.ServiceRepositoryType serviceRepositoryType =
        ServiceRepository.ServiceRepositoryType.VERIFIED;
    private static final String hostName = "hostName";
    private static final String spaceId = "spaceId";
    private static final String version = "version";
    private static final String username = "username";
    private static final String password = "password";
    private static final int counter = 4;

    @Override
    protected DomainConverter<ServiceRepository> createConverter() {
        return new DuracloudServiceRepositoryConverter();
    }

    @Override
    protected ServiceRepository createTestItem() {
        return new ServiceRepository(id,
                                     serviceRepositoryType,
                                     hostName,
                                     spaceId,
                                     version,
                                     username,
                                     password,
                                     counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudServiceRepositoryConverter repoCvtr =
            new DuracloudServiceRepositoryConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(SERVICE_REPOSITORY_TYPE_ATT,
                                   repoCvtr.asString(serviceRepositoryType)));
        testAtts.add(new Attribute(HOST_NAME_ATT, hostName));
        testAtts.add(new Attribute(SPACE_ID_ATT, spaceId));
        testAtts.add(new Attribute(VERSION_ATT, version));
        testAtts.add(new Attribute(USERNAME_ATT, username));
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(ServiceRepository serviceRepo) {
        Assert.assertNotNull(serviceRepo);

        Assert.assertNotNull(serviceRepo.getServiceRepositoryType());
        Assert.assertNotNull(serviceRepo.getSpaceId());
        Assert.assertNotNull(serviceRepo.getVersion());
        Assert.assertNotNull(serviceRepo.getHostName());
        Assert.assertNotNull(serviceRepo.getUsername());
        Assert.assertNotNull(serviceRepo.getPassword());

        Assert.assertEquals(counter, serviceRepo.getCounter());
        Assert.assertEquals(serviceRepositoryType,
                            serviceRepo.getServiceRepositoryType());
        Assert.assertEquals(hostName, serviceRepo.getHostName());
        Assert.assertEquals(spaceId, serviceRepo.getSpaceId());
        Assert.assertEquals(version, serviceRepo.getVersion());
        Assert.assertEquals(username, serviceRepo.getUsername());
        Assert.assertEquals(password, serviceRepo.getPassword());
    }

}
