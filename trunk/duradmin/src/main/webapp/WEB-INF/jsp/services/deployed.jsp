<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="services"/>
	<tiles:putAttribute name="menu">

	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						Deployed Services
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<a id="availableServicesLink" href="<c:url value="/services/available.htm"/>">Available</a>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<c:forEach items="${serviceInfos}" var="serviceInfo">
				<div class="service">
					<table >
						<tr>
							<td>
								<span title="${serviceInfo.description}">${serviceInfo.displayName}</span>
							</td>
							<td style="text-align:right">
								<c:if test="${serviceInfo.newDeploymentAllowed}">
									<a href="<c:url value="/services/deploy">
									<c:param name="serviceId" value="${serviceInfo.id}"/>
									<c:param name="returnTo" value="${currentUrl}"/>
									</c:url>"><spring:message code="deploy.new"/></a>
								</c:if>
							</td>
						</tr>
						<!-- 
						<tr>
							<td colspan="2">
								<p style="margin-left:3em">
									${serviceInfo.description}
								</p>
							</td>
						</tr>
						 -->
						
					</table>
					<table>
						<tr >
							<td colspan="2">
								<table id="deploymentsTable" class="small standard deployment">
									<tbody>
									<tr>
										<th>
											<spring:message code="hostname"/>
										</th>
										<th>
											<spring:message code="status"/>
										</th>
										<th style="text-align:right"><spring:message code="configuration"/></th>
									</tr>
									<c:forEach items="${serviceInfo.deployments}" var="deployment" varStatus="status">
									<tr id="deployment-${serviceInfo.id}-${deployment.id}">
										<td>
											${deployment.hostname}
										</td>
										<td>
											${deployment.status}
										</td>


										<td style="text-align:right">
											<div  id="actionDiv">
												<a onclick="showConfigurationDetails(event, '${serviceInfo.id}','${deployment.id}')"><spring:message code="view"/></a> | 
												<c:if test="${not empty serviceInfo.userConfigs}">
												<a href="<c:url value="/services/deploy">
													<c:param name="serviceId" value="${serviceInfo.id}"/>
													<c:param name="deploymentId" value="${deployment.id}"/>
													<c:param name="returnTo" value="${currentUrl}"/>
													</c:url>"><spring:message code="reconfigure"/></a> |
												</c:if>
												<input type="button" 
													   onclick="undeployService('${serviceInfo.id}','${deployment.id}')" 
													   value="Undeploy"/>
											</div>
										</td>
									</tr>
									<tr><td colspan = "3">
											<div id="configurationDetails" class="details" style="display:none;">
													<c:if test="${not empty deployment.userConfigs}">
														<table>
															<tr>
																<th colspan="2">User Configuration</th>
															</tr>
															<c:forEach items="${deployment.userConfigs}" var="uc">
															<tr>
																<td>${uc.displayName}</td>
																<td>${uc.displayValue}</td>
															</tr>												
															</c:forEach>
														</table>
													</c:if>
													
													<!-- properties table gets inserted here by javascript  -->													
											</div>
										</td>
									</tr>
									</c:forEach>
									</tbody>
								</table>
	
							</td>
						</tr>
	
					</table>
				</div>			
				</c:forEach>
			    
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

