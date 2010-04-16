dojo.require("duracloud._base");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.BorderContainer");
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
		dojo.create("th", {"colspan": 2, "innerHTML":title}, header);
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
		    load: function(responseObject, ioArgs) {
				dojo.attr(node, "innerHTML", "");
				loadFunc(responseObject, ioArgs);
				
			},
			error: function(responseObject, ioArgs){
		          console.error("HTTP status code: ", ioArgs.xhr.status); 
		          duracloud.showError(node, ioArgs);
			}
		});
	},

	
	addServicePanel: function (/*service*/service){
		var that = this;

		var deployedList = dijit.byId("deployedList");
		var deployed = dijit.byId("deployed");
		//define left hand clickable panel
		var panel = new dijit.layout.ContentPane({style:"max-height: 100px", content: service.displayName});
		deployedList.containerNode.appendChild(panel.domNode);
		dojo.addClass(panel.domNode, "label-panel")

		dojo.connect(panel.domNode, "onclick", function(){
			deployed.addChild(details);
		});

		
		//define details panel 
		var details = new dijit.layout.BorderContainer({region:"center"});
		var topPane = new dijit.layout.ContentPane({region:"top"});
		details.addChild(topPane);
		var namePane = dojo.create("div", {innerHTML : service.displayName + " v." + service.serviceVersion});
		var actionPane = dojo.create("div");
		topPane.containerNode.appendChild(namePane);
		topPane.containerNode.appendChild(actionPane);
		
		if(service.maxDeploymentsAllowed < service.deployments.length){
			dojo.create("a", {"innerHTML": "Deploy New Instance", "href": "/duradmin/services/deploy?serviceId=" + service.id}, actionPane);
		}	
		
		//add tabs
		var tabs = new dijit.layout.TabContainer({region: "center"});
		details.addChild(tabs);
		
		var d = null;
		for(d in service.deployments){
			var deployment = service.deployments[d];
			var tab = new dijit.layout.ContentPane({title: deployment.hostname});
			tabs.addChild(tab);
			var tabContent = tab.containerNode;

			dojo.create("a", {innerHTML: "Reconfigure", href: "/duradmin/services/deploy?serviceId=" + service.id + "&deploymentId=" + deployment.id},tabContent);
			var undeployLink = dojo.create("a", {"href":"javascript:void(0)", "innerHTML": "Undeploy"}, tabContent);
			dojo.connect(undeployLink,"onclick", function(){
				duracloud.duraservice.undeployService(service.id, deployment.id, tabContent.containerNode, function(responseObject, ioArgs) {
		    		try{
					    
		    			setFlashMessage("Successfully undeployed " + service.displayName);
		    			
			    		dojo.fadeOut({
			    	        node: tab.domNode,
			    	        duration : 1000,
			    	        delay: 0,
			    	        onEnd: function () {
						      tabs.removeChild(tab);
						      if(tabs.getChildren().length == 0){
						    	 details.destroyRecursive();
						    	 var pd = panel.domNode;
						    	 pd.parentNode.removeChild(pd);
						    	 panel.destroyRecursive();
						      }
			    			}
			    	    }).play(  );


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
				tabContent.appendChild(table);
			}
			
			var cd = dojo.create("div", {"class":"configDetails"}, tabContent);
			duracloud.duraservice.loadConfigurationDetails(cd, service.id, deployment.id);
			
			
		}
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

			var table = dojo.create("table", null, avail.containerNode);
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
		that._getServices("deployed", deployed.getChildren()[0].containerNode, function(responseObject, ioArgs){
	    	console.debug("responseObject = " + responseObject);
			var services = responseObject.list;
			deployed.attr("content", "");
			
			if(services.length == 0){
		    	var tabs = dijit.byId("tabs");
		    	tabs.selectChild(available);
		    	deployed.getChildren()[0].attr("content", "There are no services currently deployed.");
		    	return;
			}
			
			var sv = null;
			
			
			for(sv in services){
				that.addServicePanel(services[sv]);
			}
			
		});
	},
	
	_resolveUserConfigValue: function (uc){
		if(uc.displayValue != undefined){
			return uc.displayValue;
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
    			
    			return value;
			}else{
				var value = "no value";
			}
		}
	}

}

