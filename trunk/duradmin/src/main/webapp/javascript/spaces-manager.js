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
	$("#check-all-spaces").click(
		function(evt){
			var checked = $(evt.target).attr("checked");
			$("#spaces-list input[type=checkbox]").attr("checked", checked);
			dcStyleItem($("#spaces-list .dc-item").first());
			if(checked){
				showMultiSpaceDetail();
			}else{
				showGenericDetailPane();
			}
		});

	$("#check-all-content-items").click(
			function(evt){
				var checked = $(evt.target).attr("checked");
				$("#content-item-list input[type=checkbox]").attr("checked", checked);
				dcStyleItem($("#content-item-list .dc-item").first());
				if(checked){
					showMultiContentItemDetail();
				}else{
					showGenericDetailPane();
				}
			});
	
	var showMultiSpaceDetail = function(){
		var multiSpace = $("#spaceMultiSelectPane").clone();
		loadMetadataPane(multiSpace);
		loadTagPane(multiSpace);
		swapDetailPane(multiSpace,"#detail-pane", spaceDetailLayoutOptions);
		$("#content-item-list > .dc-item").each(function(element){
			if(!$(element).hasClass("dc-prototype")){
				$(element).remove();
			};
			
		});
	};

	var showMultiContentItemDetail = function(){
		var multiSpace = $("#contentItemMultiSelectPane").clone();
		loadMetadataPane(multiSpace);
		loadTagPane(multiSpace);
		swapDetailPane(multiSpace,"#detail-pane", contentItemDetailLayoutOptions);
	};

	var showGenericDetailPane = function(){
		swapDetailPane("#genericDetailPane","#detail-pane", spaceDetailLayoutOptions);
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
	///click on a space list item
	$("#spaces-list .dc-item").live("click",
		function(evt){
			//if multiple spaces are selected display
			//spaceMultiselect Panel
			var multiChecked = $("#spaces-list").find("input[type=checkbox][checked]").size() > 1;
			//deselect everything in the content window
		
			if(multiChecked){
				showMultiSpaceDetail();
			}else{
				$("#content-item-list > .dc-item").removeClass("dc-selected-list-item dc-checked-list-item dc-checked-selected-list-item")
				$("#content-item-list > .dc-item").find("input[type][checked]").attr("checked", false);
				var spaceId = $(dcNearestDcItem(evt.target)).attr("id");
				
				dcGetSpace(spaceId,{
					load: loadSpace
				});
			}
		
		
		}
	);

	///////////////////////////////////////////
	///click on a content list item
	$("#content-item-list .dc-item").live("click",
		function(evt){
			var multiChecked = $("#content-item-list").find("input[type=checkbox][checked]").size() > 1;
			if(multiChecked){
				showMultiContentItemDetail();
			}else{
				var pane = $("#contentItemDetailPane").clone();
				setObjectName(pane, "Content Item Name");
				loadMetadataPane(pane);
				loadTagPane(pane);
				swapDetailPane(pane, "#detail-pane",contentItemDetailLayoutOptions);
			}
		}
	);
	
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
		loadContentItems(contentItems);
		setObjectName(spaceDetailPane, space.spaceId);
		loadMetadataPane(spaceDetailPane);
		loadTagPane(spaceDetailPane);
		swapDetailPane(spaceDetailPane,"#detail-pane", spaceDetailLayoutOptions);
		
	};
	

	var loadContentItems = function(contentItems){
		var prototype = $("#content-item-list .dc-prototype").first();
		for(i in contentItems){
			var node =  prototype.clone();
			var ci = contentItems[i];
			$(node).attr("id", ci);
			$(node).removeClass("prototype");
			$(".content-item-id", node).html(ci);
			$("#content-item-list").append(node);
			$(node).show();
		}
	}
	
	var dcGetSpaces = function(callback){
		var spaces = new Array(50);
		for(var i = 0; i < spaces.length; i++){
			spaces[i] = {spaceId:"Space #" + i};
		}
		callback.load(spaces);
	};
	
	var dcGetSpace = function(spaceId,callback){
		var contentItems = new Array(50);
		for(var i = 0; i < contentItems.length; i++){
			contentItems[i] = "/this/is/faux/content/item/" + i;
		}
		
		
		callback.load({
						spaceId: spaceId,
						contentItems: contentItems,
						createdOn: "Jan 1, 2010 12:00:00 GMT",
						itemCount: 50
					  });
	};


	
	dcGetSpaces({
		load: function(spaces){
			var prototype = $("#spaces-list .dc-prototype").first();
			for(s in spaces){
				var node =  prototype.clone();
				var space = spaces[s];
				$(node).attr("id", space.spaceId);
				$(node).removeClass("prototype");
				$(".space-id", node).html(space.spaceId);
				$("#spaces-list").append(node);
				$(node).show();
			}
			
			if(spaces.length > 0){
				loadSpace(spaces[0]);
			}
		}
	});
});