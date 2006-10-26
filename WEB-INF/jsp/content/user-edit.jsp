<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/user" />
<c:url var="formAction" value="${template.formAction}" />
<div class="sectionHeading">
  <c:choose>
  	<c:when test="${template.formMode == 'edit'}">Edit user</c:when>
  	<c:otherwise>Add user</c:otherwise>
  </c:choose>  
</div>
<div class="sectionContent">
  <form method="post" action="${formAction}">
  
		<spring:bind path="command">
			<c:if test="${fn:length(status.errorMessages) > 0}">
				<c:forEach items="${status.errorMessages}" var="error">
					<div class="globalError"><c:out value="${error}" /></div>
				</c:forEach>
			</c:if>
		</spring:bind>
  
		<c:choose>
			<c:when test="${template.formMode == 'add'}">
		  	<spring:bind path="command.userName">
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
							<label for="userName">User name:</label>
							<input:text name="userName" styleClass="text" styleId="userName" maxlength="30" value="${status.value}" />
						</div>
						<div class="description">
							Enter a login name for the new user.
						</div>
					</div>
		  	</spring:bind>
		  </c:when>
		  <c:otherwise>
				<div class="fields">
					<div>					
						<label>User name:</label>
						<span class="constant"><c:out value="${command.userName}" /></span>
					</div>
				</div>
		  </c:otherwise>
		</c:choose>
  	
  	<spring:bind path="command.enabled">
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
					<fieldset class="checkbox">
						<legend>Login status</legend>
						<div class="description">
							Change this option to enable / disable user logins.
						</div>
						<div>
							<label for="enabled">Enabled</label>
							<input:checkbox styleClass="checkbox" name="enabled" styleId="enabled" checked="${status.value}" value="true" />
						</div>
					</fieldset>
				</div>
			</div>
  	</spring:bind>
  	
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
					Enter the user's email address.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.password">
  		<c:set var="fieldClasses">fields<c:if test="${status.error}"> error</c:if></c:set>
  		<c:if test="${template.formMode == 'add'}">
  			<c:set var="fieldClasses">${fieldClasses} required</c:set>
  		</c:if>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label for="password">Password:</label>
					<input:password name="password" styleClass="text" styleId="password" value="${status.value}" />
				</div>
				<div class="description">
					<c:choose>
						<c:when test="${template.formMode == 'add'}">
							Enter a password for the new user.
						</c:when>
						<c:otherwise>
							Enter a new password for this user, or leave blank to keep it
							unchanged.
						</c:otherwise>
					</c:choose>				
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.password2">
  		<c:set var="fieldClasses">fields<c:if test="${status.error}"> error</c:if></c:set>
  		<c:if test="${template.formMode == 'add'}">
  			<c:set var="fieldClasses">${fieldClasses} required</c:set>
  		</c:if>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label for="password2">Password (again):</label>
					<input:password name="password2" styleClass="text" styleId="password2" value="${status.value}" />
				</div>
				<div class="description">
					Enter the password again for verification.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.roles">
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
					<label for="roles">Roles:</label>
					<input:select multiple="true" size="5" name="roles" styleId="roles">
						<c:forEach var="selectedRole" items="${command.roles}">
							<input:selected-value value="${selectedRole.name}" />
						</c:forEach>
						<c:forEach var="availableRole" items="${availableRoles}">
							<input:option text="${availableRole.description}" value="${availableRole.name}" />
						</c:forEach>
					</input:select>
				</div>
				<div class="description">
					Select security roles to apply to this user. Use 'Ctrl' to select
					multiple roles.
				</div>
			</div>
  	</spring:bind>
  	
  	<div class="fields">
			<c:if test="${template.formMode == 'edit'}">
	  		<input:hidden name="id" value="${command.id}" />
	  		<input:hidden name="userName" value="${command.userName}" />
	  	</c:if>
  		<div class="buttons">
		    <input type="submit" class="submit" value="Save &#187;" />
	  	  <input type="button" class="button" value="Cancel" onclick="document.location.href='${homeUrl}'" />
  		</div>
  	</div>

  </form>
  
</div>