/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.flow.createaccount;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.util.AccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein 
 *         Date: April 17, 2012
 * 
 */
@Component
public class CreateAccountFlowHelper {
    @Autowired
    private AccountManagerService accountManagerService;
    
    public List<AccountClusterData> getAccountClusters(){
        Set<AccountCluster> accountClusters = 
                                (Set<AccountCluster>) 
                                    accountManagerService.listAccountClusters(null);
     
        List<AccountClusterData> list = new LinkedList<AccountClusterData>();
        for(AccountCluster cluster : accountClusters){
            list.add(new AccountClusterData(cluster.getId(),cluster.getClusterName()));
        }
        return list;
    }
    
    /**
     * This simplified cluster data class is a workaround for a problem related to the serialization
     * of AccountCluster objects.  Any objects stored in the flowScope must be serializable. The
     * default serialization behavior doesn't serialize the id field correctly in AccountCluster. However
     * implementing read/writeObject methods in order to correct the problem breaks XStreams' serialization.
     * Thus enters AccountClusterData.
     * 
     */
    public static class AccountClusterData implements Serializable{
        private int id;
        private String clusterName;
        
        public AccountClusterData(int id, String clusterName){
            this.id = id;
            this.clusterName = clusterName;
        }

        public int getId() {
            return id;
        }

        public String getClusterName() {
            return clusterName;
        }
    }

}
