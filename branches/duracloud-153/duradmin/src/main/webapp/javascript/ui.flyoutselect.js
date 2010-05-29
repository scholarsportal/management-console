/**
 * This jquery plugin is a fancy flyout select box
 * created by Daniel Bernstein
 */
$.widget("ui.flyoutselect", 
	{  
		_init: function(){ 
			var that = this;
			var o = this.options;
			var widgetClass = o.widgetClass;
			
			$(this.element).addClass(widgetClass)
			var selection = $.fn.create("a")
						.addClass("flex button")
						.addClass(widgetClass + "-selected")
						.append($.fn.create("span").append(
											$.fn.create("i")
												.addClass("post arw-down-liteblue")
												.addClass(widgetClass+"-selected-text")));
			
			$(this.element).append(selection);
			
			var ul = $.fn.create("ul");
			$(this.element).append(ul);
			
			$(this.element).hover(
					function() { $('ul', this).css('display', 'block'); },
					function() { $('ul', this).css('display', 'none'); }
			);
				
			this._selectedIndex = o.selectedIndex;

			this._rerender(o.data);

		}, 
		
		_rerender: function(data){
			var that = this;
			var ul = $("ul", this.element).first();
			var widgetClass = this.options.widgetClass;
			ul.children().remove();
			
			var selectedTextClass = widgetClass + "-selected-text";
			for(i in data){
				if(i == this._selectedIndex){
					$("."+ selectedTextClass ,this.element).html(data[i].label);
				}else{
					var item = $.fn.create("li");
					var itemTextHolder = $.fn.create("a").html(data[i].label);
					item.append(itemTextHolder);
					ul.append(item);
					item.click(function(){
						var data = that.options.data;
						for(d in data){
							if(data[d].label == itemTextHolder.html()){
								that._selectedIndex = d;	
								$("ul", that.element).fadeOut("fast",function(){
									that._rerender(data);
								});
							}
						}
					});
				}
			}
		},
		
		_selectedIndex: 0,
		
		destroy: function(){ 

		}, 
		
		options: {
			widgetClass: "fos-widget",
			data: [
			       {id:"1", label:"Amazon S3"},
			       {id:"2", label:"Rackspace"},
			       {id:"3", label:"EMC - with a very long name here"}],
			selectedIndex: 0, 
			
			       
		},
		
	}
);
