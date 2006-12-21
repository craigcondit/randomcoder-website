<%-- Login form --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="loginUrl" value="/j_security_check" />
<c:url var="homeUrl" value="/" />
<div class="sectionHeading">Login</div>
<div class="sectionContent">
	<form action="${loginUrl}" method="post">
 	  <c:if test="${template.error}">
			<div class="globalError">Invalid username or password.</div></td>
		</c:if>
		<div class="fields required">
			<div>					
				<label for="username">User name:</label>
				<input type="text" name="j_username" class="text" id="username" value="" />
			</div>
		</div>
		<div class="fields required">
			<div>					
				<label for="password">Password:</label>
				<input type="password" name="j_password" class="password" id="password" value="" />
			</div>
		</div>
		<div class="fields">
			<div class="checkbox">					
				<label for="persist">Remember me</label>
				<input type="checkbox" name="j_persist" class="checkbox" id="persist" value="true" />
			</div>
		</div>
		<div class="fields">
			<div class="buttons">					
				<input style="margin-right: 5px" type="submit" class="formSubmit" value="Login &#187;" />
				<input type="button" class="formButton" value="Cancel" onclick="document.location.href='${homeUrl}'" />
			</div>
		</div>
	</form>
</div>

<div class="sectionHeading">CardSpace Login</div>
<div class="sectionContent">
	<form name="infocard" id="infocard" method="post" action="${pageContext.request.contextPath}/j_cardspace_check" id="infocard">
		<img
			style="cursor: pointer; cursor: hand; width: 100px; height: 86px"
			src="${pageContext.request.contextPath}/images/informationcard.gif"
    		onclick="document.getElementById('infocard').submit()" />
		<object type="application/x-informationCard" name="xmlToken">
			<param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion" />
			<param name="requiredClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
			<param name="optionalClaims" value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender http://schemas.xmlsoap.org/ws/2005/05/identity/claims/webpage" />
		</object>
	</form>	

</div>