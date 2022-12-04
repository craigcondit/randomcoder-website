$(document).ready(function() {
	$('BUTTON.delete').click(function()
	{
		return confirm("Are you sure you want to delete this tag?\nThis action cannot be undone.");
	});
});