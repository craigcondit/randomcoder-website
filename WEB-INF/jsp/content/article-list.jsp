<%-- Article list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<%@ taglib uri="http://randomcoder.com/tags-url" prefix="url" %>

<c:set var="articlePost" value="${false}" />
<sec:inRole role="article-post"><c:set var="articlePost" value="${true}" /></sec:inRole>

<c:set var="articleAdmin" value="${false}" />
<sec:inRole role="article-admin"><c:set var="articleAdmin" value="${true}" /></sec:inRole>

<c:set var="userName" value="" />
<sec:loggedIn><c:set var="userName" value="${pageContext.request.userPrincipal.name}" /></sec:loggedIn>
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
			by <strong><c:out value="${articleAuthor}" /></strong>
		</c:if>
		on <fmt:formatDate type="date" dateStyle="short" value="${articleDecorator.article.creationDate}" />
		@ <fmt:formatDate type="time" timeStyle="short" value="${articleDecorator.article.creationDate}" />
		<c:if test="${articleDecorator.article.modificationDate != null}">
			:: Updated
			<c:if test="${articleDecorator.article.modifiedByUser != null}">
				by <strong><c:out value="${articleDecorator.article.modifiedByUser.userName}" /></strong>
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
	  <sec:loggedIn>		  	
		  <c:if test="${articleAdmin || (articlePost && userName == articleAuthor)}">
				:: <a class="edit" href="${editLink}">Edit</a>
			  :: <a class="delete" href="${deleteLink}">Delete</a>
			</c:if>
		</sec:loggedIn>
	</div>
	<div class="sectionContent">
	  ${articleDecorator.formattedText}
	  <c:if test="${template.showCommentLinks == 'true'}">
			<div class="sectionFooter">
				<c:set var="commentCount" value="${fn:length(articleDecorator.comments)}" />
				<c:choose>
					<c:when test="${commentCount == 1}">
						<c:set var="commentText">1 comment...</c:set>
					</c:when>
					<c:when test="${commentCount > 1}">
						<c:set var="commentText">${commentCount} comments...</c:set>
					</c:when>
					<c:otherwise>
						<c:set var="commentText">Comment on this article...</c:set>
					</c:otherwise>
				</c:choose>
				<a rel="comment" class="comment" href="${permUrl}#comments">${commentText}</a>
			</div>
		</c:if>
	</div>

  <c:if test="${template.showCommentLinks == 'false'}">
  	<a name="comments"></a>
  	<c:forEach var="commentDecorator" items="${articleDecorator.comments}">
  		<a name="comment-${commentDecorator.comment.id}"></a>
		  <c:url var="deleteCommentLink" value="/comment/delete">
		  	<c:param name="id" value="${commentDecorator.comment.id}" />
		  </c:url>
		  <c:choose>
			  <c:when test="${commentDecorator.comment.createdByUser != null}">
			  	<c:set var="commentAuthor" value="${commentDecorator.comment.createdByUser.userName}" />
			  	<%-- TODO this should eventually reference username link --%>
			  	<c:set var="commentLink" value="${null}" />
			  </c:when>
			  <c:when test="${commentDecorator.comment.anonymousUserName != null}">
			  	<c:set var="commentAuthor" value="${commentDecorator.comment.anonymousUserName}" />
			  	<c:set var="commentLink" value="${commentDecorator.comment.anonymousWebsite}" />
			  </c:when>
			  <c:otherwise>
				  <c:set var="commentAuthor" value="${null}" />			  
			  	<c:set var="commentLink" value="${null}" />
			  </c:otherwise>
			</c:choose>
  		<div class="sectionHeading"><c:out value="${commentDecorator.comment.title}" /></div>
			<div class="sectionSubHeading">
				Posted
				<c:if test="${commentAuthor != null}">
					by
					<c:choose>
						<c:when test="${commentLink != null}">
							<a rel="nofollow" href="${commentLink}"><strong><c:out value="${commentAuthor}" /></strong></a>
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
			<sec:inRole role="manage-comments">
				<div class="sectionSubHeading">
					<a rel="delete" class="deleteComment delete" href="${deleteCommentLink}">Delete</a>
				</div>
			</sec:inRole>
  		<div class="sectionContent">
  			${commentDecorator.formattedText}
  		</div>
  	</c:forEach>
  </c:if>
	
</c:forEach>