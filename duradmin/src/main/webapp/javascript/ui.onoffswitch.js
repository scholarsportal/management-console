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
		//clear the element state
		$(that.element).html("");
		var o = this.options;
		var onButtonOnState = this._createButton(o.onText, o.onStateClass, o.onIconClass, false);
		var offButtonOnState = this._createButton(o.offText, "", o.offIconClass, true);
		offButtonOnState.click(function(evt){
			evt.preventDefault();
			that._fireOffEvent();
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
			evt.preventDefault();
			that._fireOnEvent(evt);
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
	
	_fireOnEvent: function(evt){
		var that = this;
		this.element.trigger("turnOn", {
			success:function(evt){ 
				that._switch("on")
			},
			
			failure: function(evt){
				alert("no turn on failure handler defined");
			},
		});
	},

	_fireOffEvent: function(){
		var that = this;
		this.element.trigger("turnOff", {
			success:function(evt){ 
				that._switch("off")
			},
			
			failure: function(evt){
				alert("no turn offfailure handler defined");
			},
		});
	},

	_switch: function(state){
		$(".button-holder", this.element).css("display","none");

		if(state =="on"){
			$(".button-holder-on", this.element).css("display","inline-block");
		}else{
			$(".button-holder-off", this.element).css("display","inline-block");
		}
	},
	
	on: function(){
		this._switch("on");
		this._fireOnEvent();
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


//an open close switch
$.widget("ui.accessswitch", 
		$.extend({}, $.ui.onoffswitch.prototype, 
			{  //extended definition 
				_init: function(){ 
					$.ui.onoffswitch.prototype._init.call(this); //call super init first
				}, 
				
				
				destroy: function(){ 
					$.ui.onoffswitch.prototype.destroy.call(this); // call the original function 
				}, 
				
				options: $.extend({}, $.ui.onoffswitch.prototype.options, 
						{
					  	      initialState: "on"
							, onStateClass: "unlocked"
							, onIconClass: "unlock"
							, offStateClass: "locked"
							, offIconClass: "lock"
							, onText: "Open"
							, offText: "Closed"
						}
				),
			}
		)
	);



