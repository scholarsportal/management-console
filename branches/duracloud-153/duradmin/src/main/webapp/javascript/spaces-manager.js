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

	 dc.store.GetSpace = function(spaceId,callback,options){
		 
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

		var contentItems = new Array();
		
		for(var i = 0; i < 100; i++){
			var contentId = spaceId+"/this/is/faux/content/item/" + i;
			if(prefix == null || contentId.toLowerCase().indexOf(prefix.toLowerCase()) == 0){
				contentItems.push(contentId);
			}
			

		}
		
		dc.showLoading();

		callback.success({
						spaceId: spaceId,
						access: 'OPEN',
						contentItems: contentItems,
						metadata: {
							count: 10, 
							created: "Jan 1, 2010 12:00:00 GMT",
							tags: ["tag1", "tag2", "tag3", "tag4"],
						},

						extendedMetadata: [
						                   {name: "name1", value: "value1"},
						                   {name: "name2", value: "value2"},
						                   {name: "name3", value: "value3"},
						                   {name: "name4", value: "value4"},
						                   ],						
					  });
		
		//dc.hideLoading();
		//t.toaster("close");
	};

	/**
	 * Returns a list of spaces
	 * @param Number | String storeProviderId The id of the store provider
	 * @param Object callback
	 * @option Function success(spaces) a handler for an array of spaces
	 * @option Function failure(info) a handler that returns failure info 
	 */
	dc.store.GetSpaces = function(storeProviderId, callback){
		var spaces = new Array(50);
		for(var i = 0; i < spaces.length; i++){
			spaces[i] = {spaceId:"Space-" + i + "-from-provider"+storeProviderId};
		}
		callback.success(spaces);
	};
	
	
	/**
	 * returns contentItem details
	 */
	dc.store.GetContentItem = function(contentItemId, spaceId, callback){
		var contentItem = {
				contentId: contentItemId, 
				spaceId:   spaceId, 
				thumbnailURL: "http://farm5.static.flickr.com/4024/4605414261_db2e6d8cbe.jpg",
				viewerURL: "http://farm5.static.flickr.com/4024/4605414261_db2e6d8cbe_b.jpg",
				metadata:  {
					size: "1234567", 
					created: "Jun 10 2009 12:00:00",
					checksum: "deadbeafdeadbeefdeadbeef",
					mimetype: "text/plain",
					tags: ["tag1", "tag2", "tag3", "tag4"],
					},
				extendedMetadata: [
				                   {name: "name1", value: "value1"},
				                   {name: "name2", value: "value2"},
				                   {name: "name3", value: "value3"},
				                   {name: "name4", value: "value4"},
				                   ],
			};
		callback.success(contentItem);
	};
	
})();


/**
 * 
 */
var centerLayout, listBrowserLayout, spacesListPane, contentItemListPane,detailPane, spacesManagerToolbar;



$(document).ready(function() {
	centerLayout = $('#page-content').layout({
		//minSize:				50	// ALL panes
		north__size: 			50	
	,	north__paneSelector:     ".center-north"
	,   north__resizable:   false
	,   north__slidable:    false
	,   north__spacing_open:			0			
	,	north__togglerLength_open:		0			
	,	north__togglerLength_closed:	0			

	,   west__size:				800
	,   west__paneSelector:     "#list-browser"
	,   west__onresize:         "listBrowserLayout.resizeAll"
	,	center__paneSelector:	"#detail-pane"
	,   center__onresize:       "detailPane.resizeAll"
	});


	listBrowserLayout = $('#list-browser').layout({
	    	west__size:				400
		,   west__paneSelector:     "#spaces-list-view"
		,   west__onresize:         "spacesListPane.resizeAll"
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

	var loadPreview = function(target, contentItem){
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

	

		/* This is basic - uses default settings */
		
		viewerLink.fancybox({
				'transitionIn'	:	'elastic',
				'transitionOut'	:	'elastic',
				'speedIn'		:	600, 
				'speedOut'		:	200, 
				'overlayShow'	:	false});
		
		$(".center", target).append(div);

	};
	
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
					alert("Adding new space ajax impl goes here");
					$(this).dialog("close");
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
					spacename: {
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
			$.validator.addMethod("misc", function(value,element){return !(/^.*([.][.-]|[-][.]).*$/.test(value));}, 
					"A Space ID must not contain '..' '-.' or '.-'");

			
			
		}
		
	});



	$('.add-space-button').live("click",
			function(evt){
				$("#add-space-dialog").dialog("open");
			}
		);
	

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
				alert("implement add functionality");
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
	
	//hides the title bar on all dialogs;
	
	$(".ui-dialog-titlebar").hide();
	
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
		var contentItems = space.contentItems;
		setObjectName(detail, space.spaceId);
		
		//attach delete button listener
		$(".delete-space-button",detail).click(function(evt){
			deleteSpace(space, {
				success:function(){
					$("#spaces-list").selectablelist("removeById", space.spaceId);
				},
				
				failure: function(message){
					//do failure messaging here
				},
			});
		});
		
		
		
		//create access switch and bind on/off listeners
		$(".access-switch", detail).accessswitch({
				initialState: (space.access=="OPEN"?"on":"off")
			}).bind("turnOn", function(evt, future){
				toggleSpaceAccess(space, future);
			}).bind("turnOff", function(evt, future){
				toggleSpaceAccess(space, future);
			});

		
		
		
		loadProperties(detail, extractSpaceProperties(space));

		var mp = loadMetadataPane(detail, space.extendedMetadata);
		
		$(mp).bind("add", function(evt, future){
				future.success();
			}).bind("remove", function(evt, future){
				future.success();
			});
		
		var tag = loadTagPane(detail, space.metadata.tags);

		$(tag).bind("add", function(evt, future){
			future.success();
		}).bind("remove", function(evt, future){
			future.success();
		});

		$("#detail-pane").replaceContents(detail, spaceDetailLayoutOptions);

		loadContentItems(contentItems);
		
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

	var loadContentItem = function(contentItem){
		var pane = $("#contentItemDetailPane").clone();
		setObjectName(pane, contentItem.contentId);
		loadPreview(pane, contentItem);
		loadProperties(pane, extractContentItemProperties(contentItem));
		//load the details panel
		var mimetype = contentItem.metadata.mimetype;
		$(".mime-type .value", pane).text(mimetype);
		$(".mime-type", pane).addClass(dc.getMimetypeImageClass(mimetype));

		var mp = loadMetadataPane(pane, contentItem.extendedMetadata);
		
		$(mp).bind("add", function(evt, future){
			future.success();
		});

		$(mp).bind("remove", function(evt, future){
			future.success();
		});
		
		var tag = loadTagPane(pane, contentItem.metadata.tags);

		$(tag).bind("add", function(evt, future){
			future.success();
		});

		$(tag).bind("remove", function(evt, future){
			future.success();
		});

		
		$("#detail-pane").replaceContents(pane,contentItemDetailLayoutOptions);
	};


	
	$("#content-item-list-view").find(".dc-item-list-filter").bind("keyup", $.debounce(250, false, function(evt){
		var currentItem = $("#spaces-list").selectablelist("getCurrentItem");
		var spaceId = currentItem.data.spaceId;
		dc.store.GetSpace(
				spaceId, 
				{
					success: function(space){
						loadContentItems(space.contentItems);
					}, 
					failure:function(info){
						alert("onkeyup failure not handled: " + info);
					},
			
				},
				{prefix: evt.target.value});
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
		var access = space.access;
		var newAccess = (access == "OPEN") ? "CLOSED":"OPEN";
		
		var success = true;//DO AJAX COMMAND HERE
		alert("ajax call not yet implemented!");
		//update space object
		space.access = newAccess;
		success ? callback.success() : callback.failure();
	};

	



	$("#content-item-list").selectablelist({});
	$("#spaces-list").selectablelist({});
	
	$(".dc-item-list-filter").focus(function(){
		if($(this).val() == "filter"){
			$(this).val('');
		};
	}).blur(function(){
		if($(this).val() == ""){
			$(this).val('filter');
		};
	});
	
	///////////////////////////////////////////
	///click on a space list item

	$("#spaces-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			if(state.item !=null && state.item != undefined){
				dc.store.GetSpace($(state.item).attr("id"),{
					success: loadSpace
				});
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
			dc.store.GetSpace($(state.item).attr("id"),{
				success: loadSpace
			});
		}else{
			showMultiSpaceDetail();
		}
	});


	$("#spaces-list").bind("itemRemoved", function(evt,state){
		refreshSpaces();
	});
	///////////////////////////////////////////
	///click on a content list item
	$("#content-item-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			/**
			 * @FIXME 
			 */
			var spaceId = "XXXXXX";
			dc.store.GetContentItem($(state.item).attr("id"),spaceId,{
				success: loadContentItem
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
			dc.store.GetContentItem($(state.item).attr("id"),spaceId,{
				success: loadContentItem
			});
		}else{
			showMultiContentItemDetail();
		}
	});

	///////////////////////////////////////////
	///click on a space list item
	var spacesArray = new Array();
	var spacesIdArray = new Array();

	
	$("#spaces-list-view").find(".dc-item-list-filter").bind("keyup", $.debounce(250, false, function(evt){
			loadSpaces(spacesArray, evt.target.value);
	}));

	var loadSpaces = function(spaces,filter) {
		$("#spaces-list").selectablelist("clear");
		
		var firstMatchFound = false;
		for(s in spaces){
			var space = spaces[s];
			if(filter === undefined || space.spaceId.toLowerCase().indexOf(filter.toLowerCase()) > -1){
				var node =  $.fn.create("div");
				var actions = $.fn.create("div");
				actions.append("<button class='delete-space-button'>-</button>");
				node.attr("id", space.spaceId)
					   .html(space.spaceId)
					   .append(actions);
				
				$("#spaces-list").selectablelist('addItem',node,space);	   
				if(!firstMatchFound){
					$("#spaces-list").selectablelist('setCurrentItemById',node.attr("id"));	   
					firstMatchFound = true;
				}
			}
			

		}
		
		
	};
	
	var deleteSpace = function(space, callback){
		alert("ajax call to delete space" + space.spaceId + "here");
		var success = true; //ajax call returns true if okay
		success ? callback.success() : callback.failure("Failed message here");
	};
	
	

	
	var refreshSpaces = function(providerId){
		dc.store.GetSpaces(providerId,{
			success: function(spaces){
				spacesArray = spaces;
				spacesIdArray = new Array();
				for(s in spacesArray){
					spacesIdArray[s] = spacesArray[s].spaceId;
				}
				//clear filters
				$(".dc-item-list-filter").val('');
				
				loadSpaces(spacesArray);
	
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
			data: [
 			       {id:"1", label:"Amazon S3"},
			       {id:"2", label:"Rackspace"},
			       {id:"3", label:"EMC with a very long name"}],
			selectedIndex: 0
		};

		var currentProviderId = options.data[options.selectedIndex].id;
		var cookie = $.cookie(PROVIDER_COOKIE_ID);
		
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
			$.cookie(PROVIDER_COOKIE_ID, state.value.id);
			console.debug("value changed: new value=" + state.value.label);
			refreshSpaces(state.value.id);
		});		
		
		refreshSpaces(currentProviderId);
	};
	
	
	
	initSpacesManager();
});