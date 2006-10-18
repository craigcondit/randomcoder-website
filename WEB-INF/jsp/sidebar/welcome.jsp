<%-- Welcome --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<c:url var="logoutUrl" value="/logout" />
<c:url var="postUrl" value="/article/add" />
<sec:loggedIn>
	<div class="sectionHeading">
		Welcome, <c:out value="${pageContext.request.userPrincipal.name}" />
	</div>
	<div class="sectionContent" align="right">
		<ul class="nav">
			<li><a href="${postUrl}">Add new article</a></li>
	  	<li class="navbreak"><a href="${logoutUrl}">Logout</a></li>		
		</ul>
	</div>
</sec:loggedIn>