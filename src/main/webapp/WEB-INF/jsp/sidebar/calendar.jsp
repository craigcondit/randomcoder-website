<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://randomcoder.org/tags-ui" prefix="ui" %>
<%@ taglib uri="http://randomcoder.org/tags-url" prefix="url" %>
<url:modify var="baseUrl">
	<url:removeParam name="month" />
	<url:removeParam name="day" />
	<url:removeParam name="year" />
	<url:removeParam name="start" />
	<url:removeParam name="limit" />
</url:modify>
<c:url context="/" var="baseLink" value="${baseUrl}" />

<jsp:useBean id="today" class="java.util.Date" />
<c:set var="year" value="${param.year}" />
<c:set var="month" value="${param.month}" />
<c:set var="day" value="${param.day}" />
<c:if test="${day == null}"><c:set var="day" value="1" /></c:if>
<c:set var="parseDate" value="${month}/${day}/${year}" />
<c:set var="showDate" value="${today}" />
<c:catch>
	<fmt:parseDate pattern="MM/dd/yyyy" value="${parseDate}" var="showDate" />
</c:catch>
<div class="sectionHeading">
	<ui:calendar-heading
			prevLink="${baseLink}"
			nextLink="${baseLink}"
			showDate="${showDate}" />
</div>
<div class="sectionContent">
  <div align="center">
		<ui:calendar
			  showDate="${showDate}"
				maxWeekdayLength="2"
				tableClass="calendar"
				todayClass="calendar-today"
				selectedClass="calendar-selected"
				weekendClass="calendar-weekend">
			<ui:calendar-day showLink="false" link="${baseUrl}" />
			<c:forEach begin="1" end="31" varStatus="stat">
				<c:if test="${days[stat.index-1]}">
					<ui:calendar-day day="${stat.index}" showLink="true" />
				</c:if>
			</c:forEach>
		</ui:calendar>
	</div>
	<div align="right">
		<a href="${baseLink}">Today</a>
	</div>			
</div>
