<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab">services</tiles:putAttribute>

		<tiles:putAttribute name="main-content">
		<div>
		Services
		</div>
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



