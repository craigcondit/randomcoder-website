$(document).ready(function() {
	$('A.deleteArticle').click(function()
	{
		return confirm("Are you sure you want to delete this article?\nAll comments will be deleted as well.");
	});
	$('BUTTON.deleteComment').click(function()
	{		
		if (!confirm("Are you sure you want to delete this comment?\nThis action cannot be undone."))
		{
			return false;
		}
		var button = $(this);
		$.ajax({
			type: "DELETE",
			url: $(this).closest("form").get(0).action
		}).done(function(msg)
		{
			button.closest(".commentGroup").first().animate({ height: 0, opacity: 0}, "slow", function()
			{
				$(this).remove();
			});
		});
		return false;
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