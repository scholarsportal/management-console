package org.duracloud.account.util;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class DuracloudInstanceNotAvailableException extends DuraCloudCheckedException {

	private static final long serialVersionUID = 1L;

	public DuracloudInstanceNotAvailableException(String message, Throwable cause){
		super(message,cause);
	}
}
