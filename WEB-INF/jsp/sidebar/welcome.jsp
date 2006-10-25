<%-- Welcome --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<c:url var="logoutUrl" value="/logout" />
<c:url var="postUrl" value="/article/add" />
<c:url var="changePassUrl" value="/user/change-password" />
<sec:loggedIn>
	<div class="sectionHeading">
		Welcome, <c:out value="${pageContext.request.userPrincipal.name}" />
	</div>
	<div class="sectionContent" align="right">
		<ul class="nav">
			<sec:inRole role="article-post,article-admin">
				<li><a href="${postUrl}">Add new article</a></li>
			</sec:inRole>
			<li><a href="${changePassUrl}">Change password</a></li>
	  	<li class="navbreak"><a href="${logoutUrl}">Logout</a></li>		
		</ul>
	</div>
</sec:loggedIn>