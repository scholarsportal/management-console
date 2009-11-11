<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="space" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		<div class="sidebar-actions">
			<ul>
				<li>
					<a href="<c:url value="/contents/add?spaceId=${space.spaceId}"/>"><spring:message code="add.contentItem"/></a>
				</li>
				<li>
					<a href="<c:url value="/space/changeAccess">
						<c:param name="spaceId" value="${space.spaceId}"/>
						<c:param name="returnTo" value="${currentUrl}"/>
					</c:url>" >
						<spring:message code="${space.metadata.access == 'OPEN' ?'close.space' : 'open.space'}"/>
					</a>
				</li>

			</ul>
		</div>
		<div>
		<tiles:insertTemplate template="/WEB-INF/jsp/layout/tag-control.jsp">
			<tiles:putAttribute name="spaceId" value="${space.spaceId}"/>
			<tiles:putAttribute name="tags" value="${space.metadata.tags}"/>
		</tiles:insertTemplate>
		</div>
		<div>
			<tiles:insertTemplate template="/WEB-INF/jsp/layout/metadata-control.jsp">
				<tiles:putAttribute name="spaceId" value="${space.spaceId}"/>
				<tiles:putAttribute name="metadata" value="${space.extendedMetadata}"/>
			</tiles:insertTemplate>
		</div>

	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${space.spaceId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<table style="margin:0;padding:0;">
							<tr>
								<td>
									<a href="<c:url value="/spaces.htm"/>"><spring:message code="spaces"/></a> <c:out value="::"/> 
								</td>
								<td style="text-align:right">
									<ul>
										<li>
											<spring:message code="access"/>: <spring:message code="access.${fn:toLowerCase(space.metadata.access)}"/> | 
										</li>
										<li>
											<spring:message code="created"/>: ${space.metadata.created}
										</li>

									</ul>
								</td>
							</tr>
						</table>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<tiles:importAttribute name="contentStoreProvider"/>
				<c:set var="contentStore" value="${contentStoreProvider.contentStore}"/>
				<c:if test="${empty space.contents}">
					<p>
					This space is empty. <a href="contents/add?spaceId=${space.spaceId}"><spring:message code="add.contentItem"/> >></a>
					</p>
				</c:if>
			
				
				<c:if test="${not empty space.contents}">
					<div>
						<span style="float:left">
							<input type="text" name="filter"/> <spring:message code="filterById"/>
						</span>
						<span style="float:right">
							<ul class="horizontal-list">
								<a  href="#">first</a>
								<a  href="#">previous</a>
								<a  href="#"> 1 </a>
								<a  href="#"> 2 </a>
								<a  href="#"> 3 </a>
								<a  href="#"> 4 </a>
								<a  href="#">next</a>
								<a  href="#">last</a>
							</ul>

						</span>
					</div>	
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
											<li><a href="${contentStore.baseURL}/${space.spaceId}/${content}?storeID=${contentStore.storeId}"><spring:message code="download"/></a></li>
											<li>
												<a href="<c:url value="removeContent.htm" >
											   		<c:param name="spaceId" value="${space.spaceId}"/>
											   		<c:param name="contentId" value="${content}"/>
											   		<c:param name="returnTo" value="${currentUrl}"/>
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

