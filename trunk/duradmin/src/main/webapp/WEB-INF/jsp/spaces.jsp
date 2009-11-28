<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="spaces" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						<spring:message code="spaces"/>
					</tiles:putAttribute>	
					<tiles:putAttribute name="subtitle">
						<a id="addSpaceLink" href="<c:url value="/spaces/add"/>"><spring:message code="add.space"/></a>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<table class="standard" id="spacesTable">
					<tbody>

					<tr>
						<th>
							<a href="<c:url value="spaces.htm?sortField=spaceId&asc=true"/>"><spring:message code="space"/></a>
						</th>
						<th>
							<spring:message code="metadata"/>
						</th>
					</tr>
						<c:forEach items="${spaces}" var="spaceId" varStatus="status">
							<tr id="${spaceId}" onmouseover="loadSpaceMetadata('metadata-div-${status.count}', '${spaceId}');">
								<td id="actionColumn" >
									<div>
										<b><a href="contents.htm?spaceId=${spaceId}"><c:out value="${spaceId}" /></a></b>
									</div>
								
									<div id="actionDiv" class="actions" >
										<ul>
										
										
											<li><a style="font-weight:bold" href="<c:url value="contents/add?spaceId=${spaceId}&returnTo=${currentUrl}"/>">
													<spring:message code="add.contentItem"/>
												</a>
											</li>
											
											<li><a id="removeSpaceLink" href="<c:url value="removeSpace.htm">
													   		<c:param name="spaceId" value="${spaceId}"/>
													   		<c:param name="returnTo" value="${currentUrl}"/>
													    </c:url>" onclick="return confirmDeleteOperation();">
													<spring:message code="remove"/>
												</a>
											</li>
										</ul>
									</div>
			
									
								</td>					
								<td >
									<div id="metadata-div-${status.count}" style="min-height:2.0em; font-size:0.9em">
										<!--empty-->
									</div>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
