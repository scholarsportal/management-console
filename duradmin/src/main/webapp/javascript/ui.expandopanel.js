/**
 * 
 * created by Daniel Bernstein
 */

/////////////////////////////////////////////////////////////////////////////////////
///selectable list widget
///
/////////////////////////////////////////////////////////////////////////////////////

/**
 * expando panel widget
 */
$.widget("ui.expandopanel",{
	/**
	 * Default values go here
	 */
	options: {
			togglerClass: "dc-toggler",
			title: null,
			headerClass: "segment-header",
			contentClass: "segment-content",
	},
	
	
	/**
	 * Initialization 
	 */
	_init: function(){
		var that = this;
		var options = this.options;
		var togglerClass = options.togglerClass;
		var clearFixClass = "clearfix";
		
		//add children if none are defined in html
		while($(this.element).children().size() < 2){
			$(this.element).append(document.createElement("div"));
		};
		
		var header = $(this.element).children().first();
		var content = $(this.element).children().last();
		var title = options.title;
		
 		//set the title if not null
		if(title != null){
			header.html(title);
		}
		
		//add toggle button
		if($("."+togglerClass,this.element).size() == 0){
			header.append("<a class='"+togglerClass+"'> </a>");
		}
		
		//style the header
		header.addClass(options.headerClass);
		header.addClass(options.clearfix);
		
		//add toggle to the header 
		header.click(function(evt){
			content.slideToggle("fast");
		});
		
		//style the content 
		content.addClass(options.contentClass);
		content.addClass(options.clearfix);

	},
	
	getContent: function(){
		return $(this.element).children().last();
	},
	
	append: function(/*dom node*/ node){
		this.getContent().append(node);
	},
});

