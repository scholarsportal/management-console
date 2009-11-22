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
						<a href="<c:url value="/services/available.htm"/>">Available</a>
					</tiles:putAttribute>	
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<c:forEach items="${serviceInfos}" var="serviceInfo">
				<div class="service">
					<table >
						<tr>
							<td>
								${serviceInfo.displayName}
							</td>
							<td style="text-align:right">
								<span class="enabled" >Deployments: ${serviceInfo.deploymentCount} </span> 
								<c:if test="${serviceInfo.newDeploymentAllowed}">
									| 
									<a href="<c:url value="/services/deploy">
									<c:param name="serviceId" value="${serviceInfo.id}"/>
									<c:param name="returnTo" value="${currentUrl}"/>
									</c:url>">Deploy New Instance</a>
								</c:if>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<p style="margin-left:3em">
									${serviceInfo.description}
								</p>
							</td>
						</tr>
					</table>
					<table>
						<tr >
							<td colspan="2">
								<table id="deploymentsTable" class="deployment small standard">
									<tbody>
									<tr>
										<th>
											Hostname
										</th>
										<th>
											Status
										</th>
										<th style="text-align:right">Configuration</th>
									</tr>
									<c:forEach items="${serviceInfo.deployments}" var="deployment">
									<tr>
										<td>
											${deployment.hostname}
										</td>
										<td>
											${deployment.status}
										</td>

										<td style="text-align:right">
											<div  id="actionDiv">
												View | 
												<a href="<c:url value="/services/deploy">
													<c:param name="serviceId" value="${serviceInfo.id}"/>
													<c:param name="deploymentId" value="${deployment.id}"/>
													<c:param name="returnTo" value="${currentUrl}"/>
													</c:url>">Reconfigure</a>
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

