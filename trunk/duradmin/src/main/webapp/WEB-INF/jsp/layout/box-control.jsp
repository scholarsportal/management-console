<%@include file="/WEB-INF/jsp/include.jsp"%>

	<script type="text/javascript">
	dojo.addOnLoad(function(){
		
		dojo.query(".boxcontrol .miniform-button").forEach(
			    function(element) {
			    	dojo.connect(element, 'onclick', function(evt) {
			    		showMiniform(evt);
			    	});
			    }
			);		
		
		
		/*configure tag and metadata components*/
		dojo.query("div.tag, table.extended-metadata tr").forEach(
		    function(element) {
		    	dojo.connect(element, 'onmouseover', function() {
					dojo.query("[type=button]",element).attr('style', {visibility:'visible'});
		    	});
	
		    	dojo.connect(element, 'onmouseout', function() {
					dojo.query("[type=button]",element).attr('style', {visibility:'hidden'});
		    	});
		    }
		);		
	});
	</script>

		<table class="boxcontrol">
			<tr>
				<td class="title">
					<div style="float:left; width:50%;">
						<tiles:insertAttribute name="title"/>
					</div>
					<div style="text-align:right;float:right; width:50%;">
						<tiles:insertAttribute name="titlebuttons" ignore="true" />
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div id="miniform" class="miniform" style="display:none">
						<tiles:insertAttribute name="miniform"/>
					</div>
					<div class="body">
						<tiles:insertAttribute name="body"/>
					</div>				
				</td>
			</tr>
		</table>