<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab">dashboard</tiles:putAttribute>

		<tiles:putAttribute name="main-content">
		<div>
		Dashboard
		</div>
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



