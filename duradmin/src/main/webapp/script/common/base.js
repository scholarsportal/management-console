dojo.require("dijit.TooltipDialog");
dojo.require("dijit.Dialog");


/*zebra stripe standard tables*/	
dojo.addOnLoad(function(){
	dojo.query(".standard > tbody > tr:nth-child(odd)").addClass("oddRow");
});



dojo.addOnLoad(function(){

	/*adds mouse listeners on spaces table rows*/
	dojo.query("#spacesTable > tbody > tr",document).forEach(
	    function(row) {
	    	dojo.connect(row, 'onmouseover', function() {
				dojo.addClass(row,"hover");
				dojo.query("div[id=actionDiv]",row).attr('style', {visibility:'visible'});
	    	});

           	dojo.connect(row, 'onmouseout', 
				function(){
					dojo.removeClass(row,"hover");
					console.debug('moused out of row: ' + row);
					dojo.query("div[id=actionDiv]",row).attr('style', {visibility:'hidden'});
      		});
	    }
	);		
});


function confirmDeleteOperation(){
	var result = confirm('You are about to perform an irreversible delete operation\.\nClick \'OK\' if you are sure you wish to continue\.');
	
	if(!result) { 
		return false; 
	}
}