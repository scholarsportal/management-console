dojo.require("dijit.TooltipDialog");
dojo.require("dijit.Dialog");

function loadContentItem(nodeId, spaceId, contentId){
	var node = dojo.byId(nodeId);
	var contents = node.innerHTML;
	if(contents.search('<!--empty-->') < 0){
		return;
	}
	showWaitMessage(node, "Retrieving metadata...");
	dojo.xhrGet( {
	    // The following URL must match that used to test the server.
	    url: "/duradmin/data/spaces/contentItem?spaceId="+spaceId+"&contentId=" + contentId,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
		  var ci = responseObject.contentItem;
		  var details = formatContentItemMetadataHtml(ci);
	      dojo.byId(nodeId).innerHTML = details;
	},
		error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          showError(node, ioArgs);
	          return responseObject;
		}
	});
}


function undeployService( serviceInfoId, deploymentId ){
	if(!confirmDeleteOperation(null)){
		return;
	}
	
	var row = dojo.byId("deployment-"+serviceInfoId + "-" + deploymentId);
	dojo.addClass(row, "deleting")
	dojo.xhrGet( {
	    url: "/duradmin/services/undeploy?serviceInfoId="+serviceInfoId+"&deploymentId=" + deploymentId,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
		  row.parentNode.removeChild(row);
		},
		error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          return responseObject;
		}
	});
}


function removeTag(spaceId,tag, contentId, element){
	dojo.addClass(element, "deleting")
	
	dojo.xhrGet( {
	    url: "/duradmin/spaces/tag/remove?spaceId="+spaceId+"&tag=" + tag + "&contentId="+contentId,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
		  element.parentNode.removeChild(element);
		  
		},
		error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          return responseObject;
		}

	});
}

function removeMetadataByKey(spaceId,key, contentId, element){
	dojo.addClass(element, "deleting")
	
	dojo.xhrGet( {
	    url: "/duradmin/spaces/metadata/remove?spaceId="+spaceId+"&name=" + key + "&contentId="+contentId,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
		  element.parentNode.removeChild(element);
		  
		},
		error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          return responseObject;
		}
	});
}

function showWaitMessage(node, messageText){
	node.innerHTML = "";
	var div = dojo.create("div",null, node);
	var img =  dojo.create("img", {src:"/duradmin/images/wait.gif" },div);
	var span = dojo.create("span", {innerHTML: messageText},div);

}

function showError(node, ioArgs){
	node.innerHTML = "";
	var div = dojo.create("div",null, node);
	var img =  dojo.create("img", {src:"/duradmin/images/error.gif" },div);
	var messageText = "Unable to complete request: status (" + ioArgs.xhr.status + ")";
	var span = dojo.create("span", {innerHTML: messageText},div);
}


function formatContentItemMetadataHtml(ci){
      var contentId = ci.contentId;
	  var spaceId = ci.spaceId;
	  console.debug("contentId=" + contentId);
	  var metadata = ci.metadata;
	  var mimetype = metadata.mimetype;
	  var size = metadata.size;
	  var modified  = metadata.modified;
	  var checksum  = metadata.checksum;
      return "Modified on " +  modified + "<br/>" + mimetype + "<br/>" + size + " bytes<br/>checksum: " + checksum;
}


function loadSpaceMetadata(nodeId, spaceId){
	//check if already updated.
	var node = dojo.byId(nodeId);
	var contents = node.innerHTML;
	//console.debug(contents);
	if(contents.search('<!--empty-->') < 0){
		return;
	}
	
	showWaitMessage(node, "Retrieving metadata...");
	
	dojo.xhrGet( {
	    // The following URL must match that used to test the server.
	    url: "/duradmin/data/spaces/space?spaceId="+spaceId,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
		  var space = responseObject.space;
		  var metadataHtml = formatSpaceMetadataHtml(space);
	      dojo.byId(nodeId).innerHTML = metadataHtml;
	},
		error: function(responseObject, ioArgs){
	          console.error("HTTP status code: ", ioArgs.xhr.status); 
	          showError(node, ioArgs);
	          return responseObject;
		}

	});
}

function formatSpaceMetadataHtml(space){
	  var metadata = space.metadata;
	  var count = metadata.count;
	  var created  = metadata.created;
	  var access  = metadata.access;

	  return "Access: " + access + "<br/>" + "Created on " + created + "<br/>" + count +  " items";
}




/*zebra stripe standard tables*/	
dojo.addOnLoad(function(){
	dojo.query(".standard > tbody > tr:nth-child(even)").addClass("evenRow");
	dojo.query(".extended-metadata tr:nth-child(even)").addClass("evenRow");

});



dojo.addOnLoad(function(){
	/*adds mouse listeners on spaces table rows*/
	dojo.query("#spacesTable > tbody > tr, #deploymentsTable > tbody > tr",document).forEach(
	    function(row) {
	    	dojo.connect(row, 'onmouseover', function() {
				dojo.addClass(row,"hover");
				dojo.query("div[id=actionDiv]",row).attr('style', {visibility:'visible'});
	    	});

           	dojo.connect(row, 'onmouseout', 
				function(){
					dojo.removeClass(row,"hover");
					dojo.query("div[id=actionDiv]",row).attr('style', {visibility:'hidden'});
      		});
	    }
	);		

	

});


function confirmDeleteOperation(e){
	var result = confirm('You are about to perform an irreversible operation\.\nClick \'OK\' if you are sure you wish to continue\.');
	if(!result) { 
		return false; 
	}else{
		return true;
	}
}


function getBoxControlRoot(source){
	var root = source;
	//search up the tree for boxcontrol
	while(!dojo.hasClass(root,'boxcontrol')){
		root = root.parentNode;
	}
	return root;
}

function showMiniform(event){
	changeDisplay(event,"block");
}

function hideMiniform(event){
	changeDisplay(event,"none");
}

function changeDisplay(event, val){
	var root = getBoxControlRoot(event.target);
	dojo.query("[id='miniform']",root).style({"display" : val});

}	

function show(event){
	makeVisible(event.target,true);
}

function makeVisible(root,visible){
	dojo.query("[type='button']",root)
		.attr({style:{visibility:(visible ? 'visible' : 'hidden')}});
}

function hide(event){
	makeVisible(event.target,false);
}