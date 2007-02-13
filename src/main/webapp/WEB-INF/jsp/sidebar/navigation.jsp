<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="homeUrl" value="/" />
<c:url var="aboutUrl" value="/legal/about" />
<c:url var="downloadUrl" value="/download" />
<div class="sectionHeading">Go...</div>
<div class="sectionContent">
	<ul class="nav">
	  <li><a href="${homeUrl}">Home</a></li>
	  <li class="navbreak"><a href="${downloadUrl}">Download</a></li>
	  <li><a href="http://randomcoder.com/repos/public/">Subversion Repository</a></li>
	  <li><a href="http://randomcoder.com/maven/site/randomcoder-website/">Maven Site</a></li>
	  <li><a href="http://randomcoder.com/maven/site/randomcoder-website/apidocs/">JavaDoc</a></li>
		<li class="navbreak"><a href="${aboutUrl}">About this site</a></li>
	</ul>
</div>