<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-input" prefix="input" %>
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

<c:if test="${(empty command.formType) or command.formType == 'INFOCARD'}">
	<div class="sectionHeading">Create an account using an information card</div>
	<div class="sectionContent">
		<form name="infocard" id="infocard" method="post" action="${formAction}">
		
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
								alt="Submit an information card" title="Submit an information card"
								src="${pageContext.request.contextPath}/images/informationcard.gif"
								onclick="document.getElementById('infocard').submit()" />
							<object type="application/x-informationCard" name="xmlToken">
								<param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion" />
								<param name="requiredClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
								<param name="optionalClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/webpage" />
							</object>
						</div>
					</div>
				</div>
			</spring:bind>
			
		</form>	
	</div>
</c:if>

<c:if test="${empty command.formType}">
	<div class="sectionHeading">OR, create an account using a password</div>
</c:if>
<c:if test="${command.formType == 'PASS'}">
	<div class="sectionHeading">Enter account details</div>		
</c:if>
<c:if test="${(empty command.formType) or command.formType == 'PASS'}">
	<div class="sectionContent">
	  <form method="post" action="${formAction}">
			<input type="hidden" name="formType" value="PASS" />
		  
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
						Enter a login name for your new account.
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
	  	
	  	<div class="fields">
	  		<div class="buttons">
			    <input type="submit" class="submit" value="Save &#187;" />
		  	  <input type="submit" class="button" name="cancel" value="Cancel" />
	  		</div>
	  	</div>
	
	  </form>
	  
	</div>
</c:if>