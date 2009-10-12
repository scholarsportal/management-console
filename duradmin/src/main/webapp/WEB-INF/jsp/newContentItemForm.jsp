<%@include file="/WEB-INF/jsp/include.jsp"%>
<div>
	<form:form modelAttribute="contentItem" enctype="multipart/form-data" >
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
		<div>
			<table class="basic-form">
				<tr>
					<td class="label"><label for="contentId">Content ID</label></td>
					<td class="input">
						<form:input  path="contentId" />
						<form:errors path="contentId" cssClass="message-error" />
					</td>
					<td class="field-help">Content ID help goes here.</td>
				</tr>
				<tr>
					<td class="label"><label for="access">Mime Type</label></td>
					<td class="input">
						<form:input path="contentMimetype" />
						<form:errors path="contentMimetype" cssClass="message-error" />
					</td>
					<td class="field-help">Mime type help goes here.</td>
				</tr>
				<tr>
					<td class="label"><label for="access">File</label></td>
					<td class="input">
						<input type="file" name="file"/>
						<div>
							<form:errors path="file" cssClass="message-error" />
						</div>
					</td>
					<td class="field-help">File help goes here</td>
				</tr>

			</table>
		</div>
		<div class="basic-form-buttons" >
			<input type="submit" name="_eventId_submit" value="Add"/> 
			<input type="submit" name="_eventId_cancel" value="Cancel"/> 
		</div>
	</form:form>
</div>
