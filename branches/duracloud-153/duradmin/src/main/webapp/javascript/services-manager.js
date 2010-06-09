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
 * Service Widgets
 */
(function(){
	$.widget('ui.serviceconfig', { 
		_service: null,
		_controlContainer: null,
		_init: function(){
			this.element.html("");
			this._controlContainer =  $.fn.create("fieldset");
			this.element.append(
				$.fn.create("span").addClass("dc-message")
			).append(
				$.fn.create("form").append(
					$.fn.create("div").addClass("form-fields h400").append(
							this._controlContainer
					)
				)
			);
		},

		
		_destroy: function(){
			$(".dc-message", this.element).html("");
			
		},
		options: {},
		_createControl: function(/*userConfig object*/uc){
			return $.fn.create("input").attr("type", "text").attr("id", uc.name);
		},
		
		_createListItem: function(fieldId, displayName){
			var li = $.fn.create("li").addClass("row clearfix");
			
			li.append(
					$.fn.create("label").attr("for", fieldId).html(displayName)
			);
			
			return li;
		},

		service: function(){
			return this._service;
		},
		
		configure: function(service){
			this._service = service;
			console.debug("loading service: " + service.displayName);
			var userConfigs = service.userConfigs;
			this._controlContainer.html("");
			this._controlContainer.append(
				$.fn.create("input").attr("type", "hidden").val(service.id)
			);


			
			var list = $.fn.create("ul");
			this._controlContainer.append(list);
			
			var dOptions = service.deploymentOptions;
			var locationSelect = $.fn.create("select").attr("id","location");
			for(i in dOptions){
				var o = dOptions[i];
				locationSelect.append($.fn.create("option").attr("value", o.hostname).html(o.displayName + " - " + o.hostname + " - " + o.location));
			}
			
			list.append(
				this._createListItem("location", "Location").append(locationSelect)
			);
			
			
			if(userConfigs != undefined && userConfigs != null && userConfigs.length > 0){
				for(i in userConfigs){
					var uc = userconfigs[i];
					
					list.append(
							this._createListItem(uc.id, uc.displayName)
								.append(this._createControl(uc))
					);
				}	
			}
			/*
			<form enctype="multipart/form-data">
			<div id="form-fields" class="form-fields h400">
				<fieldset>
					<ul>
						<li class="row clearfix first-of-type">
							<label for="host">Select Host</label>
							<select name="host" id="host" class="field" />
								<option value="null default">- Select one -</option>
								<option value="1">First option</option>
								<option value="2">Second option</option>
							</select>
						</li>
						<li class="row clearfix"><label for="textinput1">Text Input</label><input type="text" name="textinput1" id="dropdown1" class="field" /></li>
						<li class="row clearfix">
							<label for="dropdown1">Dropdown with a really long name that wraps</label>
							<select name="dropdown1" id="dropdown1" class="field" />
								<option value="null default">- Select one -</option>
								<option value="1">First option</option>
								<option value="2">Second option</option>
							</select>
						</li>
						<li class="row clearfix">
						<label for="checkboxes">Checkboxes</label>
							<ul class="field">
								<li><input type="checkbox" id="c1" />Checkbox 1</li>
								<li><input type="checkbox" id="c2" />Checkbox 2</li>
								<li><input type="checkbox" id="c3" />Checkbox 3</li>
								<li><input type="checkbox" id="c4" />Checkbox 4</li>
								<li><input type="checkbox" id="c1" />Checkbox 1</li>
								<li><input type="checkbox" id="c2" />Checkbox 2</li>
								<li><input type="checkbox" id="c3" />Checkbox 3</li>
								<li><input type="checkbox" id="c4" />Checkbox 4</li>
								<li><input type="checkbox" id="c1" />Checkbox 1</li>
								<li><input type="checkbox" id="c2" />Checkbox 2</li>
								<li><input type="checkbox" id="c3" />Checkbox 3</li>
								<li><input type="checkbox" id="c4" />Checkbox 4</li>
								<li><input type="checkbox" id="c1" />Checkbox 1</li>
								<li><input type="checkbox" id="c2" />Checkbox 2</li>
								<li><input type="checkbox" id="c3" />Checkbox 3</li>
								<li><input type="checkbox" id="c4" />Checkbox 4</li>
							</ul>
						</li>
					</ul>
				</fieldset>
			</div>
		</form>
		*/
		
		},
		reconfigure: function(service,deployment){
			this.element.html("loading service: " + service.displayName + ", deployment: " + deployment.id);
		},

	});
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
		var panel = $("#deployed-services");
		var table = $("#deployed-services-table");
		
		table.hide();
		$(".dc-message", panel).remove();
		
		servicesList.selectablelist({selectable: false});
		servicesList.selectablelist("clear");
		
		if(services == null || services == undefined || services.length == 0){
			panel.append($.fn.create("span").addClass("dc-message").html("No services are currently deployed."));
			return;
		}
		
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
	
	
/*	
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
	*/
	
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
	
	var refreshDeployedServices = function(){
		dc.service.GetDeployedServices({
			begin: function(){
				dc.busy("Retrieving available services...");
			},
			success: function(data){
				dc.done();
				loadDeployedServiceList(data.services);
			},
			failure: function(text){
				dc.done();
				alert("get available services failed: " + text);
			},
		});
	};

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

				$(this).dialog("close");
				
				var service = currentItem.data;
				var confElement = $("#configure-service-dialog");
				$("#service-config").serviceconfig({}).serviceconfig("configure", service);
				confElement.dialog("open");
				
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
			
			
			dc.service.GetAvailableServices({ 
				begin: function(){
					$("#available-services-dialog .dc-message").html("Loading...");
				},
				success: function(data){
					$("#available-services-dialog .dc-message").html("");
					var services = data.services;
					if(services == undefined){
						$("#available-services-dialog .dc-message").html("There are no available services.");
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
					$("#available-services-dialog .dc-message").hide();
					$("#available-services-dialog .dc-message").html("Unable to load services: " + text);
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
				
				var service = $("#configure-service-dialog .service-config")
									.serviceconfig("service");
				$("#configure-service-dialog").dialog("close");
				
				var form = $("#configure-service-dialog form");

				$(form).ajaxSubmit(
					{
						url: "/duradmin/services/service?action=post",
						success: function(){
							dc.showTransientStatus("successfully deployed the service");
							refreshDeployedServices();
						},
					
						error: function(xhr, textStatus, errorThrown){
					    	alert("failed: " + textStatus);
					    },
					});
			},
			
			"Cancel": function(){
				$("#configure-service-dialog").dialog("close");
			}
		},
		
		open: function(e){
		},
	});
	

	$(".ui-dialog-titlebar").hide();


	refreshDeployedServices();

});