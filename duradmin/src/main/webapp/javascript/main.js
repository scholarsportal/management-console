/**
 * 
 * created by Daniel Bernstein
 */

////////////////////////////////////////////////////////////////////
//Opens any dialog by id over the event's target
////////////////////////////////////////////////////////////////////
var dcOpenDialogOverTarget =  function(evt, dialogId) {
	var offset = $(evt.target).offset();
	var coords = [offset.left,offset.top];
	coords[1] = coords[1] + evt.target.height;
	$(dialogId)
		.dialog('option', 'position', coords)
		.dialog('open');
	return false;
};

var dcNearestDcItem = function(node){
	return($(node).hasClass("dc-item")) ? $(node).first() : $(node).closest(".dc-item");
};

var dcStyleItem =  function(target){
	var dcItem = dcNearestDcItem(target);
	$(dcItem).removeClass("dc-checked-selected-list-item dc-checked-list-item dc-selected-list-item");
	var checked = $(dcItem).find("input[type=checkbox][checked]").size() > 0;
	$(dcItem).addClass(checked ? "dc-checked-selected-list-item" : "dc-selected-list-item");
	dcRestyleDcItemSiblings(dcItem);
};

//style the list items
var dcRestyleDcItemSiblings = function(dcItem){
	$(dcItem).siblings().removeClass("dc-selected-list-item dc-checked-selected-list-item dc-checked-list-item");
	$(dcItem).siblings().find("input[type=checkbox][checked]").closest(".dc-item").addClass("dc-checked-list-item");
};



$(document).ready(function() {

	////////////////////////////////////////////////////////////////////
	//start generic dc-item list behavior
	////////////////////////////////////////////////////////////////////
	
	$(".dc-item-list .dc-item").live("click",function(evt){
		var dcItem = dcNearestDcItem(evt.target);
		dcRestyleDcItemSiblings(dcItem);
		if($("input[type=checkbox][checked]",dcItem).size() > 0){
			$(dcItem).addClass("dc-checked-selected-list-item");	
		}else{
			$(dcItem).addClass("dc-selected-list-item");
		}
	}).live("mouseover",function(evt){
		$(".dc-action-panel",evt.target).css("visibility","visible");
	}).live("mouseout",function(evt){
		if(!$.contains(evt.target, evt.relatedTarget)){
			$(".dc-action-panel",evt.target).css("visibility","hidden");
		}
	});


	//on checkbox click styling
	$(".dc-item-list input[type=checkbox]")
		.live("click", function(evt){
			dcStyleItem(evt.target);
			evt.stopPropagation();
		});
	$(".dc-item-list .dc-item")
		.live("dblclick", function(evt){
			evt.stopPropagation();
			$(dcNearestDcItem(evt.target)).find("input[type=checkbox]").click();
			dcStyleItem(evt.target)});

	//if you set this style property in css, it doesn't layout properly
	$(".dc-item .dc-action-panel").css("visibility", "hidden");



	////////////////////////////////////////////////////////////////////
	//end generic dc-item list behavior
	////////////////////////////////////////////////////////////////////
	
	
	$(".dc-toggler").live("click",function(evt){
		var parent  = $(evt.target).closest(".dc-expandable-panel");
		$(parent).children().last().slideToggle("slow");
	});
		
	$(document.body).bind("DOMSubtreeModified",function(){ 
		$(".dc-expandable-panel").each(function(){
			
			if($("input[class=dc-toggler]",this).size() == 0){
				$(this).children().first().append("<input type='button' value='>' class='dc-toggler'/>");
			}
		});	
	})
	
	$(".dc-mouse-panel-activator td,li.dc-mouse-panel-activator").live("mouseover",function(evt){
		console.debug(evt);
		
		var ancestor = $(evt.target).closest(".dc-mouse-panel-activator");
		$(".dc-mouse-panel",ancestor)
					 .css("visibility","visible")
					 .fadeIn("fast");
	}).live("mouseout",function(evt){
		console.debug(evt);
		var ancestor = $(evt.target).closest(".dc-mouse-panel-activator");
		if($(ancestor).find(evt.relatedTarget).size() == 0){
			$(".dc-mouse-panel",ancestor).fadeOut("fast");
		}
	});
	
	///////////////////////////////////////////////////////////////////////
	////Layout Page Frame
	///////////////////////////////////////////////////////////////////////
	var pageHeaderLayout 

	 $("body").layout({
			north__size:	    87
		,   north__paneSelector:"#page-header"
		,	north__onresize:	"pageHeaderLayout.resizeAll"
		,   resizable:   false
		,   slidable:    false
		,   spacing_open:			0			
		,	togglerLength_open:		0			
		,	togglerLength_closed:	-1
		,	useStateCookie:		true
		,   center__paneSelector: "#page-content"
		,	center__onresize:	"centerLayout.resizeAll"
	});


	var pageHeaderLayout  = $("#page-header").layout({
			center__paneSelector:	"#header-center"
		,	east__paneSelector:	"#header-east"
		,	east__size:			600
		,   resizable:   false
		,   slidable:    false
		,   spacing_open:			0			
		,	togglerLength_open:		0			
		,	togglerLength_closed:	-1			

	});
/*
	var headerCenter  = $("#header-center").layout({
		center__paneSelector:	"#dc-logo-panel"
	,	south__paneSelector:	"#dc-tabs-panel"
	,	south__size:			30
	,   resizable:   false
	,   slidable:    false
	,   spacing_open:			0			
	,	togglerLength_open:		0			
	,	togglerLength_closed:	-1			
	
	});
	*/
});