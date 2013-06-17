$(document).ready(function() {
	$('A.deleteArticle').click(function()
	{
		return confirm("Are you sure you want to delete this article?\nAll comments will be deleted as well.");
	});
	$('A.deleteComment, BUTTON.deleteComment').click(function()
	{
		return confirm("Are you sure you want to delete this comment?\nThis action cannot be undone.");
	});
	$('#j_username').each(function()
	{
		if (document.forms && document.forms.length == 1)
		{
			$(this).focus();
			$(this).select();
		}		
	});
});