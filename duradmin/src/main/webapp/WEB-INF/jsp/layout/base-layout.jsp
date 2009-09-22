<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="style/main.css" />
    <title><tiles:insertAttribute name="title"  /></title>
  </head>
  
  <body>
    <div id="header"><tiles:insertAttribute name="header"/></div>
   	<table width="100%" cellpadding="0" cellspacing="0">
   		<tr>
   			<td id="menu">
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