<%@include file="/WEB-INF/jsp/include.jsp" %>
<div id="top-header">
	<img alt="<spring:message code="application.title"/>" height="25px" src="images/duraspace-logo.jpg">

	<strong>
		<spring:message code="application.title"/>: 
	</strong>
	 <spring:message code="application.tagline"/>
	 
</div>

<div id="main-menu">
	
	<ul id="menu">
		<li>
			<a href='<c:url value="/" />' ><spring:message code="general.home"/></a>
		</li>
		<li>
			<a href='<c:url value="/spaces.htm" />'><spring:message code="general.spaces"/></a>
		</li>
		<li>
			<a href='<c:url value="/services.htm" />'><spring:message code="general.services"/></a>
		</li>
	</ul>
</div>

