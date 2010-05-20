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
			togglerClass: "dc-toggler"
	},
	
	/**
	 * Initialization 
	 */
	_init: function(){
		var that = this;
		var options = this.options;
		var togglerClass = options.togglerClass;
		if($("."+togglerClass,this.element).size() == 0){
			$(this.element).children().first().append("<a class='"+togglerClass+"'> </a>");
		}
		that.element.children().first().click(function(evt){
			$(that.element).children().last().slideToggle("fast");
		});
	},
	
});

