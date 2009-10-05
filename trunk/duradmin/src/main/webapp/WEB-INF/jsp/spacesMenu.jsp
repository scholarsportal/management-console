<%@include file="/WEB-INF/jsp/include.jsp"%>
<div class="sidebar-actions">
	<a href="#"	onclick="dijit.byId('spaceAddDialog').show()">Add Space</a>
</div>

<div dojotype="dijit.Dialog" id="spaceAddDialog" title="Add Space" execute="">
	<form action="spaceAdd.action" method="post">
	<table>
		<tr>
			<td><label for="spaceId">Space ID</label></td>
			<td><input type="text" id="spaceId" name="spaceId" /></td>
		</tr>
		<tr>
			<td><label for="access">Space Access</label></td>
			<td><select id="access" name="access">
				<option value="OPEN" selected>Open</option>
				<option value="CLOSED">Closed</option>
			</select></td>
		</tr>
		<tr>
			<td/>
			<td><input type="submit" value="Add" /></td>
		</tr>
	</table>
	
</form>
</div>

