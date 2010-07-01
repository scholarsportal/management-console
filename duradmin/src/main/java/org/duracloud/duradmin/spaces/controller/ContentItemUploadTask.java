/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */

package org.duracloud.duradmin.spaces.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.controller.UploadTask;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.error.ContentStoreException;
import org.duracloud.io.ByteCountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Bernstein
 */
public class ContentItemUploadTask implements UploadTask, Comparable{
		Logger log = LoggerFactory.getLogger(ContentItemUploadTask.class);
		private ContentItem contentItem;
		private ContentStore contentStore;
		private long totalBytes = 0;
		private long bytesRead = 0;
		private ByteCountingInputStream inputStream = null;
		public static enum State {
			INITIALIZED,
			RUNNING,
			SUCCESS,
			FAILURE,
			CANCELLED
		}
		
		private State state = null;
		
		public ContentItemUploadTask(ContentItem contentItem, ContentStore contentStore, String username) throws Exception{
			this.contentItem = contentItem;
			this.contentStore = contentStore;
			this.totalBytes = this.contentItem.getFile().getSize();
			this.inputStream = new ByteCountingInputStream(this.contentItem.getFile().getInputStream());
			this.state = State.INITIALIZED;
			this.username = username;
		}

		public void execute() throws ContentStoreException, IOException{
			this.startDate = new Date();

			ContentItem ci = this.contentItem;
			
			try{
				state = State.RUNNING;
				contentStore.addContent(ci.getSpaceId(),
	                    ci.getContentId(),
	                    this.inputStream,
	                    this.totalBytes,
	                    ci.getContentMimetype(),
	                    null,
	                    null);

				this.bytesRead = this.totalBytes;
				state = State.SUCCESS;
			}catch(ContentStoreException ex){
				log.error("failed to upload content item [ " + ci.getContentId() + "]", ex);;
				state = State.FAILURE;
			}
		}
		
		public String getId() {
			return this.contentItem.getStoreId()+"-"+
					this.contentItem.getSpaceId()+"-"+
						this.contentItem.getContentId();
		}
		
		public void cancel(){
			if(state == State.RUNNING){
				state = State.CANCELLED;
				try {
					this.inputStream.close();
				} catch (IOException e) {
					log.error("failed to close input stream of content item in upload process.", e);
				}
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
			this.bytesRead = this.inputStream.getBytesRead();
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

        private String username;

		@Override
        public String getUsername() {
            return this.username;
        }

}
