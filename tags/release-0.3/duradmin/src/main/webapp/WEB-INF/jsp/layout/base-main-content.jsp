<%@include file="/WEB-INF/jsp/include.jsp"%>

   	<div dojoType="dijit.layout.BorderContainer" gutters="false" style="width: 100%; height: 100%;">
   	    <div class="main-content-header" dojoType="dijit.layout.ContentPane" region="top" splitter="false">
			<tiles:insertAttribute name="header"/>
		</div>
   	    <div class="main-content-body" dojoType="dijit.layout.ContentPane" region="center" splitter="false">
			<tiles:insertAttribute name="body"/>
		</div>
	</div>
