dojo.require("duracloud._base");
dojo.require("duracloud.storage");
dojo.provide("duracloud.durastore");
duracloud.durastore = {
		
	loadSpaceMetadata: function(node, spaceId){
		//check if already updated.
		//var node = dojo.byId(nodeId);
		var contents = node.innerHTML;
		//console.debug(contents);
		if(contents.search('<!--empty-->') < 0){
			return;
		}

		var s = duracloud.storage.get(spaceId);
		if(s != null){
		      node.innerHTML = duracloud.formatSpaceMetadataHtml(s);
		      return;
		}
		
		duracloud.showWaitMessage(node, "Retrieving metadata...");
		
		dojo.xhrGet( {
		    // The following URL must match that used to test the server.
			url: "/duradmin/data/spaces/space?spaceId="+spaceId,
		    handleAs: "json",
		    headers: getAuthHeaders(),
		    load: function(responseObject, ioArgs) {
			  console.debug(responseObject);  // Dump it to the console
			  var space = responseObject.space;
			  node.innerHTML = duracloud.formatSpaceMetadataHtml(space);
			  duracloud.storage.put(spaceId, space);
			},
			error: function(responseObject, ioArgs){
	          	  console.error("HTTP status code: ", ioArgs.xhr.status); 

		          duracloud.showError(node, ioArgs);
		          return responseObject;
			}

		});
	},
	
	loadContentItem: function (node, spaceId, contentId){
		//var node = dojo.byId(nodeId);
		var contents = node.innerHTML;
		if(contents.search('<!--empty-->') < 0){
			return;
		}
		
		var s = duracloud.storage.get(spaceId,contentId);
		if(s != null){
			  node.innerHTML = "";
		      node.appendChild(duracloud.formatContentItemMetadataHtml(s));
		      return;
		}

		duracloud.showWaitMessage(node, "Retrieving metadata...");
		dojo.xhrGet( {
		    // The following URL must match that used to test the server.
		    url: "/duradmin/data/spaces/contentItem?spaceId="+spaceId+"&contentId=" + contentId,
		    handleAs: "json",
		    load: function(responseObject, ioArgs) {
			  console.debug(responseObject);  // Dump it to the console
			  var ci = responseObject.contentItem;
			  var details = duracloud.formatContentItemMetadataHtml(ci);
			  //var node = dojo.byId(nodeId);
			  node.innerHTML = "";
		      node.appendChild(details);
		      duracloud.storage.put(spaceId,contentId,ci);

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
