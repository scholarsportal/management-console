<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-space">
	<tiles:putAttribute name="title">
		<spring:message code="space" />
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/box-control.jsp">
			<tiles:putAttribute name="title">
				<spring:message code="space.details"/>
			</tiles:putAttribute>
			<tiles:putAttribute name="miniform" value="" />
			<tiles:putAttribute name="body">
				<table class="small extended-metadata">
					<tr>
						<td><spring:message code="access" /></td>
						<td><spring:message
							code="access.${fn:toLowerCase(space.metadata.access)}" /></td>
					</tr>
					<tr>
						<td><spring:message code="created" /></td>
						<td>${space.metadata.created}</td>
					</tr>
				</table>
			</tiles:putAttribute>
		</tiles:insertTemplate></div>
		<!-- extended metadata -->
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/metadata-control.jsp">
			<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
			<tiles:putAttribute name="metadata" value="${space.extendedMetadata}" />
		</tiles:insertTemplate></div>
		<!-- tags -->
		<div><tiles:insertTemplate
			template="/WEB-INF/jsp/layout/tag-control.jsp">
			<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
			<tiles:putAttribute name="tags" value="${space.metadata.tags}" />
		</tiles:insertTemplate></div>
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${space.spaceId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<table style="margin: 0; padding: 0;">
							<tr>
								<td>

								<ul>
									<li><a href="<c:url value="/spaces.htm"/>"><spring:message
										code="spaces" /></a> <c:out value="::" /></li>
								</ul>


								</td>
								<td style="text-align: right">
								<ul>
									<li><a
										href="<c:url value="/contents/add?spaceId=${space.spaceId}"/>"><spring:message
										code="add.contentItem" /></a></li>
									<li>|</li>
									<li><a
										href="<c:url value="/space/changeAccess">
						<c:param name="spaceId" value="${space.spaceId}"/>
						<c:param name="returnTo" value="${currentUrl}"/>
					</c:url>">
									<spring:message
										code="${space.metadata.access == 'OPEN' ?'close.space' : 'open.space'}" />
									</a></li>
								</ul>

								</td>
							</tr>
						</table>
					</tiles:putAttribute>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<tiles:importAttribute name="contentStoreProvider" />
				<c:set var="contentStore"
					value="${contentStoreProvider.contentStore}" />
				<c:if test="${empty space.contents}">
					<p>This space is empty. <a
						href="contents/add?spaceId=${space.spaceId}"><spring:message
						code="add.contentItem" /> >></a></p>
				</c:if>


				<c:if test="${not empty space.contents}">
					<table class="small">
						<tr>
							<td><input type="text" name="filter" /> <spring:message
								code="filterById" /></td>
							<td>
							<!-- ugly: should be cleaned up  -->
							<form action="contents.htm?spaceId=${space.spaceId}"
								method="post"><select id="mpp" name="mpp"
								onchange="submit()">
								<option <c:if test="${contentItemList.maxResultsPerPage == 5 }">selected</c:if>>5</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 10 }">selected</c:if>>10</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 25 }">selected</c:if>>25</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 50 }">selected</c:if>>50</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 100 }">selected</c:if>>100</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 200 }">selected</c:if>>200</option>

							</select> <label for="mpp">items per page</label></form>

							</td>
							<td>
								<c:if
									test="${contentItemList.previousAvailable or contentItemList.nextAvailable}">

							<ul class="horizontal-list">
								<c:if
									test="${contentItemList.previousAvailable}">

									<a title="first page"
										href="contents.htm?action=f&spaceId=${space.spaceId}">first</a>
									<a title="next page"
										href="contents.htm?action=p&spaceId=${space.spaceId}">previous</a>
								</c:if>

								<c:if
									test="${contentItemList.nextAvailable}">
									<a title="next"
										href="contents.htm?action=n&spaceId=${space.spaceId}">next</a>
								</c:if>
							</ul>
								</c:if>
							</td>
						</tr>
					</table>
					<table class="standard" id="spacesTable" style="margin-top: 0.25em">
						<tr>
							<th><spring:message code="contentItem.id" /></th>
							<th><spring:message code="metadata" /></th>
						</tr>
						<tbody>
							<c:forEach items="${contentItemList.contentItemList}" var="content"
								varStatus="status">
								<tr id="${content.contentId}"
									onmouseover="loadContentItem('metadata-div-${status.count}', '${space.spaceId}', '${content.encodedContentId}');">
									<td id="actionColumn"><b><a
										href="<c:url value="content.htm">
									              <c:param name="spaceId" value="${space.spaceId}"/>
											   	  <c:param name="contentId" value="${content.contentId}"/>
										      </c:url>">${content.contentId}</a></b>
									<div id="actionDiv" class="actions">
									<ul>
										<!-- 
										<li>
										<a href="<c:url value="${content.downloadURL}"></c:url>"><spring:message code="download" /></a>
											      
											      </li>
										-->
										<li><a
											href="<c:url value="removeContent.htm" >
											   		<c:param name="spaceId" value="${space.spaceId}"/>
											   		<c:param name="contentId" value="${content.contentId}"/>
											   		<c:param name="returnTo" value="${currentUrl}"/>
											    </c:url>"
											onclick="return confirmDeleteOperation();"><spring:message
											code="remove" /></a></li>
									</ul>
									</div>
									</td>
									<td id="details">
									<div id="metadata-div-${status.count}"
										style="min-height: 3.0em; font-size: 0.9em"><!--empty-->
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

