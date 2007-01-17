<%-- Tag list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<c:url var="homeUrl" value="/" />
<c:url var="addUrl" value="/user/profile/add-card" />
<div class="sectionHeading">Information cards</div>
<div class="sectionSubHeading">
	<a class="add" href="${addUrl}">Add new card</a> :: <a href="${homeUrl}">Done</a>
</div>
<c:choose>
	<c:when test="${empty cardSpaceTokens}">
		<div class="sectionContent">
			You do not have any information cards.
		</div>
	</c:when>
	<c:otherwise>
		<div class="sectionContentFull">
			<c:set var="class" value="even" />
			<c:forEach var="token" items="${cardSpaceTokens}" varStatus="status">
				<c:url var="deleteLink" value="/user/profile/delete-card">
					<c:param name="id" value="${token.id}" />
				</c:url>
				<div class="${class}">
					<div class="actions">
						<a class="delete" href="${deleteLink}">Delete</a>
					</div>
					<strong><c:out value="${token.emailAddress}" /></strong>
					<div class="details">
						<strong>Created:</strong>
						<fmt:formatDate dateStyle="long" timeStyle="long" value="${token.creationDate}" />
						<strong>Last used:</strong>
						<c:choose>
							<c:when test="${not empty token.lastLoginDate}">
								<fmt:formatDate dateStyle="long" timeStyle="long" value="${token.lastLoginDate}" />
							</c:when>
							<c:otherwise>
								Never
							</c:otherwise>
						</c:choose>
					</div>
				</div>				
				<c:choose>
					<c:when test="${class == 'even'}"><c:set var="class" value="odd" /></c:when>
					<c:otherwise><c:set var="class" value="even" /></c:otherwise>
				</c:choose>		
			</c:forEach>
		</div>
	</c:otherwise>
</c:choose>

<div class="sectionHeading">Add an information card</div>
<div class="sectionContent">
	<form name="infocard" id="infocard" method="post" action="${pageContext.request.contextPath}/user/profile/add-card">
		<div class="fields required">
			<div>					
				<label>Information card:</label>
				<div style="margin-left: 10.5em">
					<img
						style="cursor: pointer; cursor: hand; width: 100px; height: 86px"
						alt="Login with your information card" title="Add an information card"
						src="${pageContext.request.contextPath}/images/informationcard.gif"
						onclick="document.getElementById('infocard').submit()" />
					<object type="application/x-informationCard" name="xmlToken">
						<param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion" />
						<param name="requiredClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
				</div>
			</div>
		</div>
	</form>	
</div>