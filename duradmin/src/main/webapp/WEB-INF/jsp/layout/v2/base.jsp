<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 
	created by Daniel Bernstein and CH
 -->
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="language" content="en" />
    <title><spring:message code="application.title" /> :: <tiles:insertAttribute name="title"/></title>

	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/base.css" type="text/css" />	
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/flex.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/dialogs.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/buttons.css" type="text/css" />

	<!-- jquery core, ui and css -->
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.js"></script>
	<!-- 3rd party jquery plugins start-->
	<script type="text/javascript" src="http://layout.jquery-dev.net/download/jquery.layout.min-1.2.0.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/jquery/plugins/jquery.ba-throttle-debounce/jquery.ba-throttle-debounce.min.js"></script>

	<script type="text/javascript" src="http://ajax.microsoft.com/ajax/jquery.validate/1.7/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/plugins/jquery.form/jquery.form-2.4.3.js"></script>

	<!-- 3rd party jquery plugins end-->
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/jquery.dc.common.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.glasspane.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.onoffswitch.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.selectablelist.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.listdetailviewer.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.expandopanel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/durastore-api.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/main.js"></script>

	<!-- page level header extensions reserved for pages that wish to inject page specific scripts into the header -->
	<tiles:insertAttribute name="header-extensions" ignore="true"/>
		
</head>
<body>
	<tiles:insertAttribute name="body"/>
</body>
</html>
