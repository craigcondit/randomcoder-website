<%-- User list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<c:url var="homeUrl" value="/" />
<c:url var="addUrl" value="/user/add" />

<div class="sectionHeading">Users</div>
<div class="sectionSubHeading">
	<a class="add" href="${addUrl}">Add new user</a> :: <a href="${homeUrl}">Done</a>
</div>
<div class="sectionContentFull">
	<c:set var="class" value="even" />
	<c:forEach var="user" items="${users}" varStatus="status">	
	  <c:url var="editLink" value="/user/edit">
	  	<c:param name="id" value="${user.id}" />
	  </c:url>
	  <c:url var="deleteLink" value="/user/delete">
	  	<c:param name="id" value="${user.id}" />
	  </c:url>
		<div class="${class}">
			<div class="actions">
				<a class="edit" href="${editLink}">Edit</a> :: <a class="delete" href="${deleteLink}">Delete</a>
			</div>
			<c:set var="data">
				<a href="${editLink}"><strong><c:out value="${user.userName}" /></strong></a>
				<c:if test="${not empty user.emailAddress}">
					(<c:out value="${user.emailAddress}" />)
				</c:if>
			</c:set>			
			<c:choose>
				<c:when test="${user.enabled}">
					${data}
				</c:when>
				<c:otherwise>
					<del>${data}</del>
				</c:otherwise>
			</c:choose>
			<c:if test="${not empty user.roles}">
				<div class="details">
					<strong>Roles:</strong>
					<c:forEach var="role" items="${user.roles}" varStatus="roleStatus"><c:if test="${roleStatus.index > 0}">,</c:if>
					<c:out value="${role.description}" /></c:forEach>
				</div>
			</c:if>
		</div>
		<c:choose>
			<c:when test="${class == 'even'}"><c:set var="class" value="odd" /></c:when>
			<c:otherwise><c:set var="class" value="even" /></c:otherwise>
		</c:choose>		
	</c:forEach>
</div>