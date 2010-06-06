package org.duracloud.duradmin.spaces.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.controller.UploadTask;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentItemUploadTask implements UploadTask, Comparable{
		Logger log = LoggerFactory.getLogger(ContentItemUploadTask.class);
		private ContentItem contentItem;
		private ContentStore contentStore;
		private FileInputStream inputStream;
		private long totalBytes = 0;
		private long bytesRead = 0;

		public static enum State {
			INITIALIZED,
			RUNNING,
			SUCCESS,
			FAILURE,
			CANCELLED
		}
		
		private State state = null;
		
		public ContentItemUploadTask(ContentItem contentItem, ContentStore contentStore) throws Exception{
			this.contentItem = contentItem;
			this.contentStore = contentStore;
			this.totalBytes = this.contentItem.getFileData().getData().length();
			this.state = State.INITIALIZED;
		}

		public void execute() throws ContentStoreException, IOException{
			this.startDate = new Date();
			
			try{
				this.inputStream = new FileInputStream(this.contentItem.getFileData().getData());
				ContentItem ci = this.contentItem;
				File file = ci.getFileData().getData();
				this.inputStream = new FileInputStream(file);
				state = State.RUNNING;
				contentStore.addContent(ci.getSpaceId(),
	                    ci.getContentId(),
	                    this.inputStream,
	                    file.length(),
	                    ci.getContentMimetype(),
	                    null,
	                    null);

				this.bytesRead = this.totalBytes;
				state = State.SUCCESS;
			}catch(ContentStoreException ex){
				ex.printStackTrace();
				state = State.FAILURE;
			}catch(IOException ex){
				ex.printStackTrace();
				state = State.FAILURE;
			}finally{
				cleanup();
			}
		}
		
		public String getId() {
			return this.contentItem.getStoreId()+"-"+this.contentItem.getSpaceId()+"-"+this.contentItem.getContentId();
		}
		
		private void cleanup(){
			try{
				this.contentItem.getFileData().dereferenceFileData();
				this.inputStream.close();
			}catch(Exception ex){
			}finally{
				this.inputStream = null;
			}
		}
		
		public void cancel(){
			if(state == State.RUNNING){
				state = State.CANCELLED;
				cleanup();
			}
		}
		
		public Map<String,String> getProperties(){
			Map<String,String> map = new HashMap<String,String>();
			if(state == State.RUNNING){
				updateBytesRead();
			}
			
			map.put("bytesRead", String.valueOf(this.bytesRead));
			map.put("totalBytes", String.valueOf(this.totalBytes));
			map.put("state", String.valueOf(this.state.toString().toLowerCase()));
			map.put("contentId", this.contentItem.getContentId());
			map.put("spaceId", this.contentItem.getSpaceId());
			map.put("storeId", this.contentItem.getStoreId());
			return map;
		}

		private void updateBytesRead() {
			try{
				this.bytesRead = this.inputStream.getChannel().position();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}

		private Date startDate = null;

		@Override
		public Date getStartDate() {
			return this.startDate;
		}
		


		@Override
		public int compareTo(Object o) {
			ContentItemUploadTask other = (ContentItemUploadTask)o;
			return this.getStartDate().compareTo(other.getStartDate());
		}
		
		public String toString(){
			return "{startDate: " + startDate + ", bytesRead: " + this.bytesRead + 
						", totalBytes: " + totalBytes +
								", storeId: " + contentItem.getStoreId() + 
								", spaceId: " + contentItem.getSpaceId() + 
								", contentId: " + contentItem.getContentId() + 
								
								"}";
		}

}
