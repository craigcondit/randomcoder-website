$(function() {
	$('BUTTON.delete').click(function()
	{
		return confirm("Are you sure you want to delete this user?\nThis action cannot be undone.");
	});
});