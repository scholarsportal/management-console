/**
 * 
 * created by Daniel Bernstein
 */
var pageHeaderLayout, centerLayout, listBrowserLayout, spacesListPane, contentItemListPane,detailPane, spacesManagerToolbar;

	$(document).ready(function() {

		 $('body').layout({
				north__size:	    100
			,   north__paneSelector:'.page-header'
			,	north__onresize:	'pageHeaderLayout.resizeAll'
			,   resizable:   false
			,   slidable:    false
			,   spacing_open:			0			
			,	togglerLength_open:		0			
			,	togglerLength_closed:	-1
			,   center__paneSelector: '#page-content'
			,	center__onresize:	'centerLayout.resizeAll'
			,	useStateCookie:		true
		});


		centerLayout = $('#page-content').layout({
			//minSize:				50	// ALL panes
			north__size: 			50	
		,	north__paneSelector:     ".center-north"
		,   north__resizable:   false
		,   north__slidable:    false
		,   north__spacing_open:			0			// cosmetic spacing
		,	north__togglerLength_open:		0			// HIDE the toggler button
		,	north__togglerLength_closed:	0			// "100%" OR -1 = full width of pane

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
		
		northLayout = $('.page-header').layout({
				center__paneSelector:	".north-center"
			,   center__resizable:   false
			,   center__slidable:    false
			,   center__spacing_open:			0			
			,	center__togglerLength_open:		0			
			,	center__togglerLength_closed:	-1			

			,	south__paneSelector:	".north-south"
			,   south__resizable:   false
			,   south__slidable:    false
			,   south__spacing_open:			0			
			,	south__togglerLength_open:		0			
			,	south__togglerLength_closed:	-1			

			,	south__size:			30
		});

		var spacesAndContentLayoutOptions = {
				north__paneSelector:	".north"
			,   north__size: 			100
			,	center__paneSelector:	".center"
			,   resizable: 				false
			,   slidable: 				false
			,   spacing_open:			0			
			,	togglerLength_open:		0			
		};
				
		spacesListPane = $('#spaces-list-view').layout(spacesAndContentLayoutOptions);
		contentItemListPane = $("#content-item-list-view").layout(spacesAndContentLayoutOptions);

		//detail pane's layout options
		var detailOptions = {
				north__paneSelector:	".north"
					,   north__size: 			175
					,	center__paneSelector:	".center"
					,   resizable: 				false
					,   slidable: 				false
					,   spacing_open:			0
					,	togglerLength_open:		0
		};


		detailPane = $('#detail-pane').layout(detailOptions);

		//style the list items
		$(".dc-item-list .dc-item").live("click",function(evt){
			$(evt.target).siblings().removeClass("dc-selected-list-item");
			$(evt.target).addClass("dc-selected-list-item");
		}).live("mouseover",function(evt){
			$(".dc-action-panel",evt.target).css("visibility","visible").fadeIn("fast");
		}).live("mouseout",function(evt){
			//var offset = $(evt.target).offset
			if(!jQuery.contains(evt.target, evt.relatedTarget)){
				$(".dc-action-panel",evt.target).fadeOut("fast");
			}
		});

		////////////////////////////////////////////////////////////////////
		//start generic dc-item list behavior
		////////////////////////////////////////////////////////////////////
		//if you set this style property in css, it doesn't layout properly
		$(".dc-item .dc-action-panel").css("visibility", "hidden");


		//checkbox styling
		$(".dc-item-list input[type=checkbox]").live("click", function(evt){
			if(evt.target.checked == true){
				$(evt.target).closest(".dc-item").addClass("dc-checked-list-item");
			}else{
				$(evt.target).closest(".dc-item").removeClass("dc-checked-list-item");
			}
		});

		////////////////////////////////////////////////////////////////////
		//end generic dc-item list behavior
		////////////////////////////////////////////////////////////////////

		$(".dc-toggler").live("click",function(evt){
			var parent  = $(evt.target).closest(".expandable-panel");
			$(".content",parent).slideToggle("slow");
		});
		
		
		$(".dc-mouse-panel-activator").live("mouseover",function(evt){
			$(".dc-mouse-panel",evt.target).css("visibility","visible").fadeIn("fast");
		}).live("mouseout",function(evt){
			if(!jQuery.contains(evt.target, evt.relatedTarget)){
				$(".dc-mouse-panel",evt.target).fadeOut("fast");
			}
		});

		////////////////////////////
		//this method loads the children of the source
		//into the target after emptying the contents
		//with a fade in / fade out effect
		var swapDetailPane = function(source, target){
			var detail = $(source).clone();
			$(target).fadeOut("fast", function(){
				$(target).empty().prepend(detail.children());
				$(target).fadeIn("fast");
				$(target).layout(detailOptions);
			});
			return $(source);
		};		

		
		////////////////////////////////////////////
		//sets contents of object-name class
		///
		var setObjectName = function(pane, name){
			$(".object-name", pane).empty().prepend(name + " " + new Date().toString());	
		};
				

		///////////////////////////////////////////
		///click on a space list item
		$("#spacesList .dc-item").click(
			function(evt){
				setObjectName("#spaceDetailPane", "My space");
				swapDetailPane("#spaceDetailPane","#detail-pane");
				$("#contentItemList > .dc-item").removeClass("dc-selected-list-item");
			}
		);

		///////////////////////////////////////////
		///click on a content list item
		$("#contentItemList .dc-item").click(
				function(evt){
					setObjectName("#contentItemDetailPane", "My Content Item");
					swapDetailPane("#contentItemDetailPane", "#detail-pane");

				}
			);
			
	});