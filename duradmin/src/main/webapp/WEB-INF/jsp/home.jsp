<%@include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="base" >
	<tiles:putAttribute name="title">
		<spring:message code="home" />	
	</tiles:putAttribute>
	<tiles:putAttribute name="mainTab" value="home" />
	<tiles:putAttribute name="main-content">
		<tiles:insertDefinition name="base-main-content">
			<tiles:putAttribute name="header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title" value="Welcome to Duracloud"/>
				</tiles:insertDefinition>
			</tiles:putAttribute>
			<tiles:putAttribute name="body">
				<p>
				Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque elementum tellus dapibus ipsum commodo commodo. Curabitur posuere orci ut lacus eleifend in imperdiet quam vestibulum. Pellentesque placerat nulla sit amet ipsum sodales fringilla aliquam nunc imperdiet. Aenean placerat, metus eget laoreet faucibus, libero risus dapibus libero, at porttitor sem nisi vel ligula. Aliquam consectetur arcu eget nisl interdum varius. Ut non scelerisque metus. Nulla accumsan bibendum mauris. Duis at lacus non augue porta porta vitae quis lacus. Praesent a nisl eget nunc suscipit dictum. Suspendisse pulvinar arcu id libero sagittis semper. Etiam sed erat est, et tempus erat. Phasellus sit amet malesuada erat. Curabitur pretium erat in metus dignissim consectetur. Mauris blandit sollicitudin pulvinar. Vivamus et cursus massa. Vivamus sagittis, sapien quis hendrerit lacinia, nunc ipsum tincidunt leo, ac pharetra justo felis in ante. Suspendisse bibendum mattis tortor sit amet auctor. Duis sollicitudin porta nunc. Phasellus aliquam eleifend dictum. Proin quam arcu, ullamcorper eu vestibulum quis, condimentum vitae arcu.</p>
				<p>
				Aliquam euismod sapien vel nibh ornare vulputate. Praesent faucibus condimentum libero auctor dictum. Sed aliquam pulvinar nunc sit amet volutpat. Donec euismod convallis purus eget rhoncus. Vestibulum bibendum quam et neque sollicitudin a condimentum sapien facilisis. Phasellus sed nisi in metus hendrerit convallis quis sed arcu. Donec sit amet urna lectus, fermentum lobortis enim. Vivamus ac accumsan erat. In imperdiet ante eget libero cursus nec tempus nunc gravida. Nam magna ligula, mattis nec euismod sit amet, aliquet nec libero. 
				</p>
			</tiles:putAttribute>
		</tiles:insertDefinition>
	</tiles:putAttribute>
</tiles:insertDefinition>
