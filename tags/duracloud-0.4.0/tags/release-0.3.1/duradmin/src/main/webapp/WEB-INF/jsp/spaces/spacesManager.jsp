<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<!-- 
	created by Daniel Bernstein
 -->
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="language" content="en" />
	<title>Duracloud Spaces Manager</title>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.js"></script>
	<script type="text/javascript" src="http://layout.jquery-dev.net/download/jquery.layout.min-1.2.0.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/main.js"></script>
	<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/base/jquery-ui.css" type="text/css" media="all" />
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/base.css" type="text/css" />
</head>
<body>
	<div class="page-header">
		<div class="north-center"> 
			<img id="dc-logo" alt="[Duracloud Logo]" src="http://www.duracloud.org"/>
			<img id="dc-partner-logo" alt="[Partner Logo]" src="http://www.duracloud.org"/>
		</div>
		<div class="north-south">
			<ul class="horizontal-list" style="float:left">
				<li><a href="">Dashboard</a></li>
				<li class="selected"><a href="">Spaces</a></li>
				<li><a href="">Services</a></li>
			</ul>
	
			<ul class="horizontal-list" style="float:right">
				<li><a href="">Help</a></li>
				<li><a href="">Logout</a></li>
			</ul>		
			<span style="float:right">
				Charles Stross, Administrator <input type="button" value=">"/>		
			</span>			
		</div>
	</div>
	
	<div id="page-content">
		<div class="center-north">
			<div style="float:left">
				<label for="providerList">Provider</label>
				<select name="providerList"><option>Amazon S3</option>
				</select>
			</div>
	
			<div style="float:left">
	        	<ul class="horizontal-list">
	        		<li>
			        	<input type="button" value="Browse"/>
			    	</li>
		       		<li>
			        	<input type="button" value="Search"/>
			    	</li>
	        	</ul>
	        </div>
	        <!-- 
	        FIX ME  - right padding seems to be necessary to render properly in firefox
	        			without it, there is an ugly horizontal scroll for no apparent 
	        			reason. -db
	        
	         -->
			<div style="float:right;padding-right:0.25em" >
				<span>
				4 Items
				<input type="button" value="+"/>   			
				<input type="button" value="^"/>   	
				</span>
	   					
	   		</div>
	   	</div>
	   	<div id="list-browser">
			<div id="spaces-list-view">
				<div class="north">
					<h3 class="header">
						Spaces
						<input style="float:right" class="add-space-button" type="button" value="Add Space"/>
					</h3>
					<div class="header">
						<span style="float:left"><input type="checkbox"/> prev next</span> <span style="float:right"><input type="text"/></span>
					</div>
				</div>
			
				<div class="center">
					<div class="dc-item-list" id="spacesList">
						<div class="dc-item">
							<input type="checkbox"/>space item name here
							<div class="dc-action-panel">
								<input type="button" value="+"/>
								<input type="button" value="-"/>
							</div>
						</div>
						<div class="dc-item">
							<input type="checkbox"/>space item name here
							<div class="dc-action-panel">
								<input type="button" value="+"/>
								<input type="button" value="-"/>
							</div>
						</div>
		
						<div class="dc-item">
							<input type="checkbox"/>space item name here
							<div class="dc-action-panel">
								<input type="button" value="+"/>
								<input type="button" value="-"/>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="content-item-list-view">
				<div class="north">
					<h3 class="header">
						Content Items
						<input style="float:right" class="add-content-item-button" type="button" value="Add Content Item"/>
					</h3>
					<div class="header" >
						<span style="float:left"><input type="checkbox"/> prev next</span> <span style="float:right"><input type="text"/></span>
					</div>
				</div>
			
				<div class="center">
					<div class="dc-item-list" id="contentItemList">
						<div class="dc-item">
							<input type="checkbox"/>content item name here
							<div class="dc-action-panel">
								<input type="button" value="+"/>
								<input type="button" value="-"/>
							</div>
						</div>
					</div>
				</div>
			</div>
	   	</div>
		<div id="detail-pane">
		</div>
	</div>
	
	<!-- 
	Space Detail Pane:  The div is invisible and used as a prototype for displaying specific space
						details.
	 -->
	<div id="spaceDetailPane" style="display:none">
		<div class="north">
			<h3 class="header"><img src="xxx" height="25" width="25" style="background-color:#DDD"/> Space Detail</h3>
			<h5 class="header object-name">Space Name Here</h5>
			<div class="header toggle-control">
				Access: 
				<input type="button" value="Open"/>
				<input type="button" value="Close"/>
			</div>
			<div class="header button-bar">
				<ul class="horizontal-list">
					<li>
						<input class="add-content-item-button" type="button" value="Add Content"/>
					</li>
					<li>
						<input type="button" value="Delete"/>
					</li>
				</ul>
			</div>

		</div>
		<div class="center">
			<div class="dc-expandable-panel">
					<h4>Details</h4>
					<table>
						<tr>
							<td>
								Items
							</td>
							<td>
								<span id="space-item-count">3</span>
							</td>
						</tr>
						<tr>
							<td>
								Created
							</td>
							<td>
								<span id="space-created-date">Fri, 09 Apr 2010 23:13:00 UTC</span>
							</td>
						</tr>
					</table>
			</div>
			<div id="metadata-panel" class="dc-expandable-panel">
					<h4>Metadata</h4>
					<table  width="100%">
						<tr class="dc-mouse-panel-activator">
							<td class="name">
								Lorem
							</td>
							<td class="value">
								Ipsum dolor								
								<span class="dc-mouse-panel">
									<input type="button"  value="x"/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="name">
								<input type="text" value="" size="15"/>
							</td>
							<td class="value">
								<input type="text" value="" size="20"/>
								<input type="button" value="+"/>
							</td>
						</tr>
					</table>
			</div>
			<div id="tag-panel" class="dc-expandable-panel">
					<h4>Tags</h4>
					<table  width="100%">
						<tr>
							<td>
								<ul class="horizontal-list">
									<li class="dc-mouse-panel-activator">
										anicca
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									<li class="dc-mouse-panel-activator">
										dukkha
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									<li class="dc-mouse-panel-activator">
										anatta
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									
								</ul>
							</td>
						</tr>
						<tr>
							<td>
								<input type="text" value="" size="35"/>
								<input type="button" value="+"/>
							</td>
						</tr>
					</table>
			</div>

		</div>
	</div>

	<!-- 
		an invisible  prototype for content items details.
	 -->
	<div id="contentItemDetailPane" style="display:none" >
		<div class="north">
			<h3 class="header"><img src="xxx" height="25" width="25" style="background-color:#DDD"/> Content Detail</h3>
			<h5 class="header object-name">Content Name</h5>
			<div class="header">
				<img src="xxx" height="50" width="50"/><span>Mime Type:</span><span>image/jpg</span>
			</div>
			<div class="header button-bar">
				<ul class="horizontal-list">
					<li>
						<input type="button" value="Edit"/>
					</li>
					<li>
						<input type="button" value="Download"/>
					</li>
					<li>
						<input type="button" value="Delete"/>
					</li>
				</ul>
			</div>

		</div>
		<div class="center">
			<div class="dc-expandable-panel">
					<h4>Preview</h4>
					<div style="text-align:center">
						<a href="xxx">
							<img src="xxx" height="200" width="200"/>					
						</a>
					</div>

			</div>
			<div class="dc-expandable-panel">
					<h4>Details</h4>
					<table>
						<tr>
							<td>
								Items
							</td>
							<td>
								<span id="space-item-count">3</span>
							</td>
						</tr>
						<tr>
							<td>
								Created
							</td>
							<td>
								<span id="space-created-date">Fri, 09 Apr 2010 23:13:00 UTC</span>
							</td>
						</tr>
					</table>
			</div>
			<div id="metadata-panel" class="dc-expandable-panel">
					<h4>Metadata</h4>
					<table  width="100%">
						<tr class="dc-mouse-panel-activator">
							<td class="name">
								Lorem
							</td>
							<td class="value">
								Ipsum dolor								
								<span class="dc-mouse-panel">
									<input type="button"  value="x"/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="name">
								<input type="text" value="" size="15"/>
							</td>
							<td class="value">
								<input type="text" value="" size="20"/>
								<input type="button" value="+"/>
							</td>
						</tr>
					</table>
			</div>
			<div id="tag-panel" class="dc-expandable-panel">
					<h4>Tags</h4>
					<table  width="100%">
						<tr>
							<td>
								<ul class="horizontal-list">
									<li class="dc-mouse-panel-activator">
										anicca
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									<li class="dc-mouse-panel-activator">
										dukkha
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									<li class="dc-mouse-panel-activator">
										anatta
										<span class="dc-mouse-panel">
											<input type="button"  value="x"/>
										</span>
									</li>
									
								</ul>
							</td>
						</tr>
						<tr>
							<td>
								<input type="text" value="" size="35"/>
								<input type="button" value="+"/>
							</td>
						</tr>
					</table>
			</div>

		</div>
	</div>
	
	<div class="ui-layout-south">
		Footer
	</div>	

	<div id="add-space-dialog" class="dc-dialog" title="Add Space">
		<p class="validateTips">All form fields are required.</p>
		<form>
		<fieldset>
			<label for="name">Name</label>
			<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
		</fieldset>
		</form>

	</div>

	<div id="add-content-item-dialog" class="dc-dialog" title="Add Content Item">
		<form>
		<fieldset>
			<label for="name">Item Name</label>
			<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
		</fieldset>
		</form>

	</div>
	
</body>
</html>