dojo.require("dijit.TooltipDialog");
dojo.require("dijit.Dialog");


/*zebra stripe standard tables*/	
dojo.addOnLoad(function(){
	dojo.query(".standard > tbody > tr:nth-child(odd)").addClass("oddRow");
});



dojo.addOnLoad(function(){
	/*adds action div on spaces table*/
	dojo.query("#spacesTable td[id=actionColumn]",document).forEach(
	    function(cell) {
	    	var rowId = cell.parentNode.id;
	    	var cellDiv = dojo.create("div");
	    	cellDiv.id = "actionDiv";
	    	dojo.addClass(cellDiv,"actions");
	    	dojo.html.set(cellDiv,"<a href='#"+rowId+"'>Delete</a>");
	    	cell.appendChild(cellDiv);
	    	console.log("row id = " + rowId);
	    }
	);	

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


