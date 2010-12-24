<%-- User Profile --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.org/tags-escape" prefix="rcesc" %>
<%@ taglib uri="http://randomcoder.org/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/" />
<c:url var="addUrl" value="/user/profile" />
<c:url var="prefsUrl" value="/user/profile" />
<c:url var="changePasswordUrl" value="/user/profile/change-password" />

<spring:bind path="command">
	<c:if test="${fn:length(status.errorMessages) > 0}">
		<c:forEach items="${status.errorMessages}" var="error">
			<div class="globalError"><c:out value="${error}" /></div>
		</c:forEach>
	</c:if>
</spring:bind>

<div class="sectionHeading">My profile</div>
<div class="sectionSubHeading">
	<c:choose>
		<c:when test="${empty user.password}">
			<a class="add" href="${changePasswordUrl}">Set a password</a>
		</c:when>
		<c:otherwise>
			<a class="edit" href="${changePasswordUrl}">Change password</a>
		</c:otherwise>
	</c:choose>
	::
	<a href="${homeUrl}">Done</a>
</div>
<div class="sectionContent">
  <form method="post" name="prefs" id="prefs" action="${prefsUrl}">
  	<input type="hidden" name="formType" value="PREFS" />
  	
		<div class="fields">
			<div>					
				<label>User name:</label>
				<span class="constant"><c:out value="${user.userName}" /></span>
			</div>
		</div>
		
		<div class="fields">
			<div>					
				<label>Password:</label>
				<c:choose>
					<c:when test="${empty user.password}"><span class="constant">Disabled</span></c:when>
					<c:otherwise><span class="constant">********</span></c:otherwise>
				</c:choose>
			</div>
		</div>

  	<spring:bind path="command.emailAddress">
  		<c:set var="fieldClasses">fields required<c:if test="${status.error}"> error</c:if></c:set>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label for="emailAddress">Email address:</label>
					<input:text name="emailAddress" styleClass="text" styleId="emailAddress" maxlength="320" value="${status.value}" />
				</div>
				<div class="description">
					Enter your email address. This will not be shown publicly.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.website">
  		<c:set var="fieldClasses">fields<c:if test="${status.error}"> error</c:if></c:set>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label for="website">Web site:</label>
					<input:text name="website" styleClass="text" styleId="website" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter the URL of your web site, if you have one.
				</div>
			</div>
  	</spring:bind>
  
  	<div class="fields">
	 		<div class="buttons">
		    <input type="submit" class="submit" value="Save &#187;" />
	  	  <input type="submit" class="button" name="cancel" value="Cancel" />
	 		</div>
	 	</div>
  
  </form>
</div>

<div class="sectionHeading">Information cards</div>
<div class="sectionSubHeading">
	<a href="${homeUrl}">Done</a>
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
	<form name="infocard" id="infocard" method="post" action="${addUrl}">	
		<input type="hidden" name="formType" value="INFOCARD" />
		
  	<spring:bind path="command.xmlToken">
			<c:set var="fieldClasses">fields required<c:if test="${status.error}"> error</c:if></c:set>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label>Information card:</label>
					<div style="margin-left: 10.5em">
						<img
							style="cursor: pointer; cursor: hand; width: 100px; height: 86px"
							alt="Add an information card" title="Add an information card"
							src="${pageContext.request.contextPath}/images/informationcard.gif"
							onclick="document.getElementById('infocard').submit()" />
						<object type="application/x-informationCard" name="xmlToken">
							<param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion" />
							<param name="requiredClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
					</div>
				</div>
			</div>
		</spring:bind>
		
	</form>	
</div>
