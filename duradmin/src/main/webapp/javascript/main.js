/**
 * 
 * created by Daniel Bernstein
 */

///////////////////////////////////////////////////////////////////////
////duracloud js utils
///////////////////////////////////////////////////////////////////////
var dc = {};
dc.createTable = function(data, /*optional: array*/ columnClasses){
	var table = document.createElement("table");
	for(i = 0; i < data.length; i++){
		var row = document.createElement("tr");
		$(table).append(row);
		for(j = 0; j < data[i].length; j++){
			var cell = document.createElement("td");
			$(row).append(cell);
			$(cell).html(data[i][j]);
			if(columnClasses !=null){
				var columnClass;
				if(j >= columnClasses.length){
					columnClass = columnClasses[ j % columnClasses.length];
				}else{
					columnClass = columnClasses[j];
				}

				$(cell).addClass(columnClass)
			}
		}
	}
	return table;
};


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