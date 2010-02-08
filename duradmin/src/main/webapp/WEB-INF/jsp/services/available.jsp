<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-service" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />:: <spring:message code="available"/>	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="services"/>
	<tiles:putAttribute name="menu">

	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="available.services"/>
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<a id="deployedServicesLink" href="<c:url value="/services/deployed.htm"/>"><spring:message code="deployed"/></a>
					</tiles:putAttribute>	

				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<c:forEach items="${serviceInfos}" var="serviceInfo">
					<table class="standard small">
						<tr>
							<td style="width:25%">
								${serviceInfo.displayName}
							</td>
							<td>
								${serviceInfo.description}
							
							</td>
							<td style="text-align:right;width:25%">
							<a href="<c:url value="/services/deploy">
								<c:param name="serviceId" value="${serviceInfo.id}"/>
								<c:param name="returnTo" value="${currentUrl}"/>
								</c:url>"><spring:message code="deploy.new"/></a>							
							</td>
						</tr>
					</table>
				</c:forEach>

			    
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

