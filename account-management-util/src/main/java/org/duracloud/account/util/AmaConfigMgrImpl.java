/*
 * Copyright (c) 2009-2014 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class AmaConfigMgrImpl implements AmaConfigMgr {
	private AmaConfig amaConfig;

	@Override
	public void initialize(AmaConfig config) {
		this.amaConfig = config;
	}
	
	public AmaConfig getConfig(){
		if(this.amaConfig == null){
			throw new DuraCloudRuntimeException("the AmaConfigMgr has not been initialized.");
		}
		
		return this.amaConfig;
	}
}
