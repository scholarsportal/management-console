<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="app-base">
	<tiles:putAttribute name="title">
		<spring:message code="services" />
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/javascript/dashboard-manager.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/jquery/plugins/jquery.jfeed/jquery.jfeed.pack.js"></script>

	
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
		<tiles:insertDefinition name="app-frame">
			<tiles:putAttribute name="mainTab">dashboard</tiles:putAttribute>

			<tiles:putAttribute name="main-content">
				<div class="center-north" id="center-pane-north">
					<div class="float-l"><h1>Welcome</h1></div>
				</div>
				<div id="dynamic-panel">
					<div id="announcements"></div>
					<div id="current-users">
						
					</div>
				</div>
				<div id="static-panel">
						<h2>Getting Started with Duracloud</h2>
						<p>
						DuraCloud is particularly focused on providing preservation support services and access services for academic libraries, academic research centers, and other cultural heritage organizations.
						</p>
						<p>
						The service builds on the pure storage from expert storage providers by overlaying the access functionality and preservation support tools that are essential to ensuring long-term access and durability. DuraCloud offers cloud storage across multiple commercial and non commercial providers, and offers compute services that are key to unlocking the value of digital content stored in the cloud. DuraCloud provides services that enable digital preservation, data access, transformation, and data sharing. Customers are offered "elastic capacity" coupled with a "pay as you go" approach. DuraCloud is appropriate for individuals, single institutions, or for multiple organizations that want to use cross-institutional infrastructure.
						</p>
						<p>DuraCloud has been in a pilot phase since the beginning of Fall 2009 and will be released as a service of the DuraSpace not-for-profit organization in the fall of 2010.
						</p>
						
				</div>
			</tiles:putAttribute>
			<tiles:putAttribute name="main-footer">
				<div id="status-holder">
				</div>
			</tiles:putAttribute>
			</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>



