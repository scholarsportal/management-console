dojo.require("dojox.encoding.base64");
dojo.require("dojo.cookie");

dojo.provide("duracloud._base");

duracloud = function(){};

duracloud.showWaitMessage = function (node, messageText){
		if(node == null){
			node = dojo.byId("flashMessageDiv");
		}
		node.innerHTML = "";
		var div = dojo.create("div",null, node);
		var img =  dojo.create("img", {src:"/duradmin/images/wait.gif" },div);
		dojo.create("span", {innerHTML: messageText},div);
};


duracloud.showError = function (node, ioArgs){
	var messageText = "Unable to complete request: status (" + ioArgs.xhr.status + ")";
	node.innerHTML = "";
	var div = dojo.create("div",null, node);
	var img =  dojo.create("img", {src:"/duradmin/images/error.png" },div);
	dojo.create("span", {innerHTML: messageText},div);
};
	
duracloud.formatSpaceMetadataHtml = function (/*Object*/space){
		  var metadata = space.metadata;
		  var count = metadata.count;
		  var created  = metadata.created;
		  var access  = metadata.access;

		  return "Access: " + access + "<br/>" + "Created on " + created + "<br/>" + count +  " items";
};

duracloud.formatContentItemMetadataHtml =  function(/*ConentItem Object*/ci){
		  var metadata = ci.metadata;
		  var table = dojo.create("table", {class:"content-item-metadata-summary"});
	      var row = addRow(table);
		  addCell("modified", row);
	      addCell(metadata.modified, row);
	      var tn = ci.tinyThumbnailURL;
	      if(tn != undefined && tn != null){
	    	  var thumblink = dojo.create("a", {target:"viewer", href: ci.viewerURL});
	    	  dojo.create("img", {src:tn, style:"height:75px;width:75px"}, thumblink);
	    	  var cell = addCell(thumblink, row);
	          dojo.attr(cell, "rowspan", "3");
	      }

	      
	      row = addRow(table);
	      addCell("mimetype", row);
		  addCell(metadata.mimetype, row);
		  row = addRow(table);
		  addCell("size", row);
		  addCell(metadata.size + " bytes", row);
	      
		  row = addRow(table);
		  addCell("checksum", row);
		  addCell(metadata.checksum, row);
	      return table;
};


	
	


addRow = function (tableElement){
	return dojo.create("tr", null, tableElement);
};


addCell = function (contents, row){
	if(typeof contents == "string"){
		return dojo.create("td", {innerHTML: contents}, row);
	}else{
		var cell = dojo.create("td", null, row);
		if(contents != undefined && contents != null){
			cell.appendChild(contents);
		}
		return cell;
	}
};

	
populateTable = function (table, data) {
	for(i = 0; i < data.length; i++){
		var row = dojo.create("tr",null,table);
		for(j = 0; j < data[i].length; j++){
			addCell(data[i][j],row);
		}
	}
};

showInProcessOfDeletionFeedback = function (element){
	clearFlashMessage();
	dojo.addClass(element, "removing-element");
	
    var animation = dojo.animateProperty({
        node : element,
        duration : 500,
        properties : {
            opacity : {start : '1.0', end : '0.50'}
        }
    });

    animation.play();
    return animation;
};

onDeletionSucceeded = function (element, message){
	dojo.fadeOut({
        node: element,
        duration : 500,
        delay: 1000,
        onEnd: function () {
			element.parentNode.removeChild(element);
		}
    }).play(  );
};


cancelShowInProcessOfDeletionFeedback = function (element){
	dojo.removeClass(element, "removing\-element")
};


clearFlashMessage = function (){
	dojo.byId("flashMessageDiv").innerHTML = "";
};

setFlashInfoMessage =  function (message){
	setFlashMessage(message, "info");
};

setFlashMessage = function (message, type){
//	clearFlashMessage();
//	var div = dojo.byId("flashMessageDiv");
//	var span = dojo.create("span", {innerHTML: message}, div);
//	dojo.addClass(span, "message-" + type);
	
    var toaster = dijit.byId("toaster1");
    toaster.setContent(message, type, 3000);
    toaster.show();
};

confirmDeleteOperation = function (e){
	var result = confirm('You are about to perform an irreversible operation\.\nClick \'OK\' if you are sure you wish to continue\.');
	if(!result) { 
		return false; 
	}else{
		return true;
	}
};


getBoxControlRoot = function (source){
	var root = source;
	//search up the tree for boxcontrol
	while(!dojo.hasClass(root,'boxcontrol')){
		root = root.parentNode;
	}
	return root;
};

showMiniform = function (event){
	changeDisplay(event,"block");
};

hideMiniform = function (event){
	changeDisplay(event,"none");
};

changeDisplay = function (event, val){
	var root = getBoxControlRoot(event.target);
	dojo.query("[id='miniform']",root).style({"display" : val});

};	

show = function (event){
	makeVisible(event.target,true);
};

makeVisible = function (root,visible){
	dojo.query("[type='button']",root)
		.attr({style:{visibility:(visible ? 'visible' : 'hidden')}});
};

hide = function (event){
	makeVisible(event.target,false);
};

convertToByteArray = function(data){
    var bin=[] ;
    for (var i=0; i<data.length; i++){
    bin.push(data.charCodeAt(i));
    }
    return bin;
};

getAuthHeaders = function(){
	var cookie = dojo.cookie("auth");
	return	{"Authorization": cookie };	
};

formatCredentials = function(username, password){
	var credentials = convertToByteArray(username + ":" + password);
	return ("Basic " + dojox.encoding.base64.encode(credentials));
};

storeCredentials = function(){
	var form = dojo.byId("loginForm");
	var username = form["j_username"].value;
	var password = form["j_password"].value;
	dojo.cookie("auth", formatCredentials(username,password));
};








