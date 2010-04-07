dojo.require("duracloud._base");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.TooltipDialog");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.form.Button");

dojo.provide("duracloud.duraservice");

duracloud.duraservice = {
		
	_createTable: function(/*title*/title, data, /*object*/properties){
		if(properties === undefined){
			properties = null;
		}
		var table = dojo.create("table", properties);
		dojo.addClass(table,"standard");
		var header = dojo.create("tr",null, table);
		dojo.create("th", {colspan: 2, innerHTML:title}, header);
		populateTable(table,data);
		return table;
	},
	
	loadConfigurationDetails: function (configNode, serviceId, deploymentId){
		var that = this;
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
				var table = that._createTable("Properties", data, {});
				configNode.appendChild(table);
			},
			error: function(responseObject, ioArgs){
		          console.error("HTTP status code: ", ioArgs.xhr.status); 
				  var data = new Array(['unable to load properties:', "HTTP status code: "+ ioArgs.xhr.status]);
				  populateTable(table, data);
			}
		});
	},
	
	undeployService: function (serviceInfoId, deploymentId, /*DomNode - in which to display error*/ node, /*Function*/ loadFunc){
		clearFlashMessage();
		dojo.xhrGet({
		    url: "/duradmin/services/undeploy?serviceInfoId="+serviceInfoId+"&deploymentId=" + deploymentId,
		    handleAs: "json",
		    load: loadFunc,
			error: function(responseObject, ioArgs){
		          console.error("HTTP status code: ", ioArgs.xhr.status); 
		          duracloud.showError(node, ioArgs);
			}
		});
	},
	


	_getServices: function (/*type deployed|available*/listType, node,/*Function loadFunc*/loadFunc){
		clearFlashMessage();
		duracloud.showWaitMessage(node,"loading");
		var theUrl = "/duradmin/services?format=json&show=" + listType;
		//theUrl = "/duradmin/test/services.json";
		dojo.xhrGet({
		    url: theUrl,
		    handleAs: "json",
		    load: loadFunc,
			error: function(responseObject, ioArgs){
		          console.error("HTTP status code: ", ioArgs.xhr.status); 
		          duracloud.showError(node, ioArgs);
			}
		});
	},

	


	_createServicePanel: function(/*service object*/service){
		var that = this;
        var div = dojo.create("div");
        

        var deploymentTabs = new dijit.layout.TabContainer({style: "height: 100%; width: 100%;", doLayout:false});
		var d = null;
		for(d in service.deployments){
			var deployment = service.deployments[d];
			var dContent = dojo.create("div");
			var messageDiv = dojo.create("div",{},dContent);
			var actionList = dojo.create("ul", {class: "action-list"},dContent);
			var li = dojo.create("li", {}, actionList);
			dojo.create("a", {innerHTML: "Reconfigure", href: "/duradmin/services/deploy?serviceId=" + service.id + "&deploymentId=" + deployment.id},li);
			var undeployLink = dojo.create("a", {href:"javascript:void(0)", innerHTML: "Undeploy"},dojo.create("li", {}, actionList));
			dojo.connect(undeployLink,"onclick", function(){
				duracloud.duraservice.undeployService(service.id, deployment.id, messageDiv, function(responseObject, ioArgs) {
		    		try{
				      deploymentTabs.removeChild(dtab);
				      setFlashMessage("Successfully undeployed " + service.displayName);
				      if(deploymentTabs.getChildren().length == 0){
				      	dropdown.destroyRecursive();
				      }
				      
				      deployAnother();
				      duracloud.duraservice.loadAvailablePanel(dijit.byId("available"));
		    		}catch(error){
		    			console.error(error);
		    			setFlashMessage(error, "error");
		    		}
	    		});
			});


			if(deployment.userConfigs != undefined){
				var props = new Array();				
				var prop = null;

				for(var i = 0; i < deployment.userConfigs.length; i++){
					prop = new Array();
					var uc = deployment.userConfigs[i];
					prop[0] = uc.displayName != undefined ? uc.displayName : "displayName prop is undefined";
					prop[1] = this._resolveUserConfigValue(uc);
					props[i] = prop;
				}
				
				var table = that._createTable("User Configuration", props, {});
				dContent.appendChild(table);
			}
			var dtab = new dijit.layout.ContentPane({title: deployment.hostname,content: dContent});
			
			var cd = dojo.create("div", {class:"configDetails"}, dContent);
			duracloud.duraservice.loadConfigurationDetails(cd, service.id, deployment.id);
			
			deploymentTabs.addChild(dtab);
		}	

		var tooltip = new dijit.TooltipDialog({content:deploymentTabs.domNode, style:"display:none"});
		var dropdown = new dijit.form.DropDownButton({
            label: "Deployments",
            dropDown: tooltip,
        });


        var row = new Array();
		row[0] = service.displayName + " v" + service.serviceVersion;
		row[1] = service.description;
		var actionSpan = dojo.create("span");
		row[2] = actionSpan;
		var deployAnother = function (){
			dojo.create("a", {innerHTML: "Deploy New Instance", href: "/duradmin/services/deploy?serviceId=" + service.id}, actionSpan);
		};
		
		if(service.maxDeploymentsAllowed < service.deployments.length){
			deployAnother();	
		}	

        actionSpan.appendChild(dropdown.domNode);
        

        var table = dojo.create("table", {"class": "deployed-service"}, div);
        var rows = new Array();
        rows.push(row);
        populateTable(table, rows);
        deploymentTabs.startup();
        return div;
	},
	
	loadAvailablePanel: function(/*ContentPane*/avail){
		this._getServices("available", avail.containerNode, function(responseObject, ioArgs){
    		console.debug("responseObject = " + responseObject);
    		var services = responseObject.list;
    		var sv = null;
    		var contents = "";
    		if(services.length == 0){
    			avail.attr("content", "There are no available services to deploy.");
    			return;
    		}else{
    			avail.attr("content", "");
    		}

			var table = dojo.create("table", {class: "standard available-services"}, avail.containerNode);
			var rows = new Array();
			for(sv in services){
    			var service = services[sv];
    			var deployLink = dojo.create("a", {href: "/duradmin/services/deploy?serviceId=" + service.id, innerHTML: "deploy"});
    			var row = new Array();
    			row[0] = service.displayName;
    			row[1] = service.description;
    			row[2] = deployLink;
    			
    			rows.push(row);
    		}
    		populateTable(table, rows);
		});
	},
	
	loadDeployedPanel: function(/*ContentPane*/deployed, /*ContentPane*/available){
		var that = this;
		that._getServices("deployed", deployed.containerNode, function(responseObject, ioArgs){
	    	console.debug("responseObject = " + responseObject);
			var services = responseObject.list;
			deployed.attr("content", "");
			
			if(services.length == 0){
		    	var tabs = dijit.byId("tabs");
		    	tabs.selectChild(available);
		    	deployed.attr("content", "There are no services currently deployed.");
		    	return;
			}
			var sv = null;
			for(sv in services){
				var s = that._createServicePanel(services[sv]);
				deployed.containerNode.appendChild(s);
			}
		});				    	
	},
	
	_resolveUserConfigValue: function (uc){
		var value = "no value";

		if(uc.value != undefined){
			return uc.value;
		}else {
			if(uc.options != undefined){
    			var options = uc.options[0];
    			var count =	 0;
    			for(var i = 0; i < options.length; i++){
    				var option = options[i];
    				if(option.selected.toString() == "true"){
    					if(count > 0) value+=", ";
	    				value += option.displayName;
	    				count++;
    				}
    			}
			}
		}
		return value;
	}

}

