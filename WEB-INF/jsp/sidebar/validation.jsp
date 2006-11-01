<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="xhtmlUrl" value="/images/badges/w3c-xhtml.png" />
<c:url var="cssUrl" value="/images/badges/css-valid.png" />
<div class="validation" align="left">
	<a title="Valid XHTML 1.0 Transitional" href="http://validator.w3.org/check?uri=referer"><img
		style="width: 80px; height: 15px"
		src="${xhtmlUrl}"
		alt="Valid XHTML 1.0 Transitional"
		height="31" width="88" /></a>
</div>
<div class="validation" align="left">
	<a title="Valid CSS" href="http://jigsaw.w3.org/css-validator/"><img
		style="width:80px; height:15px"
		src="${cssUrl}" 
		alt="Valid CSS" /></a>
</div>