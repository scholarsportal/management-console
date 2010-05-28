/**
 * 
 * created by Daniel Bernstein
 */

////////////////////////////////////////////////////////////////////
//jquery extensions
///////////////////////////////////////////////////////////////////
$.fn.makeVisible = function() {
	return $(this).css("visibility", "visible");
};

$.fn.makeHidden = function() {
	return $(this).css("visibility", "hidden");
};

$.fn.nearestOfClass = function(className){
	var nearest = (this.hasClass(className)) ? this : this.closest("." + className);
	return $(nearest);
};

$.fn.toggleOpenClosed = function(){
	$(this).nearestOfClass("button")
			.toggleClass("dialog-open")
			.toggleClass("dialog-closed");
};

$.fn.create = function(tag){
	return $(document.createElement(tag));
	
};

////////////////////////////
//this method loads the children of the new contents
//into the target after emptying the contents
//with a fade in / fade out effect
$.fn.replaceContents = function(/*the pane whose contents will be swapped in*/ newContents,  
								/*the layout for the target*/ layoutOptions){
	var target = this;
	$(target).fadeOut("fast", function(){
		$(target).empty().prepend($(newContents).children());
		$(target).fadeIn("fast");
		if(layoutOptions != null && layoutOptions != undefined){
			$(target).layout(layoutOptions);
		}
	});
	return $(target);
};	
/**
 * openDialogClass optional - a css class to style the target while dialog is open
 * */
$.fn.openDialogOverTarget =  function(evt) {
	this.dialog('open');
	
	/*
	var offset = $(evt.target).offset();
	var coords = ['left','center'];
	this.dialog('option', 'position', coords)
	.dialog('open');
	*/
	
	/*
	$(evt.target).toggleOpenClosed();
	
	//listen when the dialog closes
	$(this).bind( "dialogclose", function(event, ui) {
		$(evt.target).toggleOpenClosed();
	});
	*/
};


$.fn.closeOnLostFocus = function() { // on the open event
    // find the dialog element
    var dialogEl = this;        
    $(document).click(function (e) { // when anywhere in the doc is clicked
        var clickedOutside = true; // start searching assuming we clicked outside
        $(e.target).parents().andSelf().each(function () { // search parents and self
            // if the original dialog selector is the click's target or a parent of the target
            // we have not clicked outside the box
            if ($(this).first().attr("id") == $(dialogEl).attr("id")) {
                clickedOutside = false; // found
                return false; // stop searching
            }
        });
        if (clickedOutside) {
            $(dialogEl).dialog("close"); // close the dialog
            // unbind this listener, we're done with it
            $(document).unbind('click',arguments.callee); 
        }
    });
};
