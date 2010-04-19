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
	    djConfig="parseOnLoad:true, isDebug:false, debugAtAllCosts:false">

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
  <body class="tundra" style="width: 100%; height: 100%;">
	<script type="text/javascript">
	dojo.require("dijit.layout.BorderContainer");
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dojox.widget.Toaster");
	dojo.require("duracloud._base");
	dojo.require("duracloud.ui");
	dojo.require("duracloud.storage");
	dojo.require("dojo.cookie");
		

		dojo.addOnLoad(function(){
			duracloud.storage.init();
			var expireContentItem = function(e){
					var contentId = dojo.attr(e.target,"contentId");
					var spaceId = dojo.attr(e.target,"spaceId");
					duracloud.storage.expireContentItem(spaceId,contentId);
				};
				
			var expireSpace = function(e){
					var spaceId = dojo.attr(e.target,"spaceId");
					duracloud.storage.expireSpace(spaceId);
				};
			
			var contentItemClick = function(e){
				expireSpace(e);
				duracloud.ui.showWait("Working...");
			};	

			dojo.query(".logout").forEach(function(node){
				dojo.connect(node, "onclick", function(){
					duracloud.storage.clear();
				});
			});
			dojo.query(".blocking-action").forEach(function(node){
				dojo.connect(node, "onclick", contentItemClick);
			});
	
			dojo.query(".update-space").forEach(function(node){
				dojo.connect(node, "onclick", expireSpace);
			});
			
			//content item removed
			dojo.query(".remove-content-item").forEach(function(node){
				dojo.connect(node, "onclick", expireContentItem);
			});
	
			//content item updated
			dojo.query(".update-content-item").forEach(function(node){
				dojo.connect(node, "onclick", expireContentItem);
			});
		
			//space removed
			dojo.query(".remove-space").forEach(function(node){
				dojo.connect(node, "onclick", expireSpace);
			});

		/*zebra stripe standard tables css 2 doesn't support nth-child*/	
			dojo.query(".standard > tbody > tr:nth-child(odd)").addClass("evenRow");
			dojo.query(".extended-metadata tr:nth-child(even)").addClass("evenRow");

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

	  <div dojoType="dojox.widget.Toaster" id="toaster1" positionDirection="tr-left">
	  </div>
	<tiles:importAttribute name="mainTab" scope="request" />
   	
    
   	<div id="main" dojoType="dijit.layout.BorderContainer" gutters="false" style="width: 100%; height: 100%;">
   	    <div id="header" dojoType="dijit.layout.ContentPane" region="top" splitter="false">
			<tiles:insertAttribute name="header"/>
		</div>

   	    <div id="menu-div" dojoType="dijit.layout.ContentPane" region="left" splitter="false">
			    <tiles:insertAttribute name="menu"/>
		</div>

   	    <div id="main-content" dojoType="dijit.layout.ContentPane" region="center" splitter="false">
			    <tiles:insertAttribute name="main-content" />

		</div>
   	    <div id="help" dojoType="dijit.layout.ContentPane" region="right" splitter="false">
				<tiles:insertAttribute name="help"/>   				
		</div>
   	    <div id="footer" dojoType="dijit.layout.ContentPane" region="bottom" splitter="false">
			<tiles:insertAttribute name="footer" />
		</div>
    </div>
  </body>
</html>
