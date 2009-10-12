<%@include file="/WEB-INF/jsp/include.jsp" %>
<div style="width:100%">
	<c:if test="${empty space.contents}">
		<p>
		This space is empty. <a href="contents/add?spaceId=${space.spaceId}">Add Content >></a>
		</p>
	</c:if>

	<c:if test="${not empty space.contents}">
		<table class="standard" id="spacesTable">
			<tr>
				<th></th>
				<th>Content ID</th>
				<th>Mime Type</th>
				<th>Checksum</th>
				<th>Last Modified</th>
	
			</tr>
			<tbody>
		        <c:forEach items="${space.contents}" var="content" varStatus="status">
				<tr id="${content}">
					<td id="actionColumn">
						<div id="actionDiv" class="actions">
							<ul>
								<li><b><a href="content.htm?spaceId=${space.spaceId}&contentId=${content}">View</a></b></li>
								<li><a href="${baseURL}/${space.spaceId}/${content}">Download</a></li>
								<li>
									<a href="<c:url value="removeContent.htm" >
								   		<c:param name="spaceId" value="${space.spaceId}"/>
								   		<c:param name="contentId" value="${content}"/>
\								   		<c:param name="returnTo" value="${returnTo}"/>
								    </c:url>" onclick="return confirmDeleteOperation();">Remove</a>
							   	</li>
							</ul>
						</div>
					</td>
					<td>
						${content}
					</td>
				    <td/>
				    <td/>
				    <td/>
	
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>