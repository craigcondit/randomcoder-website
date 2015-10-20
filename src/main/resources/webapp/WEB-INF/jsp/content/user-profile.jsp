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
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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
					<input:text name="emailAddress" styleClass="text focusFirst" styleId="emailAddress" maxlength="320" value="${status.value}" />
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
