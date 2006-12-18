<%-- Welcome --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<c:url var="logoutUrl" value="/logout" />
<c:url var="postUrl" value="/article/add" />
<c:url var="changePassUrl" value="/user/change-password" />
<c:url var="manageUsersUrl" value="/user" />
<c:url var="manageTagsUrl" value="/tag" />
<c:if test="${pageContext.request.userPrincipal != null}">
	<div class="sectionHeading">
		Welcome, <c:out value="${pageContext.request.userPrincipal.name}" />
	</div>
	<div class="sectionContent" align="left">
		<ul class="nav">
			<authz:authorize ifAnyGranted="ROLE_POST_ARTICLES,ROLE_MANAGE_ARTICLES">
				<li><a href="${postUrl}">Add new article</a></li>
			</authz:authorize>
			<authz:authorize ifAnyGranted="ROLE_MANAGE_USERS">
				<li><a href="${manageUsersUrl}">Manage users</a></li>
			</authz:authorize>
			<authz:authorize ifAnyGranted="ROLE_MANAGE_TAGS">
				<li><a href="${manageTagsUrl}">Manage tags</a></li>
			</authz:authorize>
			<li><a href="${changePassUrl}">Change password</a></li>
	  	<li class="navbreak"><a href="${logoutUrl}">Logout</a></li>		
		</ul>
	</div>
</c:if>