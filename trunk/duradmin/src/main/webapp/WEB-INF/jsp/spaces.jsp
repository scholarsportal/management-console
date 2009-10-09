<%@include file="/WEB-INF/jsp/include.jsp"%>
<c:if test="${not empty error}">
	<div id="error"><c:out value="${error}" /></div>
</c:if>

<div class="spaces">
<table class="standard" id="spacesTable">
	<tr>
		<th></th>
		<th><a href="spaces.htm?sortField=spaceId&asc=true">Space Id</a></th>
		<th>Items</th>
		<th>Access</th>
		<th>Created</th>

	</tr>
	<tbody>
		<c:forEach items="${spaces}" var="space">
			<tr id="${space.spaceId}">
				<td id="actionColumn">
				<div id="actionDiv" class="actions">
				<ul>
					<li><b><a href="contents.htm?spaceId=${space.spaceId}">View</a></b> |
					</li>
					<li><a href="spaceDelete.action?spaceId=${space.spaceId}"
						onclick="return confirmDeleteOperation();">Delete</a></li>
					</li>
				</ul>
				</div>
				</td>
				<td><c:out value="${space.spaceId}" /></td>
				<td><c:out value="${space.metadata.count}" /></td>
				<td><c:out value="${space.metadata.access}" /></td>
				<td><c:out value="${space.metadata.created}" /></td>

			</tr>


		</c:forEach>
	</tbody>

</table>
</div>
