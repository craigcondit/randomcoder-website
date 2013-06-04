<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.org/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/user" />
<c:url var="formAction" value="${template.formAction}" />
  
<spring:bind path="command">
	<c:if test="${fn:length(status.errorMessages) > 0}">
		<c:forEach items="${status.errorMessages}" var="error">
			<div class="globalError"><c:out value="${error}" /></div>
		</c:forEach>
	</c:if>
</spring:bind>

<div class="sectionHeading">Enter account details</div>		
<div class="sectionContent">
  <form method="post" action="${formAction}">
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
					<input:text name="userName" styleClass="text" styleId="userNameText" maxlength="30" value="${status.value}" />
				</div>
				<div class="description">
					Enter a user name for your new account.<br />
					This will be your public identity on this site.
				</div>
			</div>
  	</spring:bind>
  	
  	<spring:bind path="command.password">
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
					<label for="password">Password:</label>
					<input:password name="password" styleClass="text" styleId="password" value="${status.value}" />
				</div>
				<div class="description">
					Enter a password for your account.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.password2">
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
					<label for="password2">Password (again):</label>
					<input:password name="password2" styleClass="text" styleId="password2" value="${status.value}" />
				</div>
				<div class="description">
					Enter your password again for verification.
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
		    <input type="submit" class="submit" value="Register &#187;" />
	  	  <input type="submit" class="button" name="cancel" value="Cancel" />
  		</div>
  	</div>

  </form>
  
</div>