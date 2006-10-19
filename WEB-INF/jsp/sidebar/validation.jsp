<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="xhtmlUrl" value="/images/valid-xhtml10.png" />
<c:url var="cssUrl" value="/images/vcss.png" />
<div class="validation" align="right">
	<a title="Valid XHTML 1.0 Transitional" href="http://validator.w3.org/check?uri=referer"><img
		style="width: 88px; height: 31px"
		src="${xhtmlUrl}"
		alt="Valid XHTML 1.0 Transitional"
		height="31" width="88" /></a>	 
</div>
<div class="validation" align="right">       
	<a title="Valid CSS!" href="http://jigsaw.w3.org/css-validator/"><img
		style="width:88px; height:31px"
		src="${cssUrl}" 
		alt="Valid CSS!" /></a>
</div>