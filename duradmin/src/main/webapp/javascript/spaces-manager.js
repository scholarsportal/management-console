/**
 * 
 * created by Daniel Bernstein
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
				,   north__size: 			175
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
	};

	var contentItemDetailLayoutOptions = {
			north__paneSelector:	".north"
				,   north__size: 			200
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
	};

	
	detailPane = $('#detail-pane').layout(spaceDetailLayoutOptions);

	
	////////////////////////////
	//this method loads the children of the source
	//into the target after emptying the contents
	//with a fade in / fade out effect
	var swapDetailPane = function(source, target, layoutOptions){
		$(target).fadeOut("fast", function(){
			$(target).empty().prepend($(source).children());
			$(target).fadeIn("fast");
			$(target).layout(layoutOptions);
		});
		return $(target);
	};		

	
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
		swapDetailPane(multiSpace,"#detail-pane", spaceDetailLayoutOptions);
		$("#content-item-list").selectablelist("clear");
	};

	var showMultiContentItemDetail = function(){
		var multiSpace = $("#contentItemMultiSelectPane").clone();
		loadMetadataPane(multiSpace);
		loadTagPane(multiSpace);
		swapDetailPane(multiSpace,"#detail-pane", contentItemDetailLayoutOptions);
	};

	var showGenericDetailPane = function(){
		swapDetailPane($("#genericDetailPane").clone(),"#detail-pane", spaceDetailLayoutOptions);
	};

	//////////////////////////////////////////
	////functions for loading metadata and tags
	var appendCopy = function(target, source){
		var copy = $(source).clone();
		$(copy).removeAttr("id").show();
		$(".center", target).append(copy);
		return copy;
	};
	
	var loadMetadataPane = function(target){
		return appendCopy(target, "#metadata-panel");
	};

	var loadTagPane = function(target){
		return appendCopy(target, "#tag-panel");
	};
	
	
	///////////////////////////////////////////
	///open add space dialog
	$.fx.speeds._default = 10;
	$('#add-space-dialog').dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		height: 300,
		closeOnEscape:true,
		modal: false,
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

		}
	});



	$('.add-space-button').click(
			function(evt){
				dcOpenDialogOverTarget(evt,"#add-space-dialog");
			}
		);
	

	$('#add-content-item-dialog').dialog({
		autoOpen: false,
		show: 'blind',
		hide: 'blind',
		height: 300,
		closeOnEscape:true,
		modal: false,
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

		}
	});
	//hides the title bar on all dialogs;
	
	$(".ui-dialog-titlebar").hide();
	
	$('.add-content-item-button').live("click",
			function(evt){
					dcOpenDialogOverTarget(evt,"#add-content-item-dialog");
			});

	
	
	/////////////////////////////////////////////////////////////
	//Spaces / Content Ajax calls
	/////////////////////////////////////////////////////////////
	
	var loadSpace = function(space){
		
		var spaceDetailPane = $("#spaceDetailPane").clone();
		var contentItems = space.contentItems;
		setObjectName(spaceDetailPane, space.spaceId);
		loadMetadataPane(spaceDetailPane);
		loadTagPane(spaceDetailPane);
		swapDetailPane(spaceDetailPane,"#detail-pane", spaceDetailLayoutOptions);
		
		loadContentItems(contentItems);
		
	};
	
	var dcGetContentItem = function(contentItemId, spaceId, callback){
		var contentItem = {contentId: contentItemId, spaceId: spaceId};
		callback.load(contentItem);
	};

	var loadContentItem = function(contentItem){
		var pane = $("#contentItemDetailPane").clone();
		setObjectName(pane, contentItem.contentId);
		loadMetadataPane(pane);
		loadTagPane(pane);
		swapDetailPane(pane, "#detail-pane",contentItemDetailLayoutOptions);
	};

	
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
	
	var dcGetSpaces = function(callback){
		var spaces = new Array(50);
		for(var i = 0; i < spaces.length; i++){
			spaces[i] = {spaceId:"Space-" + i};
		}
		callback.load(spaces);
	};
	
	var dcGetSpace = function(spaceId,callback){
		var contentItems = new Array(50);
		for(var i = 0; i < contentItems.length; i++){
			contentItems[i] = spaceId+"/this/is/faux/content/item/" + i;
		}
		
		
		callback.load({
						spaceId: spaceId,
						contentItems: contentItems,
						createdOn: "Jan 1, 2010 12:00:00 GMT",
						itemCount: 50
					  });
	};


	$("#content-item-list").selectablelist({});
	$("#spaces-list").selectablelist({});

	
	///////////////////////////////////////////
	///click on a space list item

	$("#spaces-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			dcGetSpace($(state.item).attr("id"),{
				load: loadSpace
			});
		}else{
			showMultiSpaceDetail();
		}
	});

	$("#spaces-list").bind("selectionChanged", function(evt,state){
		if(state.selectedItems.length == 0){
			showGenericDetailPane();
		}else if(state.selectedItems.length == 1){
			dcGetSpace($(state.item).attr("id"),{
				load: loadSpace
			});
		}else{
			showMultiSpaceDetail();
		}
	});

	///////////////////////////////////////////
	///click on a content list item
	$("#content-item-list").bind("currentItemChanged", function(evt,state){
		if(state.selectedItems.length < 2){
			/**
			 * @FIXME 
			 */
			var spaceId = "XXXXXX";
			dcGetContentItem($(state.item).attr("id"),spaceId,{
				load: loadContentItem
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
			dcGetContentItem($(state.item).attr("id"),spaceId,{
				load: loadContentItem
			});
		}else{
			showMultiContentItemDetail();
		}
	});

	///////////////////////////////////////////
	///click on a space list item
	var spacesArray = new Array();
	var spacesIdArray = new Array();

	
	$(".dc-item-list-filter").bind("keyup", $.throttle(1200, true, function(evt){
			loadSpaces(spacesArray, evt.target.value);
		}));

	var loadSpaces = function(spaces,filter) {
		$("#spaces-list").selectablelist("clear");
			
		for(s in spaces){
			var space = spaces[s];
			if(filter === undefined || space.spaceId.toLowerCase().indexOf(filter.toLowerCase()) > -1){
				var node =  document.createElement("div");
				var actions = document.createElement("div");
				$(actions).append("<button class='delete-space-button'>-</button>");
				$(node).attr("id", space.spaceId)
					   .html(space.spaceId)
					   .append(actions);
				
				$("#spaces-list").selectablelist('addItem',node);	   
			}
			
			if(s == 0){
				loadSpace(spaces[0]);
			}		

		}
		
		
	};
	
	dcGetSpaces({
		load: function(spaces){
			spacesArray = spaces;
			spacesIdArray = new Array();
			for(s in spacesArray){
				spacesIdArray[s] = spacesArray[s].spaceId;
			}
			loadSpaces(spacesArray);

		}
	});
	
	
});