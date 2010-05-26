<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base" >
	<tiles:putAttribute name="title">
		<spring:message code="services" />	
	</tiles:putAttribute>

	<tiles:putAttribute name="header-extensions">
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.selectablelist.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.expandopanel.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/services-manager.js"></script>
	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab">services</tiles:putAttribute>

		<tiles:putAttribute name="main-content">
	   		<div id="services-list-view" class="list-browser dc-list-item-viewer">
				<div class="north header list-header clearfix">
					<div id="header-spaces-list" class="header-section clearfix">						
						<a class="flex button float-r deploy-service-button" href="javascript:void(1);"><span><i class="pre plus">Deploy a New Service</i></span></a>
						<h2>Services</h2>
					</div>
				</div>
			
				<div class="center dc-item-list-wrapper">
					<div class="dc-item-list">
						<table>
							<thead>
								<tr>
									<th>&nbsp;</th>
									<th>Service</th>
									<th>Hostname</th>
									<th>Status</th>
								</tr>
							</thead>
							<tbody>
								<tr class="dc-item service-replicate">
									<td class="icon"><div></div></td>
									<td>Service #1 - Replicate</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-generalcompute">
									<td class="icon"><div></div></td>
									<td>Service #1 - General compute</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-bitintegrity">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-image">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-video">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>
								<tr class="dc-item service-filetransform">
									<td class="icon"><div></div></td>
									<td>Service #1</td>
									<td>127.0.0.1</td>
									<td>OK</td>
								</tr>	
							</tbody>
						</table>
					</div>			
				</div>
			</div>
			<div id="detail-pane" class="detail-pane" style="padding-top:10px;">
				<div class="north header">
					<h1>Service Detail</h1>
					<h2 class="object-name">Service Name Here</h2>
					<div class="toggle-control flex switch-holder">
					    <div class="r">
					    	<span>
					    		Status
					    	</span>
					    	<span>
					    		[deployed] [undeploy]
					    	</span>
					    </div>
					</div>
		
				</div>
				<div class="center">
				</div>
			</div>	
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



