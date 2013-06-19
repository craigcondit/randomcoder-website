<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="homeUrl" value="/" />
<c:url var="aboutUrl" value="/legal/about" />
<c:url var="downloadUrl" value="/download" />
<div class="sectionHeading">Go...</div>
<div class="sectionContent">
	<ul class="nav">
	  <li><a href="${homeUrl}">Home</a></li>
	  <li class="navbreak"><a href="${downloadUrl}">Download</a></li>
	  <li><a href="https://randomcoder.org//cgit/public/">Git repositories</a></li>
	  <li><a href="https://nexus.randomcoder.org/content/sites/randomcoder-website/">Maven site</a></li>
	  <li><a href="https://nexus.randomcoder.org/content/sites/randomcoder-website/apidocs/">JavaDoc</a></li>
		<li class="navbreak"><a href="${aboutUrl}">About this site</a></li>
	</ul>
</div>
