<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="sectionHeading">Feeds</div>
<div class="sectionContent">
	<ul class="nav">
	  <li><a class="feed" href="${pageContext.request.contextPath}/feeds/atom/all">All articles (Atom)</a></li>
	  <li><a class="feed" href="${pageContext.request.contextPath}/feeds/rss20/all">All articles (RSS 2.0)</a></li>
	</ul>
</div>
