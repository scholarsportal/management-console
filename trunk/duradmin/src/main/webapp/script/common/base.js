dojo.require("dojo._base.html");
dojo.require("dojo.fx");

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
	          showError(node, ioArgs);
		}
	});
}


function removeTag(spaceId,tag, contentId, element){
	var location = "/duradmin/spaces/tag/remove?spaceId="+spaceId+"&tag=" + tag + "&contentId="+contentId;
	doRemoveCall(element, location);
}

function showInProcessOfDeletionFeedback(element){
	clearFlashMessage();
	dojo.addClass(element, "removing\-element");
    var animation = dojo.animateProperty({
        node : element,
        duration : 500,
        properties : {
            opacity : {start : '1.0', end : '0.50'}
        }
    });

    animation.play();
    return animation;
}

function onDeletionSucceeded(element, message){
	dojo.fadeOut({
        node: element,
        duration : 500,
        delay: 1000,
        onEnd: function () {
			element.parentNode.removeChild(element);
		}
    }).play(  );
}


function cancelShowInProcessOfDeletionFeedback(element){
	dojo.removeClass(element, "removing\-element")
}


function doRemoveCall(element, location) {
	var animation = showInProcessOfDeletionFeedback(element);
	dojo.xhrGet( {
	    url: location,
	    handleAs: "json",
	    load: function(responseObject, ioArgs) {
		  console.debug(responseObject);  // Dump it to the console
     	  //setOneSecondFloatingFlash(element, "successfully removed");
		  onDeletionSucceeded(element);

	},
		error: function(responseObject, ioArgs){
          console.error("HTTP status code: ", ioArgs.xhr.status); 
          animation.stop();
          cancelShowInProcessOfDeletionFeedback(element);
		}
	});
	
}

function removeMetadataByKey(spaceId,key, contentId, element){
	var location = "/duradmin/spaces/metadata/remove?spaceId="+spaceId+"&name=" + key + "&contentId="+contentId;
	doRemoveCall(element, location);
}

function clearFlashMessage(){
	dojo.byId("flashMessageDiv").innerHTML = "";
}
/*
 this doesn't work properly.  for some response, 
function setOneSecondFloatingFlash(refElement, message){
	var popup = dojo.create("div",{innerHTML: message},dojo.body()); //works if I substitute dojo.body() with refElement.parentNode. Not really what I want.
	var coords = dojo.coords(refElement,true);
	dojo.addClass(popup, "message-info");
	document.body.appendChild(popup);

	dojo.style(popup, 
			   {"opacity" : 1.0, 
				"visibility" : "visible", "z-index" : "16",  "top" : coords.y, "left": coords.x});
	var fi = dojo.fadeIn({
        node: popup,
        duration : 250
    });

	var fo = dojo.fadeOut({
        node: popup,
        duration : 500,
        delay: 2000,
        onEnd: function () {
			dojo.destroy(popup);
		}
    });
	
	dojo.fx.chain([fi,fo]).play(  );
	
}
*/
function setFlashInfoMessage(message){
	setFlashMessage(message, "info");
}

function setFlashMessage(message, type){
	clearFlashMessage();
	var div = dojo.byId("flashMessageDiv");
	var span = dojo.create("span", {innerHTML: message}, div);
	dojo.addClass(span, "message\-" + type);
}


function showWaitMessage(node, messageText){
	node.innerHTML = "";
	var div = dojo.create("div",null, node);
	var img =  dojo.create("img", {src:"/duradmin/images/wait.gif" },div);
	dojo.create("span", {innerHTML: messageText},div);

}

function showError(node, ioArgs){
	node.innerHTML = "";
	var div = dojo.create("div",null, node);
	var img =  dojo.create("img", {src:"/duradmin/images/error.gif" },div);
	var messageText = "Unable to complete request: status (" + ioArgs.xhr.status + ")";
	dojo.create("span", {innerHTML: messageText},div);
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
	//adds mouse listeners on spaces table rows
	//will throw an error if spaces table is not found
	try{
		
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
	}catch(err){
	}

});

dojo.addOnLoad(function(){
	
	dojo.query(".boxcontrol .miniform-button").forEach(
		    function(element) {
		    	dojo.connect(element, 'onclick', function(evt) {
		    		showMiniform(evt);
		    	});
		    }
		);		
	
	
	/*configure tag and metadata components*/
	dojo.query("div.tag, table.extended-metadata tr").forEach(
	    function(element) {
	    	dojo.connect(element, 'onmouseover', function() {
				dojo.query("[type=button]",element).attr('style', {visibility:'visible'});
	    	});

	    	dojo.connect(element, 'onmouseout', function() {
				dojo.query("[type=button]",element).attr('style', {visibility:'hidden'});
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

function showConfigurationDetails(event, serviceId, deploymentId){
	var root = event.target.parentNode.parentNode;
	var configNodeQuery = "[id='configurationDetails']";
	dojo.query(configNodeQuery,root).style("display","inline");
	dojo.query(configNodeQuery,root).forEach(function (configNode) {
		//remove property table if it exists.
		dojo.query("[id='propertyTable']", root).forEach(function(node){
			dojo.destroy(node);
		});
		
		var table = dojo.create("table", {id:"propertyTable"}, configNode);
		var header = dojo.create("tr",null, table);
		dojo.create("th", {colspan: 2, innerHTML:'Properties'}, header);

		dojo.xhrGet( {
		    // The following URL must match that used to test the server.
		    url: "/duradmin/data/services/deployment/properties?serviceInfoId="+serviceId + "&deploymentId="+deploymentId,
		    handleAs: "json",
		    load: function(responseObject, ioArgs) {
			console.debug(responseObject);
			  var properties = responseObject.properties;
			  var data = new Array();
			  for(i = 0; i < properties.length; i++){
				  data[i] = [properties[i].name, properties[i].value];
			  }
			  populateTable(table, data);
			},
			error: function(responseObject, ioArgs){
		          console.error("HTTP status code: ", ioArgs.xhr.status); 
				  var data = new Array(['unable to load properties:', "HTTP status code: "+ ioArgs.xhr.status]);
				  populateTable(table, data);
			}
		});
	});
}

function populateTable(table, data) {
	for(i = 0; i < data.length; i++){
		var row = dojo.create("tr",null,table);
		for(j = 0; j < data[i].length; j++){
			dojo.create("td", {innerHTML: data[i][j]}, row);
		}
	}
}