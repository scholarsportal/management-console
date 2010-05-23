<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page session="false"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<!-- 
	created by Daniel Bernstein and CH
 -->

<div id="page-header" class="outer">	

	<div id="left" class="float-l">
		<div id="dc-logo-panel"><a href="/duradmin/spaces" id="dc-logo"></a><span id="dc-app-title"></span></div>
		<div id="dc-tabs-panel">
		    <ul class="horizontal-list dc-main-tabs flex clearfix">
		    	<tiles:importAttribute name="mainTab" />
		        <li class="${mainTab == 'dashboard' ? 'selected':'' }"><a href="${pageContext.request.contextPath}/dashboard"><span>Dashboard</span></a></li>
		        <li class="${mainTab == 'spaces' ? 'selected':'' }"><a href="${pageContext.request.contextPath}/spaces"><span>Spaces</span></a></li>
		        <li class="${mainTab == 'services' ? 'selected':'' }"><a href="${pageContext.request.contextPath}/services"><span>Services</span></a></li>
		        <li class="${mainTab == 'reports' ? 'selected':'' }"><a href="javascript:void(1); alert('Reports click')"><span>Reports</span></a></li>
		    </ul>
		</div>
	</div>	
	<div id="right" class="float-r">
		<img class="float-r" id="dc-partner-logo" src="/duradmin/images/partner_logo_nypl.png"/>
		<div id="dc-user" class="float-r">
			${pageContext.request.userPrincipal.name}
			<ul class="horizontal-list">
				<li id="help"><a href="">Help</a></li>
	            <li>
		            <a href='<c:url value="/logout"/>' class="logout">Logout</a>
	            </li>
			</ul>		
		</div>			
	</div>
</div>

<div id="page-content" class="pane-L1-body">
 	<tiles:insertAttribute name="main-content" />
</div>
<div class="ui-layout-south outer footer">
	<div class="float-r" id="logo-ds"></div>
	<div class="footer-content">
		Duracloud Administrator Release 0.4  <span class="sep">|</span>
		©<script type="text/javascript">document.write(new Date().getFullYear());</script>
		<a target="_blank" href="http://www.duraspace.org">DuraSpace.org</a>  <span class="sep">|</span>
		<a target="_blank" href="http://www.duracloud.org">Duracloud.org</a>  <span class="sep">|</span> 
		<a target="_blank" href="#">Contact Us</a>
	</div>
</div>	

	