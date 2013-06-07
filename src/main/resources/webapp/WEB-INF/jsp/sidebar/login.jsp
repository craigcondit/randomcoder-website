<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${pageContext.request.userPrincipal == null}">
	<c:url var="loginUrl" value="/login" />
	<c:url var="createAccountUrl" value="/account/create" />
	<div class="sectionHeading">Welcome</div>
	<div class="sectionContent" align="left">
		<ul class="nav">
			<li><a href="${loginUrl}">Login</a></li>
			<li class="navbreak"><a href="${createAccountUrl}">Create new account</a></li>
		</ul>
	</div>
</c:if>
