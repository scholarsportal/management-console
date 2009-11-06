<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base-space" >
	<tiles:putAttribute name="title">
		<spring:message code="space" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
		<div>
		
		<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
			<tiles:putAttribute name="title">
				Tags
			</tiles:putAttribute>
			<tiles:putAttribute name="miniform">
				<form>
					<input type="text" size="13"/> <input type="submit" value="Add"/>
					<input type="button" onclick="hideMiniform(event)" value="Cancel"/>					
				</form>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<table onmouseover="show(event)" onmouseout="hide(event)" style="white-space:nowrap;">
					<tr>
						<td>
							<a href="#" >tag 1</a>  <input id="button-0" type="button" value="x" style="visibility:hidden"/>
						</td>
					</tr>
				</table>
			</tiles:putAttribute>
		</tiles:insertTemplate>
		</div>
		<div>
		<tiles:insertTemplate template="/WEB-INF/jsp/layout/box-control.jsp">
			<tiles:putAttribute name="title">
				Metadata
			</tiles:putAttribute>
			<tiles:putAttribute name="miniform">
				<form>
					<input type="text" size="13"/> 
					<textarea rows="3" cols="13"></textarea>
					<input type="submit" value="Add"/>
					<input type="button" onclick="hideMiniform(event)" value="Cancel"/>					
				</form>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<table class="small extended-metadata">
					<tr>
						<td onmouseover="show(event)" onmouseout="hide(event)">Name 1
							<input id="metadata-0" type="button" value="x" style="visibility:hidden"/></td>
					</tr>
					<tr>
						<td >
							Value1
						</td>
					</tr>
					<tr>
						<td onmouseover="show(event)" onmouseout="hide(event)" >
							Name 2 <input id="metadata-1" type="button" value="x" style="visibility:hidden" /> 
						</td>
					</tr>
					<tr>
						<td colspan="2">
							Value2
						</td>
					</tr>
				</table>
			</tiles:putAttribute>
		</tiles:insertTemplate>
		</div>
		
	
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

			<p> Mouse over a content item for details.</p>

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

