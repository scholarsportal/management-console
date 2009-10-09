<%@include file="/WEB-INF/jsp/include.jsp"%>
<div>
	<form action="addSpace.htm" method="post">
		<div>
			<table class="basic-form">
				<tr>
					<td class="label"><label for="spaceId">Space ID</label></td>
					<td class="input">
						<form:input id="spaceId" path="space.spaceId" />
						<form:errors path="space.spaceId" cssClass="message-error" />
					</td>
					<td class="field-help">Space ID help goes here.</td>
				</tr>
				<tr>
					<td class="label"><label for="access">Space Access</label></td>
					<td class="input">
						<form:select path="space.access">
							<form:option value="OPEN" label="Open" />
							<form:option value="CLOSED" label="Closed" />
						</form:select>
						<form:errors path="space.access" cssClass="message-error" />
					</td>
					<td class="field-help">Access field description goes here.</td>
				</tr>
			</table>
		</div>
		<div class="basic-form-buttons" ><input type="submit" value="Add" onclick="this.disabled = true;"/> <a  id="cancel" href="spaces.htm">Cancel</a>
		</div>
	</form>
</div>
