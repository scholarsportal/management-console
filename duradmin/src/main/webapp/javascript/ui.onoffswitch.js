/**
 * created by Daniel Bernstein
 */

/////////////////////////////////////////////////////////////////////////////////////
///on off switch  widget
///
/////////////////////////////////////////////////////////////////////////////////////
$.widget("ui.onoffswitch",{
	/**
	 * Default values go here
	 */
	options: {
		   initialState: "on"
		,  onStateClass: "onffswitch-on-state"
		, onIconClass: "icon-on"
		, offStateClass: "onoffswitch-off-state"
		, offIconClass: "icon-off"
		, onText: "On"
		, offText: "Off"
	},
	
	
	/**
	 * Initialization 
	 */
	_init: function(){
		var that = this;
		var o = this.options;
		var onButtonOnState = this._createButton(o.onText, o.onStateClass, o.onIconClass, false);
		var offButtonOnState = this._createButton(o.offText, "", o.offIconClass, true);
		offButtonOnState.click(function(evt){
			that.element.trigger("turnOff", {
				success: function(){ 
					that._switch("off")
				},
				
				failure: function(){
					alert("no failure handler defined");
				},
			});
		});
		
		var onState = $(document.createElement("span"))
							.addClass("flex")
							.addClass("button-holder")
							.addClass("button-holder-on")
							.append(onButtonOnState)
							.append(offButtonOnState);
		
		$(this.element).append(onState);

		var onButtonOffState = this._createButton(o.onText, "", o.onIconClass, true);
		var offButtonOffState = this._createButton(o.offText, o.offStateClass, o.offIconClass, false);

		onButtonOffState.click(function(evt){
			that.element.trigger("turnOn", {
				success:function(evt){ 
					that._switch("on")
				},
				
				failure: function(evt){
					alert("no failure handler defined");
				},
			});
		});
		var offState = $(document.createElement("span"))
							.addClass("flex")
							.addClass("button-holder")
							.addClass("button-holder-off")
							.append(onButtonOffState)
							.append(offButtonOffState);
		$(this.element).append(offState);

		
		this._switch(o.initialState);
	},
	
	_switch: function(state){
		$(".button-holder", this.element).hide();

		if(state =="on"){
			$(".button-holder-on", this.element).show();
		}else{
			$(".button-holder-off", this.element).show();
		}
	},

	_createButton: function(text, stateClass, iconClass, clickable){
		var baseSpan =
				$(document.createElement("span"));
		var baseInnerButton = 
				$(document.createElement("i"))
					.addClass("pre")
					.addClass(iconClass)
					.addClass(stateClass)
					.html(text);
		if(clickable){
			baseSpan.append(baseInnerButton);
			return $(document.createElement("button"))
						.addClass("flex")
						.addClass("button")
						.addClass("switch")
						.append(baseSpan);
		}else{
			return baseInnerButton;
		}
	}
});


