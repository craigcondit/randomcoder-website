<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<sec:notLoggedIn>
	<c:url var="loginUrl" value="/j_security_check" />
	<div class="sectionHeading">Login</div>
	<div class="sectionContent" align="right">
		<form action="${loginUrl}" method="post">
			<div style="margin-bottom: 1em">
				<label class="required" for="username">User name</label><br />
				<input type="text" style="text-align: right" name="j_username" id="username" class="formText" value="" />
			</div>
			<div style="margin-bottom: 1em">
				<label class="required" for="password">Password</label><br />
				<input type="password" style="text-align: right" name="j_password" id="password" class="formPassword" value="" />
			</div>
			<div style="margin-bottom: 1em">
				<label class="optional" for="persist">
					<input type="checkbox" name="j_persist" id="persist" class="formCheckbox" value="true" />
					Remember me
				</label>
			</div>
			<input type="submit" class="formSubmit" value="Login &#187;" />
		</form>
	</div>
</sec:notLoggedIn>