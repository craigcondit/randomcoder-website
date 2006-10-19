<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://randomcoder.com/tags-security" prefix="sec" %>
<sec:notLoggedIn>
	<c:url var="loginUrl" value="/j_security_check" />
	<div class="sectionHeading">Login</div>
	<div class="sectionContent" align="right">
		<form action="${loginUrl}" method="post">
			<div class="fields required">
				<div>
					<label for="j_username">User name</label>
					<input type="text" class="text" name="j_username" id="j_username" value="" />
				</div>
			</div>
			<div class="fields required">
				<div>
					<label for="j_password">Password</label>
					<input type="password" class="password" name="j_password" id="j_password" value="" />
				</div>
			</div>
			<div class="fields">
				<div class="checkbox">
					<input type="checkbox" class="checkbox" name="j_persist" id="j_persist" value="true" />
					<label for="j_persist">Remember me</label>
				</div>
			</div>
			<div class="buttons">
				<input type="submit" class="submit" value="Login &#187;" />
			</div>
		</form>
	</div>
</sec:notLoggedIn>