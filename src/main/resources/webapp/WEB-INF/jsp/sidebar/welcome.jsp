<%-- Welcome --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<c:url var="logoutUrl" value="/logout" />
<c:url var="postUrl" value="/article/add" />
<c:url var="changePassUrl" value="/user/change-password" />
<c:url var="userProfileUrl" value="/user/profile" />
<c:url var="manageUsersUrl" value="/user" />
<c:url var="manageTagsUrl" value="/tag" />
<c:if test="${pageContext.request.userPrincipal != null}">
	<div class="sectionHeading">
		Welcome, <c:out value="${pageContext.request.userPrincipal.name}" />
	</div>
	<div class="sectionContent" align="left">
		<ul class="nav">
			<sec:authorize url="${postUrl}">
				<li><a href="${postUrl}">Add new article</a></li>
			</sec:authorize>
			<sec:authorize url="${manageUsersUrl}">
				<li><a href="${manageUsersUrl}">Manage users</a></li>
			</sec:authorize>
			<sec:authorize url="${manageTagsUrl}">
				<li><a href="${manageTagsUrl}">Manage tags</a></li>
			</sec:authorize>
			<li><a href="${userProfileUrl}">My profile</a></li>
	  	<li class="navbreak"><a href="${logoutUrl}">Logout</a></li>		
		</ul>
	</div>
</c:if>
