<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.org/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/tag" />
<c:url var="formAction" value="${template.formAction}" />
<c:choose>
	<c:when test="${template.formMode == 'edit'}">
		<c:set var="headingText" value="Edit tag" />
		<c:set var="displayNameClass" value="focusFirst " />
	</c:when>
	<c:otherwise>
		<c:set var="headingText" value="Add tag" />
		<c:set var="displayNameClass" value="" />
	</c:otherwise>
</c:choose>  
<div class="sectionHeading">
	${headingText}
</div>
<div class="sectionContent">
  <form method="post" action="${formAction}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<spring:bind path="command">
			<c:if test="${fn:length(status.errorMessages) > 0}">
				<c:forEach items="${status.errorMessages}" var="error">
					<div class="globalError"><c:out value="${error}" /></div>
				</c:forEach>
			</c:if>
		</spring:bind>
  
		<c:choose>
			<c:when test="${template.formMode == 'add'}">
				<spring:bind path="command.name">
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
							<label for="tagName">Tag name:</label>
							<input:text name="name" styleClass="text focusFirst" styleId="tagName" maxlength="255" value="${status.value}" />
						</div>
						<div class="description">
							Enter a unique name for the new tag.
						</div>
					</div>
				</spring:bind>
		  </c:when>
		  <c:otherwise>
				<div class="fields">
					<div>					
						<label>Tag name:</label>
						<span class="constant"><c:out value="${command.name}" /></span>
					</div>
				</div>
		  </c:otherwise>
		</c:choose>
  	
  	<spring:bind path="command.displayName">
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
					<label for="displayName">Display name:</label>
					<input:text name="displayName" styleClass="${displayNameClass}text" styleId="displayName" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter a friendly name for this tag.
				</div>
			</div>
  	</spring:bind>

  	<div class="fields">
			<c:if test="${template.formMode == 'edit'}">
	  		<input:hidden name="id" value="${command.id}" />
	  		<input:hidden name="name" value="${command.name}" />
	  	</c:if>
  		<div class="buttons">
		    <input type="submit" class="submit" value="Save &#187;" />
	  	  <input type="submit" class="button" name="cancel" value="Cancel" />
  		</div>
  	</div>

  </form>
  
</div>
