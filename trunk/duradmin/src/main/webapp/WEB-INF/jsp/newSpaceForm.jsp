<%@include file="/WEB-INF/jsp/include.jsp"%>
<div>
	<form:form modelAttribute="space">
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
		<div>
			<table class="basic-form">
				<tr>
					<td class="label"><label for="spaceId">Space ID</label></td>
					<td class="input">
						<form:input id="spaceId" path="spaceId" />
						<form:errors path="spaceId" cssClass="message-error" />
					</td>
					<td class="field-help">Space ID help goes here.</td>
				</tr>
				<tr>
					<td class="label"><label for="access">Space Access</label></td>
					<td class="input">
						<form:select path="access">
							<form:option value="OPEN" label="Open" />
							<form:option value="CLOSED" label="Closed" />
						</form:select>
						<form:errors path="access" cssClass="message-error" />
					</td>
					<td class="field-help">Access field description goes here.</td>
				</tr>
			</table>
		</div>
		<div class="basic-form-buttons" >
			<input type="submit" name="_eventId_submit" value="Add"/> 
			<input type="submit" name="_eventId_cancel" value="Cancel"/> 
		</div>
	</form:form>
</div>
