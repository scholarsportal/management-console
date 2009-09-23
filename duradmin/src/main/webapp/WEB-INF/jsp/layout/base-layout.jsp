<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet"  href="style/menu_style.css" type="text/css" />
	<link rel="stylesheet"  href="style/main.css" type="text/css" />
    
    <title><tiles:insertAttribute name="title"  />
     </title>
  </head>
  
  <body>
    <div id="header"><tiles:insertAttribute name="header"/></div>
    <table class="body" >
   		<tr>
   			<td id="menu-div">
			    <tiles:insertAttribute name="menu"/>
   			</td>
   			<td id="main-content">
			    <tiles:insertAttribute name="main-content" />
   			</td>
   		</tr>
   	</table>
   	
    <div id="footer"><tiles:insertAttribute name="footer" /></div>
  </body>
</html>