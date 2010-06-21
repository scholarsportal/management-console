<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="add.contentItem" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu" value=""/>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="add.contentItem"/>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<form:form modelAttribute="contentItem" enctype="multipart/form-data" >
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<div>
						<table class="basic-form">
							<tr>
								<td class="label"><label for="contentId"><spring:message code="contentItem.id"/></label></td>
								<td class="input">
									<form:input  path="contentId" />
									<form:errors path="contentId" cssClass="message-error" />
								</td>
								<td class="field-help"><spring:message code="contentItem.id.help"/></td>
							</tr>
							<tr>
								<td class="label"><label for="access"><spring:message code="mimetype"/></label></td>
								<td class="input">
									<form:input path="contentMimetype" />
									<form:errors path="contentMimetype" cssClass="message-error" />
								</td>
								<td class="field-help">
									<spring:message code="mimetype.help"/>
									<a href="http://en.wikipedia.org/wiki/MIME"><spring:message code="help.moreInfo"/></a>
								</td>
							</tr>
							<tr>
								<td class="label"><label for="access">						
									<spring:message code="form.contentItem.file"/>
								</label></td>
								<td class="input">
									<input type="file" name="file"/>
									<div>
										<form:errors path="file" cssClass="message-error" />
									</div>
								</td>
								<td class="field-help">
									<spring:message code="form.contentItem.file.help"/>
								</td>
							</tr>
			
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" name="_eventId_submit" value="Add"/> 
						<input type="submit" name="_eventId_cancel" value="Cancel"/> 
					</div>
				</form:form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

