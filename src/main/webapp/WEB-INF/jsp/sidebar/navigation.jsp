<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="homeUrl" value="/" />
<c:url var="aboutUrl" value="/legal/about" />
<c:url var="javadocUrl" value="/docs/apidocs/" />
<div class="sectionHeading">Go...</div>
<div class="sectionContent">
	<ul class="nav">
	  <li><a href="${homeUrl}">Home</a></li>
	  <li class="navbreak"><a href="http://randomcoder.com/repos/public/">Subversion Repository</a></li>
	  <li><a href="${javadocUrl}">JavaDoc</a></li>
		<li class="navbreak"><a href="${aboutUrl}">About this site</a></li>
	</ul>
</div>