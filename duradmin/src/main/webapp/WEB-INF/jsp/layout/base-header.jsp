<%@include file="/WEB-INF/jsp/include.jsp" %>
<div id="top-header">
	<img alt="<spring:message code="application.title"/>" height="25px" src="images/duraspace-logo.jpg">
	<strong>
		<spring:message code="application.title"/>: 
	</strong>
	<spring:message code="application.tagline"/>
</div>

<tiles:importAttribute name="mainMenu" />

<div id="main-menu">
	<ul id="menu">
		<c:forEach items="${mainMenu}" var="mi">
			<li>
				<a href='<c:url value="${mi.href}" />' class="<c:if test="${mi.name == mainTab}">current</c:if>" >
					<spring:message code="${mi.messageKey}"/>
				</a>
			</li>
		</c:forEach>
	</ul>
</div>

