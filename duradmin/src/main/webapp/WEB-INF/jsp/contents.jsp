<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		<spring:message code="space" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="spaces"/>
	<tiles:putAttribute name="menu">
		<div class="sidebar-actions">
			<ul>
				<li>
					<a href="<c:url value="/contents/add?spaceId=${space.spaceId}"/>"><spring:message code="add.contentItem"/></a>
				</li>
			</ul>
		</div>
		<p> Mouse over a content item for details.</p>
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${space.spaceId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<a href="<c:url value="/spaces.htm"/>"><spring:message code="spaces"/></a>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<c:if test="${empty space.contents}">
					<p>
					This space is empty. <a href="contents/add?spaceId=${space.spaceId}">Add Content >></a>
					</p>
				</c:if>
			
				<c:if test="${not empty space.contents}">
					<table class="standard" id="spacesTable">
						<tr>
							<th><spring:message code="contentItem.id"/> </th>
							<th><spring:message code="metadata"/></th>
						</tr>
						<tbody>
					        <c:forEach items="${space.contents}" var="content" varStatus="status">
							<tr id="${content}" onmouseover="loadContentItem('metadata-div-${status.count}', '${space.spaceId}', '${content}');">
								<td id="actionColumn">
									<b><a href="content.htm?spaceId=${space.spaceId}&contentId=${content}">${content}</a></b>
			
									<div id="actionDiv" class="actions">
										<ul>
											<li><a href="${baseURL}/${space.spaceId}/${content}"><spring:message code="download"/></a></li>
											<li>
												<a href="<c:url value="removeContent.htm" >
											   		<c:param name="spaceId" value="${space.spaceId}"/>
											   		<c:param name="contentId" value="${content}"/>
											   		<c:param name="returnTo" value="${returnTo}"/>
											    </c:url>" onclick="return confirmDeleteOperation();"><spring:message code="remove"/></a>
										   	</li>
										</ul>
									</div>
								</td>
							    <td id="details">
									<div id="metadata-div-${status.count}" style="min-height:3.0em; font-size:0.9em">
										<!--empty-->
									</div>
							    </td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:if>

			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>

