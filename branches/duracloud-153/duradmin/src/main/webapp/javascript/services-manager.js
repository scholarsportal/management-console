/**
 * Duraserivce AJAX API
 * @author Daniel Bernstein
 */
var dc; 

(function(){
	if(dc == undefined){
		dc ={};
	}
	
	dc.service = {};
	
	var APP_CONTEXT = "/duradmin";
	var SERVICES_URL_BASE = APP_CONTEXT+"/services?f=json&method=";

	/**
	 * Returns a list of available services.
	 */
	 dc.service.GetAvailableServices = function(callback){
		 dc.ajax({
			 url: SERVICES_URL_BASE + "available",
		 }, callback);
	 };

	/**
	 * Returns a list of deployed services.
	 */
	 dc.service.GetDeployedServices = function(callback){
		 dc.ajax({
			 url: SERVICES_URL_BASE + "deployed",
		 }, callback);
	 };

})();


/**
 * 
 * created by Daniel Bernstein
 */


$(document).ready(function() {

	var serviceDetailPaneId = "#service-detail-pane";
	var detailPaneId = "#detail-pane";
	var servicesListViewId = "#services-list-view";
	var servicesListId = "#services-list";
	
	var centerLayout = $('#page-content').layout({
		west__size:				800
	,   west__paneSelector:     servicesListViewId
	,   west__onresize:         "servicesListPane.resizeAll"
	,	center__paneSelector:	detailPaneId
	,   center__onresize:       "detailPane.resizeAll"
	});
	
	
	
	var servicesListPaneLayout = {
			north__paneSelector:	".north"
		,   north__size: 			35
		,	center__paneSelector:	".center"
		,   resizable: 				false
		,   slidable: 				false
		,   spacing_open:			0			
		,	togglerLength_open:		0	
	};
			
	var servicesListPane = $(servicesListViewId).layout(servicesListPaneLayout);
	
	//detail pane's layout options
	var detailLayoutOptions = {
				north__paneSelector:	".north"
				,   north__size: 			200
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
				
	};
	
	var detailPane = $(detailPaneId).layout(detailLayoutOptions);

	
	var deploy = function(service, future){
		//alert("ajax call here");
		future.success();
	};

	var undeploy = function(service, deployment, future){
		//alert("ajax call here");
		
		future.success();
		$(servicesListId).selectablelist("removeById", deriveDeploymentId(service,deployment));
	};
	
	
	var resolveServiceCssClass = function(services){
		return "service-replicate";
	};

	
	var loadDeploymentDetail = function(service,deployment){
		
		if(service == null){
			
			$(detailPaneId).fadeOut("slow", function(){
				$(this).html('');
			});
			return;
		};
		var serviceDetailPane = $(serviceDetailPaneId).clone();

		
		//set the title of the pane
		$(".service-name", serviceDetailPane.first()).html(service.displayName);
		$(".service-version", serviceDetailPane.first()).html(service.serviceVersion);
		
		var centerPane = $(".center",serviceDetailPane.first());
		centerPane.html("");

		//deploy/undeploy switch definition and bindings
		$(".deploy-switch",serviceDetailPane.first()).onoffswitch({
			   		initialState: "on"
					, onStateClass: "unlocked"
					, onIconClass: "checkbox"
					, offStateClass: "locked"
					, offIconClass: "x"
					, onText: "Deployed"
					, offText: "Undeploy"		
		}).bind("turnOff", function(evt, future){
			undeploy(service, deployment,future);
		});
		
		$(".reconfigure-button",serviceDetailPane.first()).click(function(){
			$("#reconfigure-service-dialog").dialog("open");
		});

		getServiceDeploymentConfig(service, deployment, {
			success: function(config){
				var data = new Array();
				for(i = 0; i < config.length; i++){
					data[i] = [config[i].name, config[i].value];
				}			
				
				centerPane.prepend(
						$.fn.create("div")
						.tabularexpandopanel(
							{
							  title: "Details", 
							  data:  data
			                }
						)
					);
				
			},
		});

		
		
		centerPane.append(
			$.fn.create("div")
			.tabularexpandopanel(
				{title: "Configuration", 
				 data: convertUserConfigsToArray(deployment.userConfigs)
                 }
			)
		);
		
		$(detailPaneId).replaceContents(serviceDetailPane, detailLayoutOptions);

	};
	
	
	var convertUserConfigsToArray = function(userConfigs){
		var a = new Array();
		for(u in userConfigs){
			var uc = userConfigs[u];
			a.push([uc.displayName, resolveUserConfigValue(uc)]);
		}
		
		return a;
	};
	
	////////////////////////////////////////////////////////
	///util functions for handling service deployment element ids
	var deriveDeploymentId = function(service, deployment){
		return service.id + "-" + deployment.id;
	};

	var extractServiceId = function(id){
		return id.split("-")[0];
	};

	var extractDeploymentId = function(id){
		return id.split("-")[1];
	};


	var resolveUserConfigValue =  function (uc){
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
	};

	var loadDeployedServiceList = function(services){
		var servicesList = $(servicesListId);
		servicesList.selectablelist({selectable: false});
		servicesList.selectablelist("clear");
		var defaultServiceSet = false;
		for(s in services){
			var service = services[s];
			for(d in service.deployments){
				var deployment = service.deployments[d];
				var item =  $.fn.create(("tr"))
								.attr("id", deriveDeploymentId(service,deployment))
								.addClass("dc-item")
								.addClass(resolveServiceCssClass(service))
								.append($.fn.create("td").addClass("icon").append($.fn.create("div")))
								.append($.fn.create("td").html(service.displayName + " - " + service.serviceVersion))
								.append($.fn.create("td").html(deployment.hostname))
								.append($.fn.create("td").html(deployment.status));
								
				servicesList.selectablelist('addItem',item,{service:service, deployment:deployment});	   
			
				if(!defaultServiceSet){
					loadDeploymentDetail(service,deployment);
					defaultServiceSet = true;
				}		
			}
		}
		
		//bind for current item change listener
		servicesList.bind("currentItemChanged", function(evt,state){
			var data = state.data;
			var service = null;
			var deployment = null;
			
			if(data != null || data != undefined){
				service = data.service;
				deployment = data.deployment;
			}

			loadDeploymentDetail(service,deployment);
		});

	};
	
	
	
	var getDeployedServices = function(callback){
		var services = [
		                
		                {	
		                		id: 0
		                	,   displayName: "Replication Service"
		                	,   serviceName:  "replication-service"
		                	,   serviceVersion: "1.0.0"
		                	,   deployments: [
		                	                  {
		                	                	  	id: "1"
		                	                	,	hostname: "127.0.0.1"
		                	                	, 	status: "OK"
		                	                	,   started: "Jan 1, 1970 00:00:00 UTC"
		                	                	,   userConfigs: [
		                	                	                  {
		                	                	                	  	displayName: "user config name1"
		                	                	                	 ,	options: [
				                	                	                           {
				                	                	                        	   selected: false,
				                	                	                        	   displayName: "option1",
				                	                	                        	   
				                	                	                           },
				                	                	                           {
				                	                	                        	   selected: true,
				                	                	                        	   displayName: "option2",
				                	                	                        	   
				                	                	                           },
				                	                	                          ]
				                	                	                   
		                	                	                  },
			                	                	              {
		                	                	                  		displayName: "user config name2"
			                	                	                 ,	displayValue: "user config value2",
			                	                	               },
		                	                	                  ],
		                	                  
		                	                		
		                	                  }
		                	                 ]
		                },
		                
		                {	
	                		id: 2
	                	,   displayName: "Image Magic"
	                	,   serviceName:  "image-magic-service"
	                	,   serviceVersion: "1.0.0"
	                	,   deployments: [
	                	                  {
	                	                	  	id: "1"
	                	                	,	hostname: "127.0.0.2"
	                	                	, 	status: "OK"
	                	                	,   started: "Jan 6, 1972 00:00:00 UTC"
	                	                	,   userConfigs: [
	                	                	                  {
	                	                	                	  	displayName: "some user config1"
	                	                	                	 ,	options: [
			                	                	                           {
			                	                	                        	   selected: false,
			                	                	                        	   displayName: "suc value 1",
			                	                	                        	   
			                	                	                           },
			                	                	                           {
			                	                	                        	   selected: true,
			                	                	                        	   displayName: "suc value 2",
			                	                	                        	   
			                	                	                           },
			                	                	                          ]
			                	                	                   
	                	                	                  },
		                	                	              {
	                	                	                  		displayName: "user config x name"
		                	                	                 ,	displayValue: "user config x value",
		                	                	               },
	                	                	                  ],
	                	                  
	                	                		
	                	                  }
	                	                 ]
	                },
		                
		                ];
		
		//implement ajax call here and remove the above mock data
		callback.load(services);
	};

	
	var getServiceDeploymentConfig = function(service, deployment, callback){
		//implement ajax call here
		var config = [
		              {name: "name1", value:"value1"},
		              {name: "name2", value:"value2"},
		              {name: "name3", value:"value3"},
		              {name: "name4", value:"value4"},
		              {name: "name5", value:"value5"},
		
		];
		
		callback.success(config);
		
	};
	getDeployedServices({load: loadDeployedServiceList});
	
	
	//dialogs
	
	var dialogLayout; 
	
	$('#available-services-dialog').dialog({
		autoOpen: false,
		show: 'fade',
		hide: 'fade',
		resizable: false,
		height: 500,
		closeOnEscape:true,
		modal: true,
		width:700,
		//resize:		function () { dialogLayout.resizeAll(); },
		
		buttons: {
			Cancel: function(){
				$(this).dialog("close");
			},
			Next: function(){
				var currentItem = $("#available-services-list").selectablelist("currentItem");

				if(currentItem == null || currentItem == undefined){
					alert("You must select a service.");
					return;
				}
				
				var service = currentItem.data;
				
				alert("configuring service " + service);
				$(this).dialog("close");
				$("#configure-service-dialog").dialog("open");
				
			}
		
		},
		close: function() {
	
		},
		
		
		
		open: function(e){	
			var that = this;
			
			if(!dialogLayout){
				dialogLayout = $(this).layout({
					resizeWithWindow:	false	// resizes with the dialog, not the window
					,  spacing_open:  0
					,  spacing_closed: 0
					,	west__spacing_open:		6
					,	west__spacing_closed:	6
					,	west__size:			300
					,   north__resizable: 			false
					,	north__slidable: 			false
					,   west__slidable: 		true
					,   west__resizable:        true
					
				});
			}
			
			$("#available-services-dialog").listdetailviewer(
				{
						selectableListId: "available-services-list"
					,	detailId:  "service-detail"
					,   detailPreparer: function(data){
							return $.fn.create("div")
										.append($.fn.create("h2").html(data.displayName))
										.append($.fn.create("p").html(data.description));
						}
				}
			);
			
			
			$("#available-services-dialog").glasspane({});
			
			dc.service.GetAvailableServices({ 
				begin: function(){
					$("#available-services-dialog").glasspane("show", "Retrieving available services...")
				},
				success: function(data){
					$("#available-services-dialog").glasspane("hide");
					var services = data.services;
					if(services == undefined){
						alert("No available services!");
						this.close();
						return;
					}
					for(i in services){
						var service = services[i];
						$("#available-services-list")
							.selectablelist("addItem", $.fn.create("tr")
												.attr("id", service.id)
												.addClass(dc.getServiceTypeImageClass(service.serviceName))
												.append($.fn.create("td").addClass("icon").append($.fn.create("div")))
												.append($.fn.create("td").html(service.displayName)), service);

					}
				},
				failure: function(text){
					$("#available-services-dialog").glasspane("hide");
					alert("get available services failed: " + text);
				},
				
			});
			
			

		},
		
	});

	
	
	$(".deploy-service-button").click(function(){
		$("#available-services-dialog").dialog("open");
	});


	$('#reconfigure-service-dialog').dialog({
		autoOpen: false,
		show: 'fade',
		hide: 'fade',
		resizable: false,
		height: 500,
		closeOnEscape:true,
		modal: true,
		width:500,
		buttons: {
			"Redeploy": function(){
				$("#reconfigure-service-dialog").dialog("close");
			},
			
			"Cancel": function(){
				$("#reconfigure-service-dialog").dialog("close");
			}
		},

		close: function() {
		
		},
		open: function(e){
		},
	});
	


	
	$('#configure-service-dialog').dialog({
		autoOpen: false,
		show: 'fade',
		hide: 'fade',
		resizable: false,
		height: 500,
		closeOnEscape:true,
		modal: true,
		width:700,
		close: function() {
		
		},
		
		buttons: {
			"< Back": function(){
				$("#configure-service-dialog").dialog("close");
				$("#available-services-dialog").dialog("open");

			},
			
			"Deploy": function(){
				$("#configure-service-dialog").dialog("close");
			},
			
			"Cancel": function(){
				$("#configure-service-dialog").dialog("close");
			}
		},
		
		open: function(e){
		},
	});
	

	$(".ui-dialog-titlebar").hide();


});