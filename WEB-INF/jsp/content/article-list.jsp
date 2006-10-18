<%-- Article list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>

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
	<div class="sectionContent">
	  ${articleDecorator.formattedText}
		<div class="sectionFooter">
			posted
			<c:if test="${articleAuthor != null}">
				by <c:out value="${articleAuthor}" />
			</c:if>
			on <fmt:formatDate type="date" dateStyle="short" value="${articleDecorator.article.creationDate}" />
			@ <fmt:formatDate type="time" timeStyle="short" value="${articleDecorator.article.creationDate}" /><br />
			<c:if test="${articleDecorator.article.modificationDate != null}">
				last updated
				<c:if test="${articleDecorator.article.modifiedByUser != null}">
					by <c:out value="${articleDecorator.article.modifiedByUser.userName}" />
				</c:if>
				on <fmt:formatDate type="date" dateStyle="short" value="${articleDecorator.article.modificationDate}" />
				@ <fmt:formatDate type="time" timeStyle="short" value="${articleDecorator.article.modificationDate}" /><br />
			</c:if>
			<c:if test="${fn:length(articleDecorator.article.tags) > 0}">
			  filed under
			  <c:forEach var="tag" items="${articleDecorator.article.tags}" varStatus="tagStat">
			    <c:url var="tagLink" value="/tags/${rcesc:urlencode(tag.name)}" />
			    :: <a href="${tagLink}"><c:out value="${tag.displayName}" /></a></c:forEach><br />
			</c:if>
			<c:choose>
				<c:when test="${not empty articleDecorator.article.permalink}">
					<c:url var="permUrl" value="/articles/${rcesc:urlencode(articleDecorator.article.permalink)}" />
				</c:when>
				<c:otherwise>
					<c:url var="permUrl" value="/articles/id/${articleDecorator.article.id}" />
				</c:otherwise>
			</c:choose>
			<a href="${permUrl}">permalink</a>
		  <sec:loggedIn>		  	
			  <c:if test="${articleAdmin || (articlePost && userName == articleAuthor)}">
					:: <a href="${editLink}">edit</a>
				  :: <a href="${deleteLink}">delete</a>
				</c:if>
			</sec:loggedIn>
		</div>	  
	</div>
</c:forEach>