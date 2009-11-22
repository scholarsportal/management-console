<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		Choose Deployment
	</tiles:putAttribute>
	<tiles:putAttribute name="menu" value=""/>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						Choose Deployment
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<div>
					<h3>
						${serviceInfo.displayName}
					</h3>
					<p>
						${service.description}
					</p>
				</div>
				<form:form >
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<form:errors element="div" cssClass="message-error" />
					<div>
						<table class="basic-form">
							<tr>
								<td>Deployment Option</td>							
								<td>value</td>
								<td>help</td>
							</tr>
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" name="_eventId_submit" value="Configure"/> 
						<input type="submit" name="_eventId_cancel" value="<spring:message code="cancel"/>"/> 
					</div>
				</form:form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

