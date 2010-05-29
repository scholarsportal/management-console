<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="app-base">
	<tiles:putAttribute name="title">
		<spring:message code="spaces" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="header-extensions">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery/plugins/jquery.fancybox-1.3.1/fancybox/jquery.fancybox-1.3.1.css" type="text/css" media="screen" />
		<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/plugins/jquery.fancybox-1.3.1/fancybox/jquery.fancybox-1.3.1.pack.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/plugins/jquery.fancybox-1.3.1/fancybox/jquery.easing-1.3.pack.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.selectablelist.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.expandopanel.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.onoffswitch.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.metadataviewer.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.tagsviewer.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/ui.flyoutselect.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/spaces-manager.js"></script>

	</tiles:putAttribute>
	<tiles:putAttribute name="body">
	<tiles:insertDefinition name="app-frame">
		<tiles:putAttribute name="mainTab" value="spaces" />
		<tiles:putAttribute name="main-content">
			<div class="center-north" id="center-pane-north">
				<div class="float-l">
						Provider:
						<span id="provider-select-box" class="provider-widget">
						</span>
						
						<!-- 
						<span id="select-provider-wrapper">
							<a class="flex button" id="select-provider" href="#"><span><i class="post arw-down-liteblue">AAAA</i></span></a>
							<ul>							
								<li class="first-of-type"><a href="?storeId=0">Amazon S3</a></li>	
								<li><a href="?storeId=1">Rackspace</a></li>								
								<li><a href="?storeId=2">AAAA Putcha Data Here An' Fuhget About It Storage</a></li>
							</ul>
						</span>
						 -->
						
				</div>
		
				<!-- <div class="float-l">
		        	<ul>
		        		<li class="row clearfix">
				        	<input type="button" value="Browse"/>
				    	</li>
			       		<li class="row clearfix">
				        	<input type="button" value="Search"/>
				    	</li>
		        	</ul>
		        </div> -->
	
				<div class="float-r" id="pinned">
					<span>
					4 Items
					<input type="button" value="+"/>   			
					<input type="button" value="^"/>   	
					</span>	   					
		   		</div>
		   	</div>
		   	<div id="list-browser" class="list-browser">
		   		<div id="spaces-list-view" class="dc-list-item-viewer">
					<div class="north header list-header clearfix">
						<div id="header-spaces-list" class="header-section clearfix">						
							<a class="flex button float-r add-space-button" href="javascript:void(1);"><span><i class="pre plus">Add Space</i></span></a>
							<h2>Spaces</h2>
						</div>
						<div class="header-section">
							<span class="float-r"><input class="dc-item-list-filter "  value="filter" type="text"/></span>
							<input id="check-all-spaces" class="dc-check-all" type="checkbox"/>
						</div>
					</div>
				
					<div class="center dc-item-list-wrapper">
						<div class="dc-item-list" id="spaces-list">
						</div>
					</div>
				</div>
				<div id="content-item-list-view" class="dc-list-item-viewer">
					<div class="north header list-header clearfix">
						<div id="header-content-list" class="header-section clearfix">
							<a class="flex button float-r add-content-item-button" href="javascript:void(1);"><span><i class="pre plus">Add Content Item</i></span></a>
							<h2>Content Items</h2>
						</div>
						<div class="header-section" >
							<span class="float-r"><input class="dc-item-list-filter" value="filter" type="text"/></span>
							<input id="check-all-content-items" class="dc-check-all" type="checkbox"/> prev next
						</div>
					</div>
				
					<div class="center dc-item-list-wrapper">
						<div class="dc-item-list" id="content-item-list">
						</div>
					</div>
				</div>
		   	</div>
			<div id="detail-pane" class="detail-pane">
			
			
			</div>	
		
		<!-- 
		Space Detail Pane:  The div is invisible and used as a prototype for displaying specific space
							details.
		 -->
		<div id="spaceDetailPane" class="dc-detail-pane" style="display:none">
			<div class="north header">
				<h1>Space Detail</h1>
				<h2 class="object-name">Space Name Here</h2>
				<div class="toggle-control flex switch-holder">
				    <div class="r access-switch"></div>
				</div>
				<div class="button-bar">
					<a href="javascript:void(1);" class="flex button add-content-item-button"><span><i class="pre plus">Add Content Item</i></span></a>
					<a href="javascript:void(1);" class="flex button std delete-space-button"><span><i class="pre trash">Delete Space</i></span></a>
				</div>
	
			</div>
			<div class="center">
			</div>
	
		</div>
	
		<div id="genericDetailPane" style="display:none">
			<div class="north header"></div>
			<div class="center"></div>
		</div>
	
		<div id="spaceMultiSelectPane" style="display:none">
			<div class="north">
				Editing multiple spaces is coming soon!

			</div>
			<div class="center">
			</div>
		</div>
	
		<div id="contentItemMultiSelectPane" style="display:none">
			<div class="north header">
				Editing multiple content items is coming soon!
			</div>
			<div class="center">
			</div>
		</div>
	
		<!-- 
			an invisible  prototype for content items details.
		 -->
		<div id="contentItemDetailPane" style="display:none" >
			<div class="north header">
				<h1>Content Detail</h1>
				<h2 class="object-name">Content Name Here</h2>
				<div class="mime-type mime-type-image" id="mime-image">
					<span class="label">Mime Type:</span> <span class="value">image/jpg</span>
				</div>
				
				<div class="button-bar">
					<a href="javascript:void(1);" class="flex button"><span><i class="pre pencil">Edit</i></span></a>
					<a href="javascript:void(1);" class="flex button"><span><i class="pre download">Download</i></span></a>
					<a href="javascript:void(1);" class="flex button std"><span><i class="pre trash">Delete</i></span></a>
				</div>
			</div>
			<div class="center">
	
			</div>
		</div>
		
		<div id="add-space-dialog" class="" title="Add Space">
			<h1>Add Space</h1>
			<p class="hint">Add a Space to the current provider. All fields are required.</p>
			<form>
				<div id="form-fields" class="form-fields">
					<fieldset>
						<ul>
							<li class="row clearfix first-of-type"><label for="name">Name</label><input type="text" name="name" id="name" class="field" /></li>
							<li class="row clearfix"><label for="access">Access</label><span name="access" class="access-switch">access control here</span></li>
							<input type="hidden" name="access" id="access"/>
						</ul>
					</fieldset>
				</div>
			</form>
	
		</div>
	
		<div id="add-content-item-dialog" class="dialog" title="Add Content Item">
			<h1>Add Content Item</h1>
			<p class="hint">Add a Content Item to the currently selected Space. All fields are required.</p>
			<form enctype="multipart/form-data">
				<div id="form-fields" class="form-fields">
					<fieldset>
						<ul>
						<li class="row clearfix first-of-type"><label for="name">Item Name</label><input type="text" name="name" id="name" class="field" /></li>
						<li class="row clearfix"><label for="mimetype">Mime Type</label><input type="text" name="mimetype" id="mimetype" class="field" /></li>
						<li class="row clearfix"><label for="file">File</label><input  type="file" name="file" id="file" class="field"/></li>
						</ul>
					</fieldset>
				</div>
			</form>	
		</div>
		</tiles:putAttribute>
		
</tiles:insertDefinition>	
	</tiles:putAttribute>
	
</tiles:insertDefinition>



