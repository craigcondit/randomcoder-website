<%-- Pager --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.org/tags-ui" prefix="ui" %>
<c:if test="${pageCount > 0 && (pageCount > pageLimit || pageStart > 0)}">
	<div class="pager">
		<ui:pager start="${pageStart}" limit="${pageLimit}" count="${pageCount}" />
	</div>
</c:if>
