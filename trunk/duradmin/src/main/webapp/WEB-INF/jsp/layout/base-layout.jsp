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
	    djConfig="parseOnLoad:true, isDebug:false, debugAtAllCosts: false">
	</script>
	<!-- load the duradmin base -->
	<!-- script type="text/javascript" src="${pageContext.request.contextPath}/script/common/base.js"></script-->
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/dojox/widget/Toaster/Toaster.css"/>
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/dijit/themes/tundra/tundra.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/main.css" type="text/css" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/menu.css" type="text/css" />

    <title>
    	<spring:message code="application.title" /> :: <tiles:insertAttribute name="title"/>
    </title>
    
  </head>
	<script type="text/javascript">
	dojo.require("dojox.widget.Toaster");
	dojo.require("duracloud._base");


		/*zebra stripe standard tables css 2 doesn't support nth-child*/	
		dojo.addOnLoad(function(){
			dojo.query(".standard > tbody > tr:nth-child(odd)").addClass("evenRow");
			dojo.query(".extended-metadata tr:nth-child(even)").addClass("evenRow");
		});

		dojo.addOnLoad(function(){
			//adds mouse listeners on spaces table rows
			//will throw an error if spaces table is not found
			try{
				
			dojo.query(".actionable-item",document).forEach(
				    function(item) {
				    	dojo.connect(item, 'onmouseover', function() {
							dojo.query(".actions",item).attr('style', {visibility:'visible'});
				    	});
			
			           	dojo.connect(item, 'onmouseout', 
							function(){
								dojo.query(".actions",item).attr('style', {visibility:'hidden'});
			      		});
				    }
				);	
			
			
			}catch(err){
			}
		
			dojo.query(".delete-action",document).forEach(
				    function(item) {
				    	dojo.connect(item, 'onclick', function(e) {
				    		if(!confirmDeleteOperation()){
				    			e.stopPropagation();
				    			e.preventDefault();
				    			dojo.stopEvent(e);
				    		}
				    	});
				    });	
			
		});
	</script>
  <body class="tundra">
  <div dojoType="dojox.widget.Toaster" id="toaster1" positionDirection="tr-left">
  </div>
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