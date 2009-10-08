<%@include file="/WEB-INF/jsp/include.jsp"%>
<c:if test="${not empty error}">
	<div id="error"><c:out value="${error}" /></div>
</c:if>

<div class="spaces">
<table class="standard" id="spacesTable">
	<tr>
		<th>ID</th>
		<th>Created</th>
		<th>Items</th>
		<th>Access</th>
		<th></th>
	</tr>
	<tbody>
		<c:forEach items="${spaces}" var="space">
			<tr id="${space.spaceId}">
				<td><c:out value="${space.spaceId}" /></td>
				<td><c:out value="${space.metadata.created}" /></td>
				<td><c:out value="${space.metadata.count}" /></td>
				<td><c:out value="${space.metadata.access}" /></td>
				<td id="actionColumn">
				<div id="actionDiv" class="actions">
				<ul>
					<li><a href="contents.htm?spaceId=${space.spaceId}">View</a> |
					</li>
					<li><a href="spaceDelete.action?spaceId=${space.spaceId}"
						onclick="return confirmDeleteOperation();">Delete</a></li>
					</li>
				</ul>
				</div>
				</td>
			</tr>


		</c:forEach>
	</tbody>

</table>
</div>
