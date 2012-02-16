/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.AccountCluster;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountClusterConverter.CLUSTER_ACCOUNT_IDS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountClusterConverter.CLUSTER_NAME_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: 2/16/12
 */
public class DuracloudAccountClusterConverterTest
    extends DomainConverterTest<AccountCluster> {

    private static final int id = 0;
    private static final String clusterName = "cluster-name";
    private static Set<Integer> clusterAccountIds = null;
    private static final int counter = 4;

    @BeforeClass
    public static void initialize() throws Exception {
        clusterAccountIds = new HashSet<Integer>();
        clusterAccountIds.add(1);
        clusterAccountIds.add(2);
    }

    @Override
    protected DomainConverter<AccountCluster> createConverter() {
        return createAccountClusterConverter();
    }

    private DuracloudAccountClusterConverter createAccountClusterConverter() {
        return new DuracloudAccountClusterConverter();
    }

    @Override
    protected AccountCluster createTestItem() {
        return new AccountCluster(id,
                                 clusterName,
                                 clusterAccountIds,
                                 counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudAccountClusterConverter converter =
            createAccountClusterConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(CLUSTER_NAME_ATT, clusterName));
        testAtts.add(new Attribute(CLUSTER_ACCOUNT_IDS_ATT,
                                   converter.idsAsString(clusterAccountIds)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(AccountCluster cluster) {
        assertNotNull(cluster);

        assertNotNull(cluster.getCounter());
        assertNotNull(cluster.getClusterName());
        assertNotNull(cluster.getClusterAccountIds());

        assertEquals(counter, cluster.getCounter());
        assertEquals(clusterName,
                     cluster.getClusterName());
        assertEquals(clusterAccountIds,
                     cluster.getClusterAccountIds());
    }

}

