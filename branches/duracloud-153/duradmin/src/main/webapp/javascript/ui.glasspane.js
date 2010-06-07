/* 
* Glasspane enables users to block any ui interaction with a component and display 
* visual feedback indicating that the pane is unavailable.
* 
* MIT License (http://www.opensource.org/licenses/mit-license.php)
*
* @author Daniel Bernstein
* 
*/
(function(){
	$.widget('ui.glasspane', {
		_glassPane: null,
		
		_init: function(){
			
		},
		
		_positionGlassPane: function(){
			this._glassPane.css("z-index", this.element.css("z-index")+10);
			this._glassPane.offset(this.element.offset());
			this._glassPane.height(this.element.height());
			this._glassPane.width(this.element.width());
		},
		
		show: function(message) {
			if(this._glassPane != null){
				this.hide();
			}
			
			var that = this;
			var o 	 = this.options;
			this._glassPane = $.fn.create("div");
			if(message != null && message != undefined){
				this._glassPane.html("<h1>"+message+"</h1>");
			}
			this._positionGlassPane();
			var listener = function(evt){
				evt.stopPropagation();
				evt.preventDefault();
			};
			
			this._glassPane.click(listener)
						   .mouseover(listener)
						   .mouseout(listener)
						   .mousedown(listener)
						   .mouseup(listener)
						   .keyup(listener)
						   .keydown(listener)
						   .keypress(listener)
						   .dblclick(listener);

			$(document.body).append(this._glassPane);
			
			this._glassPane.css("visibility: visible");
			this._glassPane.addClass(this.options.glassPaneClass);
			
	    },
	
		hide: function(){
	    	if(this._glassPane != null){
	    		this._glassPane.remove();
	    	}
	    },
	
		options: {
			glassPaneClass: "ui-glasspane",
				
		}
	});	
	
})();



