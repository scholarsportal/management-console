<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- load the dojo toolkit base -->
	<script type="text/javascript" src="dojo/dojo.js"
	    djConfig="parseOnLoad:true, isDebug:true"></script>

	<!-- load the duradmin base -->
	<script type="text/javascript" src="script/common/base.js"></script>
	<link rel="stylesheet"  href="dijit/themes/tundra/tundra.css" type="text/css" />
	<link rel="stylesheet"  href="style/menu_style.css" type="text/css" />
	<link rel="stylesheet"  href="style/main.css" type="text/css" />
    
    <title>Duradmin :: ${title}</title>
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
   				<div id="main-content-header">
				    ${title}
   				</div>
   				<div id="main-content-body">
				    <tiles:insertAttribute name="main-content" />
   				</div>
   			</td>
   		</tr>
   	</table>
   	
    <div id="footer"><tiles:insertAttribute name="footer" /></div>
  </body>
</html>