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
				</div>


				<form:form >
					<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
					<form:errors element="div" cssClass="message-error" />
					<div>
						<table class="basic-form">
							<tr>
								<td class="label">Where would you like to deploy this service?</td>							
								<td class="input">
									<ul class="vertical-list">
									<c:forEach items="${serviceInfo.deploymentOptions}" var="option" varStatus="status">
										<li>
											<input id="do-${status.count}" checked="${status.count == 0}" 
													type="radio" name="deploymentOption" value="${option}" />
											<label for="do-${status.count}"> ${option.displayName} ${option.hostname} ${option.locationType}</label>
										</li>
									</c:forEach>
									</ul>
									
								</td>
							</tr>
							
						</table>
					</div>
					<div class="basic-form-buttons" >
						<input type="submit" name="_eventId_submit" value="Next"/> 
						<input type="submit" name="_eventId_cancel" value="<spring:message code="cancel"/>"/> 
					</div>
				</form:form>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

