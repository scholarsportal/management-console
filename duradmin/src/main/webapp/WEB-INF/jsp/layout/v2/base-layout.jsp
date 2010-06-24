<%@include file="/WEB-INF/jsp/include.jsp" %>
<div id="page-header" class="outer">
	<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/main.js"></script>
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/style/base.css" type="text/css" />
	<!-- page level header extensions 
	  -- reserved for pages that wish to inject page specific scripts into the header
	  -->
	<tiles:insertAttribute name="header-extensions" ignore="true"/>
		
</head>
<body>
	<div id="page-header">
		<div id="header-center">
			<div id="dc-logo-panel"> 
				<img id="dc-logo" alt="[Duracloud Logo]" src="http://www.duracloud.org"/>
			</div>
			<div id="dc-tabs-panel">
				<ul class="horizontal-list dc-main-tabs">
					<li><a href="">Dashboard</a></li>
					<li class="selected"><a href="">Spaces</a></li>
					<li><a href="">Services</a></li>
				</ul>
			</div>
		</div>	
		<div id="header-east">
			<div style="float:right; padding-left:1em; border-left:2px solid #DDD">
				<img id="dc-partner-logo" alt="[Partner Logo]" src="http://www.duracloud.org"/>
			</div>			
			<div style="float:right; padding-right:1em;">
				<div>
					Charles Stross, Administrator <input type="button" value=">"/>		
				</div>
				<div style="text-align:right">
					<ul class="horizontal-list">
						<li><a href="">Help</a></li>
						<li><a href="">Logout</a></li>
					</ul>		
				</div>
			</div>
			
		</div>
	</div>	
	<div id="right" class="float-r">
		<img class="float-r" id="dc-partner-logo" src="/duradmin/images/partner_logo_nypl.png"/>
		<div id="dc-user" class="float-r">
			${pageContext.request.userPrincipal.name}
			<ul class="horizontal-list">
				<li id="help"><a class="flex icon-link" href="#"><i class="pre help">Help</i></a></li>
	            <li>
		            <a class="flex icon-link" href='<c:url value="/logout"/>' class="logout"><i class="pre logoff">Logout</i></a>
	            </li>
			</ul>		
		</div>			
	</div>
</div>
<div id="page-content" class="pane-L1-body">
 	<tiles:insertAttribute name="main-content" />
</div>
<div class="ui-layout-south footer">
	 <tiles:insertAttribute name="main-footer" />
	<div class="outer" id="footer-content">
		<div class="float-r" id="logo-ds"></div>
		Duracloud Administrator Release 0.4  <span class="sep">|</span>
		©<script type="text/javascript">document.write(new Date().getFullYear());</script>
		<a target="_blank" href="http://www.duraspace.org">DuraSpace.org</a>  <span class="sep">|</span>
		<a target="_blank" href="http://www.duracloud.org">Duracloud.org</a>  <span class="sep">|</span> 
		<a target="_blank" href="#">Contact Us</a>
	</div>

</div>	

<div id="loading-message" style="display:none">
Loading...
</div>
	