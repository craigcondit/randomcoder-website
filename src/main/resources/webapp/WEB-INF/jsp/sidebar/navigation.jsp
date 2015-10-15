<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="homeUrl" value="/" />
<c:url var="aboutUrl" value="/legal/about" />
<div class="sectionHeading">Go...</div>
<div class="sectionContent">
	<ul class="nav">
	  <li><a href="${homeUrl}">Home</a></li>
	  <li class="navbreak"><a href="https://bintray.com/insideo/randomcoder-release" class="external">Download</a></li>
	  <li><a href="https://github.com/search?q=user%3Ainsideo+randomcoder-" class="external">Git repositories</a></li>
		<li class="navbreak"><a href="${aboutUrl}">About this site</a></li>
	</ul>
</div>
