/**
 * 
 * @author Daniel Bernstein
 */

/**
 * Durastore API 
 */
var dc; 
(function(){
	if(dc == undefined){
		dc ={};
	}
	
	dc.store = {};

	
	/**
	 * @param String spaceId  space id 
	 * @param Object callback The callback must implement success and failure methods.
	 * @option Function success(spaces) spaces is an array of spaces objects
	 * @option Function failure(info) 
	 * @param Object options
	 * @option String marker - the last content item id of the previous page
	 * @option String prefix - a filters the results to show only those matching the prefix
	 */

	 dc.store.GetSpace = function(storeProviderId,spaceId,callback,options){
		 
		if(options == undefined){
			options = {};
		}
		
		var marker = null;
		if(options.marker != undefined){
			marker = options.marker;
		}

		var prefix = null;
			if(options.prefix != undefined){
				prefix = options.prefix;
			}

		if(callback.begin != undefined){
			callback.begin();
		}
		
		$.ajax({ url: "/duradmin/spaces/space", 
			data: "storeId="+storeProviderId+"&spaceId="+escape(spaceId)+"&prefix="+escape(prefix==null?'':prefix),
			cache: false,
			context: document.body, 
			success: function(data){
				callback.success(data.space);
			},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		alert("get spaces failed: " + textStatus);
		    },
		});		
		
	};
	
	/**
	 * @param String storeId  store id 
	 * @param String spaceId  space id 
	 * @param Object callback The callback must implement success and failure methods. options begin method is supported.
	 */
	dc.store.DeleteSpace = function(space, callback){

		if(callback.begin != undefined){
			callback.begin();
		}

		$.ajax({
			url: "/duradmin/spaces/space", 
			data: "action=delete&storeId="+space.storeId+"&spaceId="+escape(space.spaceId),
			type: "POST",
			success: callback.success,
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		callback.failure(textStatus);
			},
		});
	};

	/**
	 * @param String space  space
	 * @param String access  access 
	 * @param Object callback The callback must implement success and failure methods. options begin method is supported.
	 */
	dc.store.AddSpace = function(space, callback){

		if(callback.begin != undefined){
			callback.begin();
		}

		$.ajax({
			url: "/duradmin/spaces/space", 
			data: "storeId="+space.storeId+"&spaceId="+escape(space.spaceId)+"&access="+space.access,
			type: "POST",
			success: function(data){callback.success(data.space)},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		callback.failure(textStatus);
			},
		});
	};

	/**
	 * Returns a list of spaces
	 * @param Number | String storeProviderId The id of the store provider
	 * @param Object callback
	 * @option Function success(spaces) a handler for an array of spaces
	 * @option Function failure(info) a handler that returns failure info 
	 */
	dc.store.GetSpaces = function(storeProviderId, callback){
		if(callback.begin != undefined){
			callback.begin();
		}

		$.ajax({ url: "/duradmin/spaces", 
				data: "storeId="+storeProviderId+"&f=json",
				cache: false,
				context: document.body, 
				success: function(data){
					callback.success(data.spaces);
			    },
			    error: function(xhr, textStatus, errorThrown){
			    	alert("get spaces failed: " + textStatus);
			    },
		});
		
	};
	
	
	/**
	 * returns contentItem details
	 */
	dc.store.GetContentItem = function(storeProviderId, spaceId, contentItemId, callback){
		if(callback.begin != undefined){
			callback.begin();
		}

		$.ajax({ url: "/duradmin/spaces/content", 
				data: "storeId="+storeProviderId+"&spaceId="+escape(spaceId)+"&contentId="+escape(contentItemId),
				cache: false,
				type: "GET",
				context: document.body, 
				success: function(data){
					callback.success(data.contentItem);
			    },
			    error: function(xhr, textStatus, errorThrown){
			    	alert("get contentItem failed: " + textStatus);
			    },
		});

	};
	
	
	dc.store.AddContentItem = function(storeId,spaceId, form, future){
		$(form).ajaxForm({
			iframe: true,
			dataType: "json",
			success: function(data){
				alert("success!" + data);
		    },
		    error: function(xhr, textStatus, errorThrown){
		    	alert("get contentItem failed: " + textStatus);
		    },
		});

		$(form).ajaxSubmit();
 		
 		var checkProgressURL = "/duradmin/spaces/upload?taskId="+future.key;


		setTimeout(function(){
			dc.checkProgress(checkProgressURL, future.key, future);
		},2000);
		
	};
	
	/**
	 * checks if the content item already exists
	 * @param spaceId
	 * @param contentId
	 * @param storeId 
	 * 
	 */
	dc.store.checkIfContentItemExists = function(spaceId,contentId, storeId, callback){
		//alert("implement check if content item exists!");
		callback.success(true);
	};
		
})();


/**
 * 
 */
var centerLayout, listBrowserLayout, spacesListPane, contentItemListPane,detailPane, spacesManagerToolbar;


$(document).ready(function() {
	centerLayout = $('#page-content').layout({
	//	minWidth:				300	// ALL panes
		north__size: 			50	
	,	north__paneSelector:     ".center-north"
	,   north__resizable:   false
	,   north__slidable:    false
	,   north__spacing_open:			0			
	,	north__togglerLength_open:		0			
	,	north__togglerLength_closed:	0			

	,   west__size:				800
	,	west__minSize:			600
	,   west__paneSelector:     "#list-browser"
	,   west__onresize:         "listBrowserLayout.resizeAll"
	,	center__paneSelector:	"#detail-pane"
	,   center__onresize:       "detailPane.resizeAll"
	});


	listBrowserLayout = $('#list-browser').layout({
	    	west__size:				300
	    ,	west__minSize:			260
		,   west__paneSelector:     "#spaces-list-view"
	//	,   west__onresize:         "spacesListPane.resizeAll"
		,	center__paneSelector:	"#content-item-list-view"
		,   center__onresize:       "contentItemListPane.resizeAll"
	});
	

	var spacesAndContentLayoutOptions = {
			north__paneSelector:	".north"
		,   north__size: 			60
		,	center__paneSelector:	".center"
		,   resizable: 				false
		,   slidable: 				false
		,   spacing_open:			0			
		,	togglerLength_open:		0	
	};
			
	spacesListPane = $('#spaces-list-view').layout(spacesAndContentLayoutOptions);
	contentItemListPane = $("#content-item-list-view").layout(spacesAndContentLayoutOptions);

	//detail pane's layout options
	var spaceDetailLayoutOptions = {
			north__paneSelector:	".north"
				,   north__size: 			200
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
				
	};
	
	//content item detail layout is slightly different from 
	//the space detail - copy and supply overrides
	var contentItemDetailLayoutOptions = $.extend(true,{}, 
													   spaceDetailLayoutOptions, 
													   {north__size:200});
	
	
	detailPane = $('#detail-pane').layout(spaceDetailLayoutOptions);
	
	////////////////////////////////////////////
	//sets contents of object-name class
	///
	var setObjectName = function(pane, name){
		$(".object-name", pane).empty().prepend(name);	
	};
			
	///////////////////////////////////////////
	///check/uncheck all spaces
	$(".dc-check-all").click(
		function(evt){
			var checked = $(evt.target).attr("checked");
			$(evt.target)
				.closest(".dc-list-item-viewer")
				.find(".dc-item-list")
				.selectablelist("select", checked);
	});

	
	var showMultiSpaceDetail = function(){
		var multiSpace = $("#spaceMultiSelectPane").clone();
		loadMetadataPane(multiSpace);
		loadTagPane(multiSpace);
		$("#detail-pane").replaceContents(multiSpace, spaceDetailLayoutOptions);
		$("#content-item-list").selectablelist("clear");
	};

	var showMultiContentItemDetail = function(){
		var multiSpace = $("#contentItemMultiSelectPane").clone();
		loadMetadataPane(multiSpace);
		loadTagPane(multiSpace);
		$("#detail-pane").replaceContents(multiSpace, contentItemDetailLayoutOptions);
	};

	var showGenericDetailPane = function(){
		$("#detail-pane").replaceContents($("#genericDetailPane").clone(), spaceDetailLayoutOptions);
	};

	//////////////////////////////////////////
	////functions for loading metadata, tags and properties

	var loadMetadataPane = function(target, extendedMetadata){
		var viewerPane = $.fn.create("div")
						.metadataviewer({title: "Metadata"})
						.metadataviewer("load",extendedMetadata);

		$(".center", target).append(viewerPane);
		return viewerPane;
	};

	var loadTagPane = function(target, tags){
		var viewerPane = $.fn.create("div")
						.tagsviewer({title: "Tags"})
						.tagsviewer("load",tags);
		$(".center", target).append(viewerPane);
		return viewerPane;
	};
	
	var loadProperties = function(target, /*array*/ properties){
		$(".center", target)
			.append($.fn.create("div")
						.tabularexpandopanel(
								{title: "Details", data: properties}));
	};

	var loadVideo = function(target, contentItem){
		var viewer = $.fn.create("embed")
		.attr("src", contentItem.viewerURL)
		.attr("height", "400px")
		.attr("width", "600px");
		
		var wrapper = $.fn.create("div")
				.addClass("preview-image-wrapper")
				.append(viewer);
		
		var div = $.fn.create("div")
		.expandopanel({title: "Watch"});
		
		$(div).expandopanel("getContent").append(viewer);
		
		$(".center", target).append(div);

	};

	var loadAudio = function(target, contentItem){
		var viewer = $.fn.create("embed")
		.attr("src", contentItem.viewerURL)
		var wrapper = $.fn.create("div")
				.addClass("preview-image-wrapper")
				.append(viewer);
		
		var div = $.fn.create("div")
		.expandopanel({title: "Listen"});
		
		$(div).expandopanel("getContent").append(viewer);
		
		$(".center", target).append(div);
		
	};


	
	var loadPreview = function(target, contentItem){
		var mimetype = contentItem.metadata.mimetype;
		var isExternalViewer =  contentItem.viewerURL.indexOf('djatok') > 0;
		var isImage = (contentItem.metadata.mimetype.indexOf('image') == 0)
		var viewerType = 'iframe';
		var options = {
				'transitionIn'	:	'elastic',
				'transitionOut'	:	'elastic',
				'speedIn'		:	600, 
				'speedOut'		:	200, 
				'overlayShow'	:	false};
		
		if(isExternalViewer || !isImage){
			options['width'] = $(document).width()*0.8;
			options['height'] = $(document).height()*0.8;
			options['type'] = 'iframe';
		}else{
			options['type'] = 'image';
		}

		
		var div = $.fn.create("div")
					  .expandopanel({title: "Preview"});
		
		var thumbnail = $.fn.create("img")
							.attr("src", contentItem.thumbnailURL)
							.addClass("preview-image");
							
		var viewerLink = $.fn.create("a")
							.attr("href", contentItem.viewerURL)
							.append(thumbnail);
		
		var wrapper = $.fn.create("div")
							.addClass("preview-image-wrapper")
							.append(viewerLink);
		
		
		$(div).expandopanel("getContent").append(wrapper);
		
		
		viewerLink.fancybox(options);
		
		$(".center", target).append(div);

	};

	var options = {
			'type'			:   'inline',
			'transitionIn'	:	'elastic',
			'transitionOut'	:	'elastic',
			'speedIn'		:	600, 
			'speedOut'		:	200, 
			'overlayShow'	:	false,
			'content'			:   "Test Content"	
			
	};	
	
	var getStoreName = function(storeId) {
		for(i in storeProviders){
			var store = storeProviders[i];
			if(storeId == store.id){
				return store.label;
			}
		}
		
		return 'no provider found with id = ' + storeId;
	};
	
	var createTaskPanel = function(task) {
		var props = task.properties;
		var percentComplete = parseInt(parseInt(props.bytesRead)/parseInt(props.totalBytes)*100);

		var item = 	$.fn.create("div")
						.addClass("upload-item")
						.append(
							$.fn.create("h2").html(props.contentId)
						).append(
								$.fn.create("h3").html(getStoreName(parseInt(props.storeId)) + " >> " + props.spaceId)
						).append(
								$.fn.create("div").html(parseInt(parseInt(props.bytesRead)/1024)+"KB"+ "/" + parseInt(parseInt(props.totalBytes)/1024) + "KB")
						).append(
							$.fn.create("div").addClass("dc-progressbar").html("&nbsp;")
								.append(
									$.fn.create("div").addClass("dc-progressbar-value"))
						).append(
							$.fn.create("div").addClass("dc-controls")
						);

		//configure progress bar
		item.find(".dc-progressbar-value").css("width", percentComplete+"%").html(percentComplete+"%");			
		
		var actionCell = item.find(".dc-controls");	
		if(props.state == 'running'){
			actionCell.append($.fn.create("button").html("cancel").click(function(){ alert("implement cancel here");}));
		}else{
			actionCell.append($.fn.create("button").html("x").click(function(evt){
				var that = this;
				evt.stopPropagation();
				$.ajax({
					url: "/duradmin/spaces/upload",
					data: "action=remove&taskId="+task.id,
					type:"POST",
					success: function(){
						$(that).closest(".upload-item").fadeOut("slow");
					},
					
					error: function(){
						alert("failed to remove task");
					},
				});	
			}));
		}
		
		return item;
		
	};
	
	
	$("#view-uploads").click(function(){
		$("#upload-viewer").dialog("open");
	});

    $("#upload-viewer").dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		resizable: false,
		height: 250,
		closeOnEscape:true,
		modal: false,
		width:500,
		closable: false,
		buttons: {
			"Close": function(){
				$(this).dialog("close");
			}
		},
		beforeclose: function(event, ui){
			stopPolling = false;
		},
		open: function(event,ui){
		    var stopPolling = false;

			var poller = function(){
				$.ajax({
					cache:false,
					url: "/duradmin/spaces/upload",
					success: function(data){
						if(!stopPolling){
							var upload = $("#upload-viewer");
							upload.empty();
							var list = $.fn.create("div");
							upload.append(list);
							for(i in data.taskList){
								var t = data.taskList[i];
								list.append(createTaskPanel(t));
							}
							
							setTimeout(poller, 5000);
						}
					}
				});
			};
						
			poller();
		},
	});

	///////////////////////////////////////////
	///open add space dialog
	$.fx.speeds._default = 10;


	
	$('#add-space-dialog').dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		resizable: false,
		height: 250,
		closeOnEscape:true,
		modal: true,
		width:500,
		buttons: {
			'Add': function(evt) {
				if($("#add-space-form").valid()){
					var space = {
						storeId: getCurrentProviderStoreId(),
						spaceId: $("#add-space-dialog #spaceId").val(),
						access:  $("#add-space-dialog #access").val(),
					};
					dc.store.AddSpace(
						space,
						{
							begin: function(){
								$("#page-content").glasspane("show", "Adding space...");
							},
							success: function(space){
								$("#page-content").glasspane("hide");
								$("#spaces-list-view #space-filter").val(space.spaceId);
								refreshSpaces(space.storeId);
							},
							failure: function(text){
								$("#add-space-dialog").dialog().glasspane("hide");
								alert("add space failed: " + text);
							},
						}
					);
				
					$("#add-space-dialog").dialog("close");

				}
			},
			Cancel: function(evt) {
				$(this).dialog('close');
			}
		},
		
		close: function() {
	
		},
		
		open: function(e){
			$("#add-space-dialog .access-switch").accessswitch({})
						.bind("turnOn", function(evt, future){
							future.success();
							evt.stopPropagation();
							$("#add-space-dialog #access").val("OPEN");
							
						}).bind("turnOff", function(evt, future){
							future.success();
							evt.stopPropagation();
							$("#add-space-dialog #access").val("CLOSED");
						}).accessswitch("on");
			
			$("#add-space-form").validate({
				rules: {
					spaceId: {
						rangelength: [3,63],
						startswith: true,
						endswith: true,
						spacelower: true,
						notip: true,
						misc: true,
					},
				},
				messages: {
						
				}
			});
			
			$.validator
				.addMethod("startswith", function(value, element) { 
				  return  /^[a-z0-9]/.test(value); 
				}, "A Space ID must begin with a lowercase letter or number");
			$.validator
				.addMethod("endswith", function(value, element) { 
				  return  /[a-z0-9.]$/.test(value); 
				}, "A Space ID must end with a lowercase letter, number, or period");
			$.validator.addMethod("spacelower", function(value,element){return /^[a-z0-9.-]*$/.test(value);}, 
					"Only lowercase letters, numbers, periods, and dashes may be used in a Space ID");
			$.validator.addMethod("notip", function(value,element){return !(/^[0-9]+.[0-9]+.[0-9]+.[0-9]+$/.test(value));}, 
					"A Space ID must not be formatted as an IP address");
			$.validator.addMethod("misc", function(value,element){return !(/^.*([.][-]|[-][.]|[.][.]).*$/.test(value));}, 
					"A Space ID must not contain '..' '-.' or '.-'");

			$("#add-space-form").resetForm();

			
		}
		
	});

	$("#add-space-dialog").dialog().glasspane({});


	$('.add-space-button').live("click",
			function(evt){
				$("#add-space-dialog").dialog("open");
			}
		);
	
	


	var getCurrentSpaceId = function(){
		var currentItem = $("#spaces-list").selectablelist("currentItem");
		var spaceId = currentItem.data.spaceId;
		return spaceId;
	};

	var getCurrentProviderStoreId = function(){
		var provider = $("#provider-select-box").flyoutselect("value");
		return provider.id;
	};


	$('#add-content-item-dialog').dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		height: 250,
		resizable: false,
		closeOnEscape:true,
		modal: true,
		width:500,
		buttons: {
			'Add': function() {
				var that = this;
				if($("#add-content-item-form").valid()){
					var form = $("#add-content-item-form");

					var contentId = $("#contentId", form).val();
					
					var spaceId	  =	getCurrentSpaceId();
					var storeId	  =	getCurrentProviderStoreId();
					var filename = $("#file", form).val();

					$("#spaceId", form).val(spaceId);
					$("#storeId", form).val(storeId);
					
					dc.store.checkIfContentItemExists(
							spaceId, contentId, storeId, 
							{ 
								success: function(exists){
									if(exists){
										if(!confirm("A content ID with this name already exists. Overwrite?")){
											return;
										}
									}
									
									$(that).dialog("close");
									var callback = {
										success: function(){
										
										},
										
										failure: function(){
											alert("an error occurred");
										},
										
										view: function(){
											dc.store.GetContentItem(getCurrentProviderStoreId(),spaceId,contentId,{
												success: loadContentItem
											});

										},
									};
									
	
									var key = escape(storeId+"-"+spaceId+"-"+ contentId);

									var t = $("<div id='"+key+"'><div id='"+key+"-inside'>Sending...</div></div>")
										.toaster({
										sticky: true,
										position:'br',
										show: $.fn.slideIn,
										closable: true,
									});
									
	
							 		
									var renderToaster =  function(data){
										$('#'+key+"-inside",document).empty().append(createTaskPanel(data.task));
							 		};

									var callback = {
											key: key, 
											update: renderToaster,
											
											failure: function(){
												alert("upload failed!");
											},
											
											success: renderToaster,
									};

									
									dc.store.AddContentItem(storeId,spaceId,form, callback);
								},
								
								failure: function(){
									
								}
							});
					
				}
			},
			Cancel: function() {
				$(this).dialog('close');
			}
		},
		close: function() {

		},
		  open: function(e){
			var overwrite = false;
			var that = this;
			$("#add-content-item-form").validate({
				rules: {
					contentItemId: {
						required:true,
						minlength: 1,
						illegalchars: true,
					},
					
					file: {
						required:true,
					}
					
				},
				messages: {
						
				}
			});
			
			$.validator
				.addMethod("illegalchars", function(value, element) { 
				  return  /^[^\\?]*$/.test(value); 
				}, "A Content ID cannot contain  '?' or '\\'");
			
			$("#add-content-item-form").resetForm();
		}
	});
	
	$('#add-space-help-content').expandopanel({
		
	});
	

	$('#edit-content-item-dialog').dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		height: 250,
		resizable: false,
		closeOnEscape:true,
		modal: true,
		width:500,
		buttons: {
			'Save': function() {
				alert("implement edit Save functionality");
				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog('close');
			}
		},
		close: function() {

		},
		  open: function(e){
		}
	});
	
	
	$('.add-content-item-button').live("click",
			function(evt){
				$("#add-content-item-dialog").dialog("open");
			});
	
	$('.edit-content-item-button').live("click",
			function(evt){
				$("#edit-content-item-dialog").dialog("open");
			});

	
	
	
	/////////////////////////////////////////////////////////////
	//Spaces / Content Ajax calls
	/////////////////////////////////////////////////////////////

	/**
	 * loads the space data into the detail pane
	 */
	var loadSpace = function(space){
		var detail = $("#spaceDetailPane").clone();
		setObjectName(detail, space.spaceId);
		
		//attach delete button listener
		$(".delete-space-button",detail).click(function(evt){
			
			dc.store.DeleteSpace(space, {
				begin: function(){
					$("#page-content").glasspane("show", "Deleting space...");
				},
				
				success:function(){
					$("#page-content").glasspane("hide");

					$("#spaces-list").selectablelist("removeById", space.spaceId);
				},
				
				failure: function(message){
					$("#page-content").glasspane("hide");
					alert("failed to delete space!");
				},
			});
		});
		
		
		
		//create access switch and bind on/off listeners
		$(".access-switch", detail).accessswitch({
				initialState: (space.metadata.access=="OPEN"?"on":"off")
			}).bind("turnOn", function(evt, future){
				toggleSpaceAccess(space, future);
			}).bind("turnOff", function(evt, future){
				toggleSpaceAccess(space, future);
			});

		
		
		
		loadProperties(detail, extractSpaceProperties(space));

		var mp = loadMetadataPane(detail, space.extendedMetadata);
		
		$(mp).bind("add", function(evt, future){
				var value = future.value;
				addSpaceMetadata(space.spaceId, value.name, value.value, future);
			}).bind("remove", function(evt, future){
				removeSpaceMetadata(space.spaceId, future.value.name,future);
			});
		
		var tag = loadTagPane(detail, space.metadata.tags);

		$(tag).bind("add", function(evt, future){
			var value = future.value[0];
			addSpaceTag(space.spaceId, value, future);
		}).bind("remove", function(evt, future){
			var value = future.value[0];
			removeSpaceTag(space.spaceId, value, future);
		});

		$("#detail-pane").replaceContents(detail, spaceDetailLayoutOptions);

		loadContentItems(space.contents);
		
	};

	
	var extractSpaceProperties = function(space){
		return [ 
					['Items', space.metadata.count],
					['Created', space.metadata.created],
			   ];
	};

	var extractContentItemProperties = function(contentItem){
		var m = contentItem.metadata;
		return [
			        ["Space", contentItem.spaceId],
			        ["Size", m.size],
			        ["Created", m.created],
			        ["Checksum", m.checksum],
		       ];
	};

	var isPdf = function(mimetype){
		return(mimetype.toLowerCase().indexOf("pdf") > -1);
	};
	
	var loadContentItem = function(contentItem){
		var pane = $("#contentItemDetailPane").clone();
		setObjectName(pane, contentItem.contentId);
		
		var mimetype = contentItem.metadata.mimetype;
		
		if(mimetype.indexOf("image") == 0 || mimetype.indexOf("text") == 0 || isPdf(mimetype)){
			loadPreview(pane, contentItem);
		}else if(mimetype.indexOf("video") == 0){
			loadVideo(pane, contentItem);
		}else if(mimetype.indexOf("audio") == 0){
			loadAudio(pane, contentItem);
		}
		
		loadProperties(pane, extractContentItemProperties(contentItem));
		//load the details panel
		var mimetype = contentItem.metadata.mimetype;
		$(".mime-type .value", pane).text(mimetype);
		$(".mime-type", pane).addClass(dc.getMimetypeImageClass(mimetype));

		var mp = loadMetadataPane(pane, contentItem.extendedMetadata);
		
		
		$(mp).bind("add", function(evt, future){
				var value = future.value;
				addContentItemMetadata(contentItem.spaceId, contentItem.contentId, value.name, value.value, future);
			}).bind("remove", function(evt, future){
				removeContentItemMetadata(contentItem.spaceId, contentItem.contentId, future.value.name,future);
			});
		
		var tag = loadTagPane(pane, contentItem.metadata.tags);

		$(tag).bind("add", function(evt, future){
			var value = future.value[0];
			addContentItemTag(contentItem.spaceId, contentItem.contentId, value, future);
		}).bind("remove", function(evt, future){
			var value = future.value[0];
			removeContentItemTag(contentItem.spaceId, contentItem.contentId, value, future);
		});

		
		$("#detail-pane").replaceContents(pane,contentItemDetailLayoutOptions);
	};

	var contentItemListStatusId = "#content-item-list-status";
	
	var getSpace = function(spaceId, loadHandler){
		clearContents();
		$("#detail-pane").fadeOut("slow");
		var prefix = $("#content-item-filter").val();
		if(prefix == DEFAULT_FILTER_TEXT){
			prefix = null;
		}
		
		dc.store.GetSpace(
				getCurrentProviderStoreId(),
				spaceId, 
				{
					begin: function(){
						$("#page-content").glasspane("show");
						$(contentItemListStatusId).html("Loading...").fadeIn("slow");
					},
					success: function(space){
						$("#page-content").glasspane("hide");

						loadHandler(space);
						$(contentItemListStatusId).fadeOut("fast");
					}, 
					failure:function(info){
						$("#page-content").glasspane("hide");
						alert("Get Space failed: " + info);
					},
				},
				{
					prefix: prefix,
				});
	};
	
	$("#content-item-list-view").find(".dc-item-list-filter").bind("keyup", $.debounce(500, function(evt){
		var spaceId = getCurrentSpaceId();
		getSpace(spaceId, function(space){loadContentItems(space.contents)});
	}));

	var loadContentItems = function(contentItems){
		$("#content-item-list").selectablelist("clear");
		
		for(i in contentItems){
			var ci = contentItems[i];
			var node =  document.createElement("div");
			var actions = document.createElement("div");
			$(actions).append("<button class='delete-space-button'>-</button>");
			$(node).attr("id", ci)
				   .html(ci)
				   .append(actions);
			$("#content-item-list").selectablelist('addItem',node);	   

		}
	}
	
	
	var toggleSpaceAccess = function(space, callback){
		var access = space.metadata.access;
		var newAccess = (access == "OPEN") ? "CLOSED":"OPEN";
		$("#page-content").glasspane("show", "Changing space access..."); 
		$.ajax({ url: "/duradmin/spaces/space?storeId="+space.storeId+"&spaceId="+escape(space.spaceId), 
			data: "access="+newAccess+"&action=put&method=changeAccess",
			type: "POST",
			cache: false,
			context: document.body, 
			success: function(data){
				$("#page-content").glasspane("hide");
				callback.success(data.space);
			},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
				$("#page-content").glasspane("hide");
	    		callback.failure(textStatus);
		    },
		});		
	};

	var createSpaceMetadataCall = function(spaceId, data, method,callback){
		var newData = data + "&method=" + method;
		var storeId = getCurrentProviderStoreId();
		return {
			url: "/duradmin/spaces/space?storeId="+storeId+"&spaceId="+escape(spaceId) +"&action=put", 
			type: "POST",
			data: newData,
			cache: false,
			context: document.body, 
			success: function(data){
				callback.success();
			},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		callback.failure(textStatus);
		    },
		};
	};
	

	/////////////////////////////////////////////////////////////////////////////////
	///space metadata functions
	var createSpaceMetadataCall = function(spaceId, data, method,callback){
		var newData = data + "&method=" + method;
		var storeId = getCurrentProviderStoreId();
		return {
			url: "/duradmin/spaces/space?storeId="+storeId+"&spaceId="+escape(spaceId) +"&action=put", 
			type: "POST",
			data: newData,
			cache: false,
			context: document.body, 
			success: function(data){
				callback.success();
			},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		callback.failure(textStatus);
		    },
		};
	};
	
	var addSpaceMetadata = function(spaceId, name, value, callback){
		var data = "metadata-name=" + escape(name) +"&metadata-value="+escape(value);
		$.ajax(createSpaceMetadataCall(spaceId, data, "addMetadata", callback));		
	};

	var removeSpaceMetadata = function(spaceId, name,callback){
		var data = "metadata-name=" + escape(name);
		$.ajax(createSpaceMetadataCall(spaceId, data, "removeMetadata", callback));		
	};

	var addSpaceTag = function(spaceId, tag, callback){
		var data = "tag="+ escape(tag);
		$.ajax(createSpaceMetadataCall(spaceId, data, "addTag", callback));		
	};

	var removeSpaceTag = function(spaceId, tag,callback){
		var data = "tag="+escape(tag);
		$.ajax(createSpaceMetadataCall(spaceId, data, "removeTag", callback));		
	};

	/////////////////////////////////////////////////////////////////////////////////
	///content metadata functions
	var createContentItemMetadataCall = function(spaceId, contentId, data, method,callback){
		var newData = data + "&method=" + method;
		var storeId = getCurrentProviderStoreId();
		return {
			url: "/duradmin/spaces/content?storeId="+storeId+"&spaceId="+escape(spaceId) +"&contentId="+escape(contentId) +"&action=put", 
			type: "POST",
			data: newData,
			cache: false,
			context: document.body, 
			success: function(data){
				callback.success();
			},
		    error: function(xhr, textStatus, errorThrown){
	    		console.error("get spaces failed: " + textStatus + ", error: " + errorThrown);
	    		callback.failure(textStatus);
		    },
		};
	};
	
	var addContentItemMetadata = function(spaceId, contentId, name, value, callback){
		var data = "metadata-name=" + escape(name) +"&metadata-value="+escape(value);
		$.ajax(createContentItemMetadataCall(spaceId, contentId, data, "addMetadata", callback));		
	};

	var removeContentItemMetadata = function(spaceId, contentId, name,callback){
		var data = "metadata-name=" + escape(name);
		$.ajax(createContentItemMetadataCall(spaceId,contentId, data, "removeMetadata", callback));		
	};

	var addContentItemTag = function(spaceId, contentId, tag, callback){
		var data = "tag="+ escape(tag);
		$.ajax(createContentItemMetadataCall(spaceId,contentId, data, "addTag", callback));		
	};

	var removeContentItemTag = function(spaceId, contentId, tag,callback){
		var data = "tag="+escape(tag);
		$.ajax(createContentItemMetadataCall(spaceId, contentId, data, "removeTag", callback));		
	};
	

	$("#content-item-list").selectablelist({});
	$("#spaces-list").selectablelist({});

	var DEFAULT_FILTER_TEXT = "filter";

	$(".dc-item-list-filter").focus(function(){
		if($(this).val() == DEFAULT_FILTER_TEXT){
			$(this).val('');
		};
	}).blur(function(){
		if($(this).val() == ""){
			$(this).val(DEFAULT_FILTER_TEXT);
		};
	}).val(DEFAULT_FILTER_TEXT);
	
	///////////////////////////////////////////
	///click on a space list item

	$("#spaces-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			if(state.item !=null && state.item != undefined){
				getSpace($(state.item).attr("id"), loadSpace);
			}else{
				showGenericDetailPane();
			}
		}else{
			showMultiSpaceDetail();
		}
	});

	$("#spaces-list").bind("selectionChanged", function(evt,state){
		if(state.selectedItems.length == 0){
			showGenericDetailPane();
		}else if(state.selectedItems.length == 1){
			getSpace($(state.item).attr("id"),loadSpace);
		}else{
			showMultiSpaceDetail();
		}
	});


	$("#spaces-list").bind("itemRemoved", function(evt,state){
		clearContents();
		showGenericDetailPane();
	});
	///////////////////////////////////////////
	///click on a content list item
	$("#content-item-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			/**
			 * @FIXME 
			 */
			var spaceId = getCurrentSpaceId();

			dc.store.GetContentItem(getCurrentProviderStoreId(),spaceId,$(state.item).attr("id"),{
				begin: function(){
					$("#page-content").glasspane("show");
				},
				success: function(contentItem) {
					$("#page-content").glasspane("hide");
					loadContentItem(contentItem);
				}
			});
		}else{
			showMultiContentItemDetail();
		}
	});

	
	
	$("#content-item-list").bind("selectionChanged", function(evt,state){
		if(state.selectedItems.length == 0){
			showGenericDetailPane();
		}else if(state.selectedItems.length == 1){
			var spaceId = "YYYYYYY";
			/**
			 * @FIXME 
			 */
			dc.store.GetContentItem(getCurrentProviderStoreId(),spaceId,$(state.item).attr("id"),{
				success: loadContentItem
			});
		}else{
			showMultiContentItemDetail();
		}
	});

	///////////////////////////////////////////
	///click on a space list item
	var spacesArray = new Array();

	
	$("#spaces-list-view").find(".dc-item-list-filter").bind("keyup", $.debounce(500,function(evt){
			loadSpaces(spacesArray, evt.target.value);
	}));

	var clearContents = function(){
		$("#content-item-list").selectablelist("clear");
	};

	var loadSpaces = function(spaces,filter) {
		$("#spaces-list").selectablelist("clear");
		
		var firstMatchFound = false;
		for(s in spaces){
			var space = spaces[s];
			if(filter === undefined || filter == DEFAULT_FILTER_TEXT || space.spaceId.toLowerCase().indexOf(filter.toLowerCase()) > -1){
				var node =  $.fn.create("div");
				var actions = $.fn.create("div");
				actions.append("<button class='delete-space-button'>-</button>");
				node.attr("id", space.spaceId)
					   .html(space.spaceId)
					   .append(actions)
					   ;
				
				$("#spaces-list").selectablelist('addItem',node,space);	   
				if(!firstMatchFound){
					$("#spaces-list").selectablelist('setCurrentItemById',node.attr("id"));	   
					firstMatchFound = true;
				}
			}
			

		}
		
		
	};
	
	
	

	
	var refreshSpaces = function(providerId){
		clearContents();
		dc.store.GetSpaces(providerId,{
			begin: function(){
				$("#page-content").glasspane("show");
				$("#space-list-status").html("Loading...").fadeIn("slow");
			},
			success: function(spaces){
				$("#page-content").glasspane("hide");

				spacesArray = new Array();
				for(s in spaces){
					spacesArray[s] = {spaceId: spaces[s]};
				}
				//clear content filters
				$("#content-item-filter").val(DEFAULT_FILTER_TEXT);
				loadSpaces(spacesArray, $("#space-filter").val());
				$("#space-list-status").fadeOut("fast");

			},
			failure: function(xhr, message){
				$("#page-content").glasspane("hide");

				alert("error:" + message);
				$("#space-list-status").fadeOut("fast");
			}
			
		});
	};

	var PROVIDER_SELECT_ID = "provider-select-box";
	var initSpacesManager =  function(){
		////////////////////////////////////////////
		// initialize provider selection
		///
		var PROVIDER_COOKIE_ID = "providerId";
		var options = {
			data: storeProviders, //this variable is defined in a script in the head of spaces-manager.jsp
			selectedIndex: 0
		};

		var currentProviderId = options.data[options.selectedIndex].id;
		var cookie = $.fn.cookie(PROVIDER_COOKIE_ID);
		
		if(cookie != undefined){
			for(i in options.data)
			{
				var pid = options.data[i].id;
				if(pid == cookie){
					options.selectedIndex = i;
					currentProviderId = pid; 
					break;
				}
			}
		}

		$("#"+PROVIDER_SELECT_ID).flyoutselect(options).bind("changed",function(evt,state){
			$.fn.cookie(PROVIDER_COOKIE_ID, state.value.id);
			console.debug("value changed: new value=" + state.value.label);
			refreshSpaces(state.value.id);
		});		
		
		refreshSpaces(currentProviderId);
	};
	
	
	//$("#spaces-list-view").glasspane({});
	//$("#content-item-list-view").glasspane({});
	//$("#detail-pane").glasspane({});
	$("#page-content").glasspane({});

	
	
	initSpacesManager();

	//hides the title bar on all dialogs;
	$(".ui-dialog-titlebar").hide();

});