<%-- Pager --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.org/tags-ui" prefix="ui" %>
<%-- old style pager (params) --%>
<c:if test="${pageCount > 0 && (pageCount > pageLimit || pageStart > 0)}">
	<div class="pager">
		<ui:pager start="${pageStart}" limit="${pageLimit}" count="${pageCount}" />
	</div>
</c:if>
<%-- new style pager (spring) --%>
<c:if test="${pager != null && pager.totalPages > 1}">
	<div class="pager">
		<ui:pager
			page="${pager.number}" limit="${pager.size}" count="${pager.totalElements}"
			pageParam="page.page" limitParam="page.size" pageOffset="1" /> 
	</div>
</c:if>
