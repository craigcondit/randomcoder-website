<%-- Article comments --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.org/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://randomcoder.org/tags-url" prefix="url" %>
<url:current var="formAction" />
<url:current var="currentUrl" />
<c:url var="loginUrl" value="/redirect">
	<c:param name="url" value="${currentUrl}#post-comments" />
</c:url>
<a name="post-comments"></a>
<div class="sectionHeading">Comment on this article</div>
<div class="sectionContent">

	<c:choose>
		<c:when test="${commentsEnabled}">
			<c:if test="${command.anonymous}">
				<p>
					You are not currently logged in. <a href="${loginUrl}">Login now</a> or
					post as a guest.
				</p>
				
				<p>
					<strong>NOTE:</strong>: All comments are subject to moderation. In
					addition, comments by guests will not appear until moderation has been
					completed.
				</p>
			</c:if>
			
		  <form method="post" action="${formAction}#post-comments">
				<spring:bind path="command">
					<c:if test="${fn:length(status.errorMessages) > 0}">
						<c:forEach items="${status.errorMessages}" var="error">
							<div class="globalError"><c:out value="${error}" /></div>
						</c:forEach>
					</c:if>
				</spring:bind>
				
				<c:if test="${command.anonymous}">
					<spring:bind path="command.anonymousUserName">
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
								<label for="anonymousUserName">Your name:</label>
								<input:text name="anonymousUserName" styleClass="text" styleId="anonymousUserName" maxlength="30" value="${status.value}" />
							</div>
							<div class="description">
								Let us know who you are.
							</div>
						</div>
			  	</spring:bind>
			  	
					<spring:bind path="command.anonymousEmailAddress">
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
								<label for="anonymousEmailAddress">Email address:</label>
								<input:text name="anonymousEmailAddress" styleClass="text" styleId="anonymousEmailAddress" maxlength="320" value="${status.value}" />
							</div>
							<div class="description">
								Enter your email address (will not be shown).
							</div>
						</div>
			  	</spring:bind>
			
					<spring:bind path="command.anonymousWebsite">
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
								<label for="anonymousWebsite">Web site:</label>
								<input:text name="anonymousWebsite" styleClass="text" styleId="anonymousWebsite" maxlength="255" value="${status.value}" />
							</div>
							<div class="description">
								Enter your web site, if you have one.
							</div>
						</div>
			  	</spring:bind>
			  </c:if>
		
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
							Enter a title for your comment.
						</div>
					</div>
		  	</spring:bind>
		  		
		  	<spring:bind path="command.content">
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
							<label for="contentArea">
								Comment:
							</label>
		 					<input:textarea name="content" styleId="contentArea" cols="80" rows="7" value="${status.value}" />
						</div>
						<div class="description">
							Enter your comment. HTML is not allowed and will not be displayed.<br />					
							Be curteous. Be professional. Or don't, and face the consequences.
						</div>
					</div>
		  	</spring:bind>
		  		
		  	<div class="fields">
		  		<div class="buttons">
				    <input type="submit" class="submit" value="Post comment &#187;" />
		  		</div>
		  	</div>
		  		
		  </form>
		</c:when>
		<c:otherwise>
			<p>
				Comments are closed for this post.
			</p>
		</c:otherwise>
	</c:choose>
</div>
