if(!dojo._hasResource["duracloud.ui"]){
	dojo._hasResource["duracloud.ui"]=true;
	dojo.provide("duracloud.ui");
	dojo.require("duracloud._base");
	dojo.require("dijit.Dialog");
	
	(function(){
		duracloud.ui.showWait = function (message){
	        var dialog = new dijit.Dialog({
	            style: "width: 300px;",
	            closable: false
	        });
	        
	        var div = dojo.create("div");
	        
	        dojo.create("img", {src:"/duradmin/images/wait.gif" },div);
	        dojo.create("span", {innerHTML:message},div);
	        dialog.attr("content", div);
	        dialog.show();

		};

	
	})();
}

