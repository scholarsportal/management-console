<%@include file="/WEB-INF/jsp/include.jsp"%>
		<table class="boxcontrol">
			<tr>
				<td class="title">
					<div style="float:left; width:50%;">
						<tiles:insertAttribute name="title"/>
					</div>
					<div style="text-align:right;float:right; width:50%;">
						<input type="button" class="addButton" onclick="showMiniform(event);" style="fint-size:0.5em;margin:0;padding:0" value="+"/>
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