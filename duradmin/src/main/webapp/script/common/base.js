dojo.require("dijit.TooltipDialog");
dojo.require("dijit.Dialog");

function loadContentItem(nodeId, spaceId, contentId){
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
	          return responseObject;
		}

	});
}

function showWaitMessage(node, messageText){
	node.innerHTML = "<div><img src='/duradmin/images/wait.gif'/><span>"+messageText+"</span></div>";
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
      return modified + "<br/>" + mimetype + "<br/>" + size + "<br/>" + checksum;
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
	          return responseObject;
		}

	});
}

function formatSpaceMetadataHtml(space){
	  var metadata = space.metadata;
	  var count = metadata.count;
	  var created  = metadata.created;
    return "Created on " + created + "<br/>" + count +  " items";
}




/*zebra stripe standard tables*/	
dojo.addOnLoad(function(){
	dojo.query(".standard > tbody > tr:nth-child(even)").addClass("evenRow");
});



dojo.addOnLoad(function(){
	/*adds mouse listeners on spaces table rows*/
	dojo.query("#spacesTable > tbody > tr",document).forEach(
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
	var result = confirm('You are about to perform an irreversible delete operation\.\nClick \'OK\' if you are sure you wish to continue\.');
	if(!result) { 
		return false; 
	}else{
		return true;
	}
}