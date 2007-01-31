<%-- Download --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="sectionHeading">Latest releases</div>
<div class="sectionContentFull">
	<table style="width: 100%" class="data">
		<col width="40%" />
		<col width="30%" />
		<col width="30%" />
		<thead>
			<tr>
				<th>Package</th>
				<th style="text-align: center">Release</th>
				<th style="text-align: center">Date</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="class" value="even" />
			<c:forEach var="package" items="${packages}" varStatus="packageStat">
				<tr class="${class}">
					<td><a href="#download${packageStat.index}"><c:out value="${package.name}" /></a></td>
					<td style="text-align: center"><c:out value="${package.fileSets[0].version}" /></td>						
					<td style="text-align: center"><fmt:formatDate pattern="MMMM d, yyyy" value="${package.fileSets[0].files[0].lastModified}" /></td>
				</tr>									
				<c:choose>
					<c:when test="${class == 'even'}"><c:set var="class" value="odd" /></c:when>
					<c:otherwise><c:set var="class" value="even" /></c:otherwise>
				</c:choose>
			</c:forEach>
		</tbody>
	</table>
</div>
<div class="sectionHeading">File releases</div>
<div class="sectionContentFull">
	<table style="width: 100%" class="data">
		<col width="10%" />
		<col width="10%" />
		<col width="45%" />
		<col width="15%" />
		<col width="20%" />
		<thead>
			<tr>
				<th>Package</th>
				<th>Release</th>
				<th>Filename</th>
				<th style="text-align: center">Size</th>
				<th>Type</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="package" items="${packages}" varStatus="packageStat">
				<tr class="row-heading">
					<td colspan="5">
						<a name="download${packageStat.index}"></a>
						<a id="packageexpand-${packageStat.index}" class="package-expando expando-shown" href="#"><c:out value="${package.name}" /></a>
					</td>
				</tr>
				<c:forEach var="fileSet" items="${package.fileSets}" varStatus="fileSetStat">
					<c:choose>
						<c:when test="${fileSetStat.index == 0}"><c:set var="versionClass" value="expando-shown" /></c:when>
						<c:otherwise><c:set var="versionClass" value="expando-hidden" /></c:otherwise>
					</c:choose>
					<tr class="fileset-target row-subheading" id="fileset-${packageStat.index}-${fileSetStat.index}">
					  <td>&#160;</td>
						<td colspan="4">
							<a class="fileset-expando ${versionClass}" id="filesetexpand-${packageStat.index}-${fileSetStat.index}" href="#"><c:out value="${fileSet.version}" /></a>
							<c:if test="${not empty fileSet.files[0].lastModified}">
								(<fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${fileSet.files[0].lastModified}" />)
							</c:if>
						</td>
					</tr>
					<c:set var="class" value="even" />
					<c:forEach var="file" items="${fileSet.files}" varStatus="fileStat">
						<c:set var="fileTypeKey" value="filetype.${file.fileType}" />
						<tr class="file-target ${class}" id="file-${packageStat.index}-${fileSetStat.index}-${fileStat.index}">
							<td>&#160;</td>
							<td>&#160;</td>
							<td><a href="${file.downloadLink}"><c:out value="${file.fileName}" /></a></td>
							<td style="text-align: center"><c:out value="${file.fileSize}" /></td>
							<td><fmt:message key="${fileTypeKey}" /></td>
						</tr>
						<c:choose>
							<c:when test="${class == 'even'}"><c:set var="class" value="odd" /></c:when>
							<c:otherwise><c:set var="class" value="even" /></c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table>
</div>