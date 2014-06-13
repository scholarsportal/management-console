/*
 * Copyright (c) 2009-2014 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.domain.Initable;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public interface AmaConfigMgr extends Initable {
	AmaConfig getConfig();
}
