/**
 * 
 * created by Daniel Bernstein
 */

var centerLayout, servicesListPane, detailPane;

$(document).ready(function() {

	centerLayout = $('#page-content').layout({
		west__size:				800
	,   west__paneSelector:     "#services-list-view"
	,   west__onresize:         "servicesListPane.resizeAll"
	,	center__paneSelector:	"#detail-pane"
	,   center__onresize:       "detailPane.resizeAll"
	});
	
	
	
	var servicesListPaneLayout = {
			north__paneSelector:	".north"
		,   north__size: 			60
		,	center__paneSelector:	".center"
		,   resizable: 				false
		,   slidable: 				false
		,   spacing_open:			0			
		,	togglerLength_open:		0	
	};
			
	servicesListPane = $('#services-list-view').layout(servicesListPaneLayout);
	
	//detail pane's layout options
	var detailLayoutOptions = {
			north__paneSelector:	".north"
				,   north__size: 			130
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
				
	};
	
	
	$('#detail-pane').layout(detailLayoutOptions);
	var details = $(document.createElement("div"));
	$("#detail-pane .center").append(details);
	
	$(details).tabularexpandopanel({title: "Details", data: [
	                                     ["ID", "service-name-1.0.0"],
	                                     ["Hostname", "127.0.0.1"], 
	                                     ["Started", "Fri 06 Jan 2010 13:55:01 UTC"], 
	                                    ]});
	
	var configuration = $(document.createElement("div"));
	$("#detail-pane .center").append(configuration);
	
	$(configuration).tabularexpandopanel({title: "Configuration", data: [
	                                     ["Config Name1", "config value1 goes here"],
	                                     ["Config Name2", "config value2 goes here"],
	                                     ["Config Name3", "config value3 goes here"],
	                                     ["Config Name4", "config value4 goes here"],
	                                     ["Config Name5", "config value5 goes here"],

	                                     ]});
	
	
});