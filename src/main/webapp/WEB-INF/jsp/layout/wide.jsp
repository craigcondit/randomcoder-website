<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en_US">
	<head>
		<c:set var="pageTitle">randomCoder</c:set>
		<c:if test="${template.title != null}">
			<c:set var="pageTitle">${pageTitle} : ${template.title}</c:set>
		</c:if>
		<c:if test="${pageSubTitle != null}">
			<c:set var="pageTitle">${pageTitle} : ${pageSubTitle}</c:set>
		</c:if>
		<title><c:out value="${pageTitle}" /></title>
		<link rel="favorite icon" href="${pageContext.request.contextPath}/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/site.css" />
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/entry.css" />
		<%-- be nice to IE --%>
		<!--[if IE]>
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/site-ie.css" />
		<![endif]-->
		<%-- be nice to pre-IE 7 --%>
		<!--[if lt IE 7.]>
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/site-iepre7.css" />
		<![endif]-->
		<%-- be nice to IE 5.x --%>
		<!--[if lt IE 6]>
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/site-ie5.css" />
		<![endif]-->
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/behaviour.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
		<c:if test="${template.javascript != null}">
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/${template.javascript}"></script>
		</c:if>
		<c:if test="${template.javascripts != null}">
			<c:forEach var="javascript" items="${template.javascripts}">
				<script type="text/javascript" src="${pageContext.request.contextPath}/js/${javascript}"></script>
			</c:forEach>
		</c:if>
		<c:if test="${template.head != null}">
			<jsp:include page="${template.head}" flush="true" />  
		</c:if>
	</head>
	<body>
		<c:if test="${template.header != null}">
			<div id="header"><jsp:include page="${template.header}" flush="true" /></div>
		</c:if>
	  
		<div id="left">
			<c:if test="${template.left != null}">
				<c:forEach var="leftPage" items="${template.left}">
					<div class="left"><jsp:include page="${leftPage}" flush="true" /></div>
				</c:forEach>
			</c:if>
		</div>
	  
		<div id="content">
			<c:if test="${template.content != null}">
				<c:forEach var="contentPage" items="${template.content}">
					<div class="content"><jsp:include page="${contentPage}" flush="true" /></div>
				</c:forEach>
			</c:if>
			
			<c:if test="${template.footer != null}">
				<div id="footer"><jsp:include page="${template.footer}" flush="true" /></div>
			</c:if>			
		</div>
	</body>
</html>
