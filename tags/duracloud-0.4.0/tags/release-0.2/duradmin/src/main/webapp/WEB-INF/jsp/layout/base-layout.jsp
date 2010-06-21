<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- load the dojo toolkit base -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/dojo/dojo.js"
	    djConfig="parseOnLoad:true, isDebug:false"></script>

	<!-- load the duradmin base -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/common/base.js"></script>
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/dijit/themes/tundra/tundra.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/main.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/menu.css" type="text/css" />
    <title>
    	<spring:message code="application.title" /> :: <tiles:insertAttribute name="title"/>
    </title>
  </head>
  <body class="tundra">
	<tiles:importAttribute name="mainTab" scope="request" />
   	
    <div id="header"><tiles:insertAttribute name="header"/></div>
    <table class="body" >
   		<tr>
   			<td id="menu-div">
			    <tiles:insertAttribute name="menu"/>
   			</td>
   			<td id="main-content">
				
			    <tiles:insertAttribute name="main-content" />
   			</td>

   			<td id="help">
				<tiles:insertAttribute name="help"/>   				
   			</td>

   		</tr>
   	</table>
   	
    <div id="footer"><tiles:insertAttribute name="footer" /></div>
  </body>
</html>