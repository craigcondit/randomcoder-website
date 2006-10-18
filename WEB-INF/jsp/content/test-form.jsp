<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://randomcoder.com/tags-input" prefix="input" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:url var="homeUrl" value="/" />
<div class="sectionHeading">${template.title}</div>
<div class="sectionContent">
  <form method="post" action="">
  
		<div class="fields required">
			<div>
				<label for="text1">Text 1:</label>
				<input type="text" class="text" name="text1" id="text1" value="Text 1" />
			</div>
			<div class="description">
				This value is used for the text.
			</div>
		</div>
		
		<div class="fields error">
			<div>				
				<span class="error">Text 2 is required.</span>
			</div>
			<div>
				<label for="text2">Text 2:</label>
				<input type="text" class="text" name="text2" id="text2" value="Text 2" />
			</div>
			<div class="description">
				This value is used for the text.
			</div>
		</div>
		
		<div class="fields">
			<div>
				<label for="pass1">Pass 1:</label>
				<input type="password" class="password" name="pass1" id="pass1" value="Pass 1" />
			</div>
		</div>
		
		<div class="fields required error">
			<div>				
				<span class="error">Pass 2 is required.</span>
			</div>
			<div>
				<label for="pass2">Pass 2:</label>
				<input type="password" class="password" name="pass2" id="pass2" value="Pass 2" />
			</div>
		</div>
  
		<div class="fields">
			<div>
				<fieldset class="checkbox">
					<legend>Checkboxes</legend>
					<div>
						<label for="check1">Check 1</label>
						<input type="checkbox" class="checkbox" name="check1" id="check1" value="true" />
					</div>
					<div>
						<label for="check2">Check 2</label>
						<input type="checkbox" class="checkbox" name="check2" id="check2" value="true" />
					</div>
				</fieldset>
			</div>
		</div>

		<div class="fields error">
			<div>
				<fieldset class="checkbox">
					<legend>Checkboxes (error)</legend>
					<div>
						<span class="error">Please select an option.</span>
					</div>
					<div class="description">This is a test.</div>
					<div>
						<label for="check3">Check 3</label>
						<input type="checkbox" class="checkbox" name="check3" id="check3" value="true" />
					</div>
					<div>
						<label for="check4">Check 4</label>
						<input type="checkbox" class="checkbox" name="check4" id="check4" value="true" />
					</div>
				</fieldset>
			</div>
		</div>

		<div class="fields">
			<div>
				<fieldset class="radio">
					<legend>Radio buttons</legend>
					<div>
						<label for="radio1a">Radio 1a</label>
						<input type="radio" class="checkbox" name="radio1" id="radio1a" value="a" />
					</div>
					<div>
						<label for="radio1b">Radio 1b</label>
						<input type="radio" class="checkbox" name="radio1" id="radio1b" value="b" />
					</div>
				</fieldset>
			</div>
		</div>
		
		<div class="fields error required">
			<div>
				<fieldset class="radio">
					<legend>Radio buttons (error / required)</legend>
					<div>
						<span class="error">Please select an option.</span>
					</div>
					<div class="description">This is a test.</div>
					<div>
						<label for="radio2a">Radio 2a</label>
						<input type="radio" class="checkbox" name="radio2" id="radio2a" value="a" />
					</div>
					<div>
						<label for="radio2b">Radio 2b</label>
						<input type="radio" class="checkbox" name="radio2" id="radio2b" value="b" />
					</div>
				</fieldset>
			</div>
		</div>
  	
		<div class="fields">
			<div>
				<label for="select1">Select 1:</label>
				<select name="select1" id="select1">
					<option value="">Select an option...</option>
					<option value="1">Option 1</option>
					<option value="2">Option 2</option>
				</select>
			</div>
		</div>

		<div class="fields required error">
			<div>				
				<span class="error">Select 2 is required.</span>
			</div>
			<div>
				<label for="select1">Select 2:</label>
				<select name="select2" id="select2">
					<option value="">Select an option...</option>
					<option value="1">Option 1</option>
					<option value="2">Option 2</option>
				</select>
			</div>
		</div>
  	
  	<div class="fields">
  		<div>
  			<label for="textarea1">Textarea 1:</label>
  			<textarea name="textarea1" id="textarea1">This is a test</textarea>
  		</div>  	
  	</div>
  	
  	<div class="fields required error">
			<div>				
				<span class="error">Textarea 2 is required.</span>
			</div>
  		<div>
  			<label for="textarea2">Textarea 2:</label>
  			<textarea name="textarea2" id="textarea2">This is a test</textarea>
  		</div>  	
  	</div>

  	<div class="fields long">
  		<div>
  			<label for="textarea3">Textarea 3:</label>
  			<textarea name="textarea3" id="textarea3">This is a test</textarea>
  		</div>  	
  	</div>
  	
  	<div class="fields long required error">
			<div>				
				<span class="error">Textarea 4 is required.</span>
			</div>
  		<div>
  			<label for="textarea4">
  				Textarea 4:
					<span class="description">
						This value is used for the text.
					</span>
  			</label>
  			<textarea name="textarea4" id="textarea4" rows="10">This is a test</textarea>
  		</div>  	
  	</div>
  	
  </form>
</div>