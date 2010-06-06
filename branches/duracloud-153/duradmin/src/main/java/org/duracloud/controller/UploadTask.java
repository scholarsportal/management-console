package org.duracloud.controller;

import java.util.Date;
import java.util.Map;

public interface UploadTask extends Comparable{
	public String getId();
	
	
	public void cancel();

	
	public Map<String,String> getProperties();


	public Date getStartDate();
	
	
}
