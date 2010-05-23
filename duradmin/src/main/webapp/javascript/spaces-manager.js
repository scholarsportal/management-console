/**
 * 
 * created by Daniel Bernstein
 */


var centerLayout, listBrowserLayout, spacesListPane, contentItemListPane,detailPane, spacesManagerToolbar;

/**
 * Metadata Panel: used for displaying lists of static properties
 */
$.widget("ui.metadatapanel", 
	$.extend({}, $.ui.expandopanel.prototype, 
		{  //extended definition 
			_init: function(){ 
				$.ui.expandopanel.prototype._init.call(this); //call super init first
				var that = this;
				
				//initialize table
				var table =  $("table", this.element);
				if(table.size() == 0){
					table = $(document.createElement("table"));
					this.getContent().prepend(table);	
					var addControlsRow = this._createControlsRow();
					
					var fSuccess = function(){that._addSuccess(that)};

					var triggerAdd = function(){
						that.element.trigger("add", { value: that._getValue(), 
											  success: fSuccess,
											  failure: that._addFailure});
					};
										  
					//attach listeners
					$("input[type=button]", addControlsRow).click(function(evt){
						triggerAdd();
					});
					
					$("input[type=text]", addControlsRow).keyup(function(e) {
							//enter key listener
							if(e.keyCode == 13) {
								triggerAdd();
							}
						});

					table.append(addControlsRow);
				}
				
				this._initializeDataContainer();
			}, 
			
			
			destroy: function(){ 
				//tabular destroy here
				$.ui.expandopanel.prototype.destroy.call(this); // call the original function 
			}, 

			options: $.extend({}, $.ui.expandopanel.prototype.options, {
				data: [
				           {name: "name 1", value: "value1"},
				           {name: "name 2", value: "value2"}
							],
			}),

			load: function(data){
				this.options.data = data;
				for(i in data){
					this._add(data[i]);
				};
			},
			
			_initializeDataContainer: function(){
				
			},
			
			_addSuccess: function(context){
				var v = context._getValue();
				if($.isArray(v)){
					for(i in v){
						context._add(v[i]);
					}
				}else{
					context._add(v);
					
				}
				context._clearForm();
				$("input[type=text]", context.element).first().focus();
			},
			
			_addFailure: function(){
				alert("add operation failed!");	
			},

			_removeSuccess: function(context, data){
				$(".name",context.element).each(function(index,value){
					if($(value).html() == data.name){
						context._animateRemove(
									$(value).parent(), 
									function(){$(value).parent().remove()});
					}
					
				});	
			},
			
			_animateRemove: function(element, removeComplete){
				$(element).hide("slow", function(){
					removeComplete();
				});
				
			},
			
			_removeFailure: function(){
				alert("remove failed!");	
			},
			
			_createControlsRow: function(){
				var controls = $(document.createElement("tr"));
				controls.append(
					$(document.createElement("td"))
						.addClass("name")
						.html("<input type='text' class='name-txt' size='15'/>")
				);

				controls.append(
						$(document.createElement("td"))
							.addClass("value")
							.html("<input type='text' class='value-txt' size='20'/><input type='button' value='+'/>")
					);
				
				return controls;
				
			},

			_getValue: function(){
				var fields = { 
						name: $(".name-txt",this.element).first().val(),
						value: $(".value-txt",this.element).val(),
				};
				
				return fields;
			},

			_getDataContainer: function(){
				return $("table", this.element);
			},

			
			_createDataChild: function(data){
				var child = $(document.createElement("tr"));
				//add the name element
				child.append($(document.createElement("td")).addClass("name").html(data.name));
				//add the value value
				var valueCell = $(document.createElement("td"));
				child.append(valueCell.addClass("value").html(data.value));
				//append remove button
				button = $(document.createElement("span")).addClass("dc-mouse-panel float-r").makeHidden().append("<input type='button' value='x'/>");
				valueCell.append(button);
				return child;
			},
			
			_appendChild: function (child){
				this._getDataContainer().children().last().prepend(child);
				return child;
			},
			
			_add: function(data){
				var that = this;
				var child = this._createDataChild(data);
				child.addClass("dc-mouse-panel-activator");
				this._appendChild(child);
				//add click listener 
				$("input", child).click(function(evt){
					var props = "";
					for(p in data){
						props += p + ": " + data[p] + ", ";
					}
					//alert("clicked remove: "  + props);
					that.element.trigger("remove", { value: that._getValue(), 
						  success: function(){
							that._removeSuccess(that,data);
						  },
						  failure: that._removeFailure});
				});
			},
			
			
			_clearForm: function(){
				$("input[type='text']", this.element).val('');
			},
			
			destroy: function(){ 
			}, 
			
		}
	)
); 

/**
 * Tags panel is substantially the same in display and behavior as metadatapanel
 */
$.widget("ui.tagspanel", 
		$.extend({}, $.ui.metadatapanel.prototype, 
			{  //extended definition 
				_init: function(){ 
					$.ui.metadatapanel.prototype._init.call(this); //call super init first
				}, 
				
				_initializeDataContainer: function(){
					$("table",this.element).prepend("<tr><td><ul class='horizontal-list'></ul></td></tr>");
				},
				
				destroy: function(){ 
					$.ui.metadatapanel.prototype.destroy.call(this); // call the original function 
				}, 

				_createControlsRow: function(){
					var controls = $(document.createElement("tr"));

					controls.append(
							$(document.createElement("td"))
								.addClass("value")
								.html("<input type='text' class='name-txt' size='35'/><input type='button' value='+'/>")
						);
					
					return controls;
					
				},

				_getValue: function(){
					var tags =  $(".name-txt",this.element).first().val().split(" ");
					var fields = new Array();
					for(i in tags){
						fields.push({tag: tags[i]});
					}
					return fields;
				},

				_getDataContainer: function(){
					return $("ul", this.element);
				},

				
				_createDataChild: function(data){
					var child = $(document.createElement("li"));
					//add the name element
					child.html(data.tag);
					//append remove button
					button = $(document.createElement("span")).addClass("dc-mouse-panel float-r").makeHidden().append("<input type='button' value='x'/>");
					child.append(button);
					return child;
				},
				
				_appendChild: function (child){
					this._getDataContainer().append(child);
					return child;
				},
				
				_removeSuccess: function(context, data){
					$("li",context.element).each(function(index,value){
						var text = $(value).text();
						if(text == data.tag){
							context._animateRemove($(value), function(){$(value).remove()});
						}
						
					});	
				},
			}
		)
	);

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
				,   north__size: 			160
				,	center__paneSelector:	".center"
				,   resizable: 				false
				,   slidable: 				false
				,   spacing_open:			0
				,	togglerLength_open:		0
				,   east__paneSelector:    ".east"
				,   east__size:    5
				
	};
	
	//content item detail layout is slightly different from 
	//the space detail - copy and supply overrides
	var contentItemDetailLayoutOptions = $.extend(true,{}, 
													   spaceDetailLayoutOptions, 
													   {north__size:150});
	
	
	detailPane = $('#detail-pane').layout(spaceDetailLayoutOptions);

	
	////////////////////////////
	//this method loads the children of the source
	//into the target after emptying the contents
	//with a fade in / fade out effect
	var swapDetailPane = function(source, target, layoutOptions){
		$(".dc-expandable-panel",source).expandopanel({});

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
	////functions for loading metadata, tags and properties
	/*obsolete
	var appendCopy = function(target, source){
		var copy = $(source).clone();
		$(copy).removeAttr("id").show();
		$(".center", target).append(copy);
		return copy;
	};
	
	*/
	
	var loadMetadataPane = function(target){
		var div = document.createElement("div");
		$(".center", target).append(div);
		$(div).metadatapanel({title: "Metadata"});
		$(div).metadatapanel("load",[{name:"name1", value:"value1"}]);
		return div;
	};

	var loadTagPane = function(target){
		var div = document.createElement("div");
		$(".center", target).append(div);
		$(div).tagspanel({title: "Tags"});
		$(div).tagspanel("load",[{tag:"tag1"}, {tag:"tag2"}, {tag:"tag3"}]);
		return div;
	};
	
	var loadProperties = function(target, /*array*/ properties){
		var div = document.createElement("div");
		$(".center", target).append(div);
		$(div).tabularexpandopanel({title: "Details", data: properties});
	};

	var loadPreview = function(target, contentItem){
		var div = document.createElement("div");
		$(".center", target).append(div);
		$(div).expandopanel({title: "Preview"});
		
		var thumbnail = $(document.createElement("img"))
							.attr("src", contentItem.thumbnailURL)
							.addClass("preview-image");
							
		var viewerLink = $(document.createElement("a"))
							.attr("href", contentItem.viewerURL)
							.append(thumbnail);
		
		
		$(div).expandopanel("getContent").css("text-align", "center").append(viewerLink);

	

		/* This is basic - uses default settings */
		
		viewerLink.fancybox({
				'transitionIn'	:	'elastic',
				'transitionOut'	:	'elastic',
				'speedIn'		:	600, 
				'speedOut'		:	200, 
				'overlayShow'	:	false});
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

		},
		  open: function(e){
			$(e.target).closeOnLostFocus();
		  }
		
	});



	$('.add-space-button').live("click",
			function(evt){
				$("#add-space-dialog").openDialogOverTarget(evt);
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

		},
		  open: function(e){
			 $(e.target).closeOnLostFocus();
		  }
		
	});
	//hides the title bar on all dialogs;
	
	$(".ui-dialog-titlebar").hide();
	
	$('.add-content-item-button').live("click",
			function(evt){
				$("#add-content-item-dialog").openDialogOverTarget(evt);
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
		loadProperties(detail, extractSpaceProperties(space));
		var mp = loadMetadataPane(detail);
		$(mp).bind("add", function(evt, future){
			future.success();
		});

		$(mp).bind("remove", function(evt, future){
			future.success();
		});
		
		var tag = loadTagPane(detail);

		$(tag).bind("add", function(evt, future){
			future.success();
		});

		$(tag).bind("remove", function(evt, future){
			future.success();
		});

		swapDetailPane(detail,"#detail-pane", spaceDetailLayoutOptions);
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
	/**
	 * returns contentItem details
	 */
	var getContentItem = function(contentItemId, spaceId, callback){
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
					},
				extendedMetadata: [
				                   {name: "name1", value: "value1"},
				                   {name: "name2", value: "value2"},
				                   {name: "name3", value: "value3"},
				                   {name: "name4", value: "value4"},
				                   ],
			};
		callback.load(contentItem);
	};

	var loadContentItem = function(contentItem){
		var pane = $("#contentItemDetailPane").clone();
		setObjectName(pane, contentItem.contentId);
		loadPreview(pane, contentItem);
		loadProperties(pane, extractContentItemProperties(contentItem));
		//load the details panel
		var mimetype = contentItem.metadata.mimetype;
		$(".mime-type .value", pane).text(mimetype);
		$(".mime-type", pane).addClass(getMimetypeImageClass(mimetype));
		loadMetadataPane(pane);
		loadTagPane(pane);
		
		
		swapDetailPane(pane, "#detail-pane",contentItemDetailLayoutOptions);
	};

	var getMimetypeImageClass = function(mimetype){
		var mtc = "";
		if(mimetype.indexOf("image") > -1){
			mtc = "image";
		}else if(mimetype.indexOf("video") > -1){
			mtc = "video";
		}else if(mimetype.indexOf("audio") > -1){
			mtc ="audio";
		}else{
			mtc = "text";
		}
		
		return "mime-type-" + mtc;
		
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
		var contentItems = new Array(10);
		for(var i = 0; i < contentItems.length; i++){
			contentItems[i] = spaceId+"/this/is/faux/content/item/" + i;
		}
		
		
		callback.load({
						spaceId: spaceId,
						contentItems: contentItems,
						metadata: {count: 10, created: "Jan 1, 2010 12:00:00 GMT"},
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
			getContentItem($(state.item).attr("id"),spaceId,{
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
			getContentItem($(state.item).attr("id"),spaceId,{
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

	
	$(".dc-item-list-filter").bind("keyup", $.throttle(400, false, function(evt){
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
				dcGetSpace(spaces[0].spaceId, {load:loadSpace});
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