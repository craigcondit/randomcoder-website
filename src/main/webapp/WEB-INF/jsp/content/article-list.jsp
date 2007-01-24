<%-- Article list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<%@ taglib uri="http://randomcoder.com/tags-url" prefix="url" %>

<c:set var="postArticles" value="${false}" />
<authz:authorize ifAnyGranted="ROLE_ARTICLE_POST"><c:set var="postArticles" value="${true}" /></authz:authorize>

<c:set var="manageArticles" value="${false}" />
<authz:authorize ifAnyGranted="ROLE_MANAGE_ARTICLES"><c:set var="manageArticles" value="${true}" /></authz:authorize>

<c:set var="userName" value="" />
<c:if test="${pageContext.request.userPrincipal != null}">
	<c:set var="userName" value="${pageContext.request.userPrincipal.name}" />
</c:if>
<c:set var="previousDate" value="" />

<c:forEach var="articleDecorator" items="${articles}">
  <c:set var="articleAuthor" value="${null}" />
  <c:if test="${articleDecorator.article.createdByUser != null}">
  	<c:set var="articleAuthor" value="${articleDecorator.article.createdByUser.userName}" />
  </c:if>  
  <c:url var="editLink" value="/article/edit">
  	<c:param name="id" value="${articleDecorator.article.id}" />
  </c:url>
  <c:url var="deleteLink" value="/article/delete">
  	<c:param name="id" value="${articleDecorator.article.id}" />
  </c:url>
	<div class="sectionHeading">
		<c:out value="${articleDecorator.article.title}" />
	</div>
	<div class="sectionSubHeading">
		Posted
		<c:if test="${articleAuthor != null}">
			by
			<c:choose>
				<c:when test="${articleDecorator.article.createdByUser != null && articleDecorator.article.createdByUser.website != null}">
					<a class="external" href="${articleDecorator.article.createdByUser.website}"><c:out value="${articleAuthor}" /></a>
				</c:when>
				<c:otherwise>
					<strong><c:out value="${articleAuthor}" /></strong>
				</c:otherwise>
			</c:choose>			
		</c:if>
		on <fmt:formatDate type="date" dateStyle="short" value="${articleDecorator.article.creationDate}" />
		@ <fmt:formatDate type="time" timeStyle="short" value="${articleDecorator.article.creationDate}" />
		<c:if test="${articleDecorator.article.modificationDate != null}">
			:: Updated
			<c:if test="${articleDecorator.article.modifiedByUser != null}">
				by
				<c:choose>
					<c:when test="${articleDecorator.article.modifiedByUser.website != null}">
						<a class="external" href="${articleDecorator.article.modifiedByUser.website}"><c:out value="${articleDecorator.article.modifiedByUser.userName}" /></a>
					</c:when>
					<c:otherwise>
						<strong><c:out value="${articleDecorator.article.modifiedByUser.userName}" /></strong>
					</c:otherwise>
				</c:choose>
			</c:if>
			on <fmt:formatDate type="date" dateStyle="short" value="${articleDecorator.article.modificationDate}" />
			@ <fmt:formatDate type="time" timeStyle="short" value="${articleDecorator.article.modificationDate}" /></c:if>
	</div>
	<c:if test="${fn:length(articleDecorator.article.tags) > 0}">
		<div class="sectionSubHeading">
		  Tags
		  <c:forEach var="tag" items="${articleDecorator.article.tags}" varStatus="tagStat">
		    <c:url var="tagLink" value="/tags/${rcesc:urlencode(tag.name)}" />
		    :: <a class="tag" rel="tag" href="${tagLink}"><c:out value="${tag.displayName}" /></a></c:forEach><br />
		</div>
	</c:if>
	<div class="sectionSubHeading">
		<c:choose>
			<c:when test="${not empty articleDecorator.article.permalink}">
				<c:url var="permUrl" value="/articles/${rcesc:urlencode(articleDecorator.article.permalink)}" />
			</c:when>
			<c:otherwise>
				<c:url var="permUrl" value="/articles/id/${articleDecorator.article.id}" />
			</c:otherwise>
		</c:choose>
		<a rel="permalink" class="permalink" href="${permUrl}">Permalink</a>
		<c:if test="${pageContext.request.userPrincipal != null}">
		  <c:if test="${manageArticles || (postArticles && userName == articleAuthor)}">
				:: <a class="edit" href="${editLink}">Edit</a>
			  :: <a class="delete" href="${deleteLink}">Delete</a>
			</c:if>
		</c:if>
	</div>
	<div class="sectionContent">
	  <c:choose>
		  <c:when test="${template.summary == 'true'}">
		  	<c:choose>
		  		<c:when test="${articleDecorator.summaryPresent}">
			  		${articleDecorator.formattedSummary}
		  		</c:when>
		  		<c:otherwise>
		  			${articleDecorator.formattedText}
		  		</c:otherwise>
		  	</c:choose>
				<div class="sectionFooter">
					<c:set var="commentCount" value="${fn:length(articleDecorator.comments)}" />
					<c:choose>
						<c:when test="${commentCount == 1}">
							<c:set var="commentText">1 comment</c:set>
						</c:when>
						<c:when test="${commentCount > 1}">
							<c:set var="commentText">${commentCount} comments</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="commentText">Comment on this article</c:set>
						</c:otherwise>
					</c:choose>
					<c:if test="${articleDecorator.summaryPresent}">
						<a class="read-more" href="${permUrl}">Read more</a> :: 
					</c:if>
					<a rel="comment" class="comment" href="${permUrl}#comments">${commentText}</a>
				</div>
	  	</c:when>
	  	<c:otherwise>	  	
			  ${articleDecorator.formattedText}
	  	</c:otherwise>
	  </c:choose>
	</div>

  <c:if test="${template.summary == 'false'}">
  	<a name="comments"></a>
  	<c:forEach var="commentDecorator" items="${articleDecorator.comments}">
  		<a name="comment-${commentDecorator.comment.id}"></a>
		  <c:url var="deleteCommentLink" value="/comment/delete">
		  	<c:param name="id" value="${commentDecorator.comment.id}" />
		  </c:url>
		  <c:choose>
			  <c:when test="${commentDecorator.comment.createdByUser != null}">
			  	<c:set var="commentAuthor" value="${commentDecorator.comment.createdByUser.userName}" />
			  	<c:set var="commentLink" value="${commentDecorator.comment.createdByUser.website}" />
			  	<c:set var="commentExternal" value="${true}" />
			  </c:when>
			  <c:when test="${commentDecorator.comment.anonymousUserName != null}">
			  	<c:set var="commentAuthor" value="${commentDecorator.comment.anonymousUserName}" />
			  	<c:set var="commentLink" value="${commentDecorator.comment.anonymousWebsite}" />
			  	<c:set var="commentExternal" value="${true}" />
			  </c:when>
			  <c:otherwise>
				  <c:set var="commentAuthor" value="${null}" />			  
			  	<c:set var="commentLink" value="${null}" />
			  	<c:set var="commentExternal" value="${false}" />
			  </c:otherwise>
			</c:choose>
  		<div class="sectionHeading"><c:out value="${commentDecorator.comment.title}" /></div>
			<div class="sectionSubHeading">
				Posted
				<c:if test="${commentAuthor != null}">
					by
					<c:choose>
						<c:when test="${commentLink != null && commentExternal}">
							<a rel="nofollow" class="external" href="${commentLink}"><c:out value="${commentAuthor}" /></a>
						</c:when>
						<c:when test="${commentLink != null && !commentExternal}">
							<a href="${commentLink}"><c:out value="${commentAuthor}" /></a>
						</c:when>
						<c:otherwise>
							<strong><c:out value="${commentAuthor}" /></strong>	
						</c:otherwise>
					</c:choose>
				</c:if>
				on <fmt:formatDate type="date" dateStyle="short" value="${commentDecorator.comment.creationDate}" />
				@ <fmt:formatDate type="time" timeStyle="short" value="${commentDecorator.comment.creationDate}" />
				:: <a href="#comment-${commentDecorator.comment.id}">#<c:out value="${commentDecorator.comment.id}" /></a>
			</div>
			<authz:authorize ifAnyGranted="ROLE_MANAGE_COMMENTS">
				<div class="sectionSubHeading">
					<a rel="delete" class="deleteComment delete" href="${deleteCommentLink}">Delete</a>
				</div>
			</authz:authorize>
  		<div class="sectionContent">
  			${commentDecorator.formattedText}
  		</div>
  	</c:forEach>
  </c:if>
	
</c:forEach>