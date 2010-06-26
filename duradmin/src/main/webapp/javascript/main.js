/**
 * 
 * created by Daniel Bernstein
 */



$(document).ready(function() {

	
	
	///////////////////////////////////////////////////////////////////////
	////controls rollovers on tags and metadata
	///////////////////////////////////////////////////////////////////////
	
	$(".dc-mouse-panel-activator td, li.dc-mouse-panel-activator, .dc-mouse-panel").live("mouseover",function(evt){
		var ancestor = $(evt.target).nearestOfClass(".dc-mouse-panel-activator");
		$(".dc-mouse-panel",ancestor).css("visibility","visible");
	}).live("mouseout",function(evt){
		var ancestor = $(evt.target).nearestOfClass(".dc-mouse-panel-activator");
		$(".dc-mouse-panel",ancestor).css("visibility","hidden");
	});
	
	$(".dc-mouse-panel").css("visibility", "hidden");
	
	///////////////////////////////////////////////////////////////////////
	////Layout Page Frame
	///////////////////////////////////////////////////////////////////////
	var pageHeaderLayout 

	 $("body").layout({
			north__size:	    87
		,   north__paneSelector:"#page-header"
		,   resizable:   false
		,   slidable:    false
		,   spacing_open:			0			
		,	togglerLength_open:		0			
		,	togglerLength_closed:	-1
		,	useStateCookie:		true
		,   center__paneSelector: "#page-content"
		,	center__onresize:	"centerLayout.resizeAll"
	});
});