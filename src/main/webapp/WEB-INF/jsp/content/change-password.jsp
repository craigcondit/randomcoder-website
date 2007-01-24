<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/" />
<c:url var="formAction" value="${template.formAction}" />
<div class="sectionHeading"><c:out value="${template.title}" /></div>
<div class="sectionContent">
  <form method="post" action="${formAction}">
  
		<spring:bind path="command">
			<c:if test="${fn:length(status.errorMessages) > 0}">
				<c:forEach items="${status.errorMessages}" var="error">
					<div class="globalError"><c:out value="${error}" /></div>
				</c:forEach>
			</c:if>
		</spring:bind>
  
  	<spring:bind path="command.oldPassword">
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
					<label for="oldPassword">Current password:</label>
					<input:password name="oldPassword" styleClass="text" styleId="oldPassword" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter your current password.<br />
					Leave this field blank if you do not currently have a password.
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
					<label for="password">New password:</label>
					<input:password name="password" styleClass="text" styleId="password" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter your new password.
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
					<input:password name="password2" styleClass="text" styleId="password2" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter your new password again for verification.
				</div>
			</div>
  	</spring:bind>  	
  	
  	<div class="fields">
			<c:if test="${template.formMode == 'edit'}">
	  		<input:hidden name="id" value="${command.id}" />
	  	</c:if>
  		<div class="buttons">
		    <input type="submit" class="submit" value="Save &#187;" />
		    <input type="submit" class="button" name="cancel" value="Cancel" />
  		</div>
  	</div>

  </form>
  
</div>