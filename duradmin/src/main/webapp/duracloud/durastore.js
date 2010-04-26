dojo.require("duracloud._base");
dojo.require("duracloud.storage");
dojo.provide("duracloud.durastore");
dojo.require("dojox.dtl.filter.strings");

duracloud.durastore = {
		
	loadSpaceMetadata: function(node, spaceId){
		//check if already updated.
		//var node = dojo.byId(nodeId);
		var contents = node.innerHTML;
		//console.debug(contents);
		if(contents.search('<!--empty-->') < 0){
			return;
		}

		var s = duracloud.storage.getSpace(spaceId);
		if(s != null){
		      dojo.attr(node, "innerHTML", duracloud.formatSpaceMetadataHtml(s));
		      return;
		}

		duracloud.showWaitMessage(node, "Retrieving metadata...");
		
		dojo.xhrGet( {
		    // The following URL must match that used to test the server.
			url: "/duradmin/data/spaces/space?spaceId="+spaceId,
		    handleAs: "json",
		    load: function(responseObject, ioArgs) {
			  console.debug(responseObject);  // Dump it to the console
			  var space = responseObject.space;
			  node.innerHTML = duracloud.formatSpaceMetadataHtml(space);
			  duracloud.storage.putSpace(spaceId, space);
			},
			error: function(responseObject, ioArgs){
	          	  console.error("HTTP status code: ", ioArgs.xhr.status); 

		          duracloud.showError(node, ioArgs);
		          return responseObject;
			}

		});
	},
	
	_formatContentItemMetadataHtml: function(node, contentItem){
		  var ci = contentItem;
		  var metadata = ci.metadata;
	      var tn = ci.tinyThumbnailURL;
    	  var thumbLink = dojo.create("a", {"target":"viewer", "href": ci.viewerURL});
    	  dojo.create("img", {"src":tn}, thumbLink);
    	  
    	  dojo.query(".tiny-thumb", node).forEach(function(div){
    		 div.innerHTML = "";
    		 div.appendChild(thumbLink);
    	  });
    	  
    	  dojo.query(".content-item-metadata", node).forEach(function(div){
    		  div.innerHTML = "";

        	  var table = dojo.create("table", {"class":"content-item-metadata-summary"});
    	      var row = addRow(table);
              addCell("modified", row);
    	      addCell(metadata.modified, row);
    	      
    	      row = addRow(table);
    	      addCell("mimetype", row);
    		  addCell(metadata.mimetype, row);

    		  row = addRow(table);
    		  addCell("size", row);
    		  addCell(metadata.size + " bytes", row);
    	      
    		  row = addRow(table);
    		  addCell("checksum", row);
    		  addCell(metadata.checksum, row);

    		  div.appendChild(table);
    	  });
    	  
	},
	
	loadContentItem: function (node, spaceId, contentId){
		var that = this;
		
		var ci = duracloud.storage.getContentItem(spaceId,contentId);

		if(ci != null){
		     that._formatContentItemMetadataHtml(node,ci);
		      return;
		}

		var escapedContentId = dojox.dtl.filter.strings.urlencode(contentId);
		dojo.xhrGet( {
		    // The following URL must match that used to test the server.
		    url: "/duradmin/data/spaces/contentItem?spaceId="+spaceId+"&contentId=" + escapedContentId,
		    handleAs: "json",
		    load: function(responseObject, ioArgs) {
			  console.debug(responseObject);  // Dump it to the console
			  var contentItem = responseObject.contentItem;
			  that._formatContentItemMetadataHtml(node,contentItem);
			  duracloud.storage.putContentItem(spaceId,contentId,contentItem);
			},
			error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          duracloud.showError(node, ioArgs);
			}
		});
	},
	
	removeTag: function (spaceId,tag, contentId, element){
		var location = "/duradmin/spaces/tag/remove?spaceId="+spaceId+"&tag=" + tag + "&contentId="+contentId;
		this._doRemoveCall(element, location);
	},

	_doRemoveCall: function (element, location) {
		var animation = showInProcessOfDeletionFeedback(element);
		dojo.xhrGet( {
		    url: location,
		    handleAs: "json",
		    load: function(responseObject, ioArgs) {
			  console.debug(responseObject);  // Dump it to the console
	     	  setFlashMessage("successfully removed");
			  onDeletionSucceeded(element);

		},
			error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          animation.stop();
	          cancelShowInProcessOfDeletionFeedback(element);
			}
		});
	},
	
	removeMetadataByKey: function (spaceId,key, contentId, element){
		var location = "/duradmin/spaces/metadata/remove?spaceId="+spaceId+"&name=" + key + "&contentId="+contentId;
		this._doRemoveCall(element, location);
	}


}
