<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/" />
<c:url var="formAction" value="${template.formAction}" />
<div class="sectionHeading">
  <c:choose>
  	<c:when test="${template.formMode == 'edit'}">Edit article</c:when>
  	<c:otherwise>Add article</c:otherwise>
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
  
  	<spring:bind path="command.title">
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
					<label for="title">Title:</label>
					<input:text name="title" styleClass="text" styleId="title" maxlength="255" value="${status.value}" />
				</div>
				<div class="description">
					Enter a descriptive title for this article.
				</div>
			</div>
  	</spring:bind>
  	
  	<spring:bind path="command.permalink">
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
					<label for="permalink">Permalink:</label>
					<input:text name="permalink" styleClass="text" styleId="permalink" maxlength="100" value="${status.value}" />
				</div>
				<div class="description">
					Enter a unique name which will be used to construct an unchanging URL for this article.<br />
					Permalinks may only contain letters, numbers, and dashes.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.contentType">
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
					<label for="contentType">Content type:</label>
					<input:select name="contentType" styleId="contentType" value="${status.value}">
		    		<input:option text="" value="" />
		    	  <c:forEach var="ct" items="${contentTypes}">	  	    	  
		    	  	<input:option text="${ct.description}" value="${ct.name}" />
		    	  </c:forEach>
					</input:select>
				</div>
				<div class="description">
					Choose the content type you wish to author this article with:<br /><br />					
					<strong>Plain text:</strong> Simple to use, but doesn't allow complex formatting.<br />
					<strong>XHTML:</strong> Allows for rich formatting but requires a working knowledge of <abbr title="XML Hypertext Markup Language">XHTML</abbr>.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.tags">
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
					<label for="contentType">Tags:</label>
					<input:textarea name="tags" styleId="tags" value="${status.value}" rows="3" cols="80" />
				</div>
				<div class="description">
					Enter descriptive &quot;tags&quot; separated by commas.
				</div>
			</div>
  	</spring:bind>

  	<spring:bind path="command.content">
  		<c:set var="fieldClasses">fields long required<c:if test="${status.error}"> error</c:if></c:set>
			<div class="${fieldClasses}">
				<div>
					<c:if test="${status.error}">
						<c:forEach var="error" items="${status.errorMessages}">
							<span class="error"><c:out value="${error}" /></span>
						</c:forEach>
					</c:if>
				</div>
				<div>					
					<label for="contentArea">
						Content:
						<span class="description">
							Enter the content for this article.
						</span>
					</label>
 					<input:textarea name="content" styleId="contentArea" cols="80" rows="20" value="${status.value}" />
				</div>
			</div>
  	</spring:bind>
  	
  	<div class="fields long">
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