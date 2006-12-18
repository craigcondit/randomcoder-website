<%-- Tag list --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-escape" prefix="rcesc" %>
<c:url var="homeUrl" value="/" />
<c:url var="addUrl" value="/tag/add" />

<div class="sectionHeading">Tags</div>
<div class="sectionSubHeading">
	<a class="add" href="${addUrl}">Add new tag</a> :: <a href="${homeUrl}">Done</a>
</div>
<div class="sectionContentFull">
	<c:set var="class" value="even" />
	<c:forEach var="tagStat" items="${tagStats}" varStatus="status">	
	  <c:url var="editLink" value="/tag/edit">
	  	<c:param name="id" value="${tagStat.tag.id}" />
	  </c:url>
	  <c:url var="deleteLink" value="/tag/delete">
	  	<c:param name="id" value="${tagStat.tag.id}" />
	  </c:url>
		<div class="${class}">
			<div class="actions">
				<a class="edit" href="${editLink}">Edit</a> :: <a class="delete" href="${deleteLink}">Delete</a>
			</div>
			<a href="${editLink}"><strong><c:out value="${tagStat.tag.displayName}" /></strong></a> (<c:out value="${tagStat.articleCount}" />)
		</div>
		<c:choose>
			<c:when test="${class == 'even'}"><c:set var="class" value="odd" /></c:when>
			<c:otherwise><c:set var="class" value="even" /></c:otherwise>
		</c:choose>		
	</c:forEach>
</div>