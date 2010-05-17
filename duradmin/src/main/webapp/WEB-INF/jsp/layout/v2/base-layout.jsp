<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<!-- 
	created by Daniel Bernstein and CH
 -->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="language" content="en" />
    <title><spring:message code="application.title" /> :: <tiles:insertAttribute name="title"/></title>
	<!-- jquery core, ui and css -->
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.js"></script>
	<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/base/jquery-ui.css" type="text/css" media="all" />

	<!-- 3rd party jquery plugins start-->
	<script type="text/javascript" src="http://layout.jquery-dev.net/download/jquery.layout.min-1.2.0.js"></script>
	<!-- 3rd party jquery plugins start-->

	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/main.js"></script>
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/flex.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/base.css" type="text/css" />
	<!-- page level header extensions reserved for pages that wish to inject page specific scripts into the header -->
	<tiles:insertAttribute name="header-extensions" ignore="true"/>
		
</head>
<body>
	<div id="page-header" class="outer">	
		<div id="left" class="float-l">
			<div id="dc-logo-panel"><a href="/duradmin/spaces" id="dc-logo"></a><span id="dc-app-title"></span></div>
			<div id="dc-tabs-panel">
			    <ul class="horizontal-list dc-main-tabs flex clearfix">
			        <li><a href="javascript:void(1); alert('Dashboard click')"><span>Dashboard</span></a></li>
			        <li class="selected"><a href="javascript:void(1); alert('Spaces click')"><span>Spaces</span></a></li>
			        <li><a href="javascript:void(1); alert('Services click')"><span>Services</span></a></li>
			        <li><a href="javascript:void(1); alert('Reports click')"><span>Reports</span></a></li>
			    </ul>
			</div>
		</div>	
		<div id="right" class="float-r">
			<img id="dc-partner-logo" src="/duradmin/images/partner_logo_nypl.png"/>
			<div id="dc-user" class="float-r">
				Charles Stross, Administrator
				<ul class="horizontal-list">
					<li id="help"><a href="">Help</a></li>
					<li id="logout"><a href="">Logout</a></li>
				</ul>		
			</div>			
		</div>
	</div>
	
	<div id="page-content">
	 	<tiles:insertAttribute name="main-content" />
	</div>
	<div class="ui-layout-south outer">
		Duracloud http://www.duracloud.org  Duraspace http://www.duraspace.org
	</div>	

	
</body>
</html>
