<%-- Login form --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="loginUrl" value="/login-submit" />
<c:url var="homeUrl" value="/" />
<c:url var="createUrl" value="/account/create" />

<c:if test="${template.error}">
	<div class="globalError">Login incorrect. Please try again.</div>
</c:if>

<div class="sectionHeading">Login</div>
<div class="sectionContent">
	<form action="${loginUrl}" method="post">
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
				<input type="checkbox" name="_spring_security_remember_me" class="checkbox" id="persist" value="true" />
			</div>
		</div>
		<div class="fields">
			<div class="buttons">					
				<input style="margin-right: 5px" type="submit" class="formSubmit" value="Login &#187;" />
				<input type="button" class="formButton" value="Cancel" onclick="document.location.href='${homeUrl}'" />
			</div>
		</div>
		
		<p>
			Don't have an account? <a href="${createUrl}">Sign up now</a>.
		</p>
		
	</form>
</div>