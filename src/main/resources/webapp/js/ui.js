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
		var group = $(this).closest(".commentGroup").first();
		$.ajax({
			type: "DELETE",
			url: $(this).closest("form").get(0).action
		}).done(function(msg)
		{
			group.animate({ height: 0, opacity: 0}, "slow", function()
			{
				$(this).remove();
			});
		});
		return false;
	});
	$('BUTTON.approveComment').click(function()
	{
		var group = $(this).closest(".commentGroup").first();
		var heading = $(this).closest(".sectionSubHeading").first();
		$.ajax({
			type: "PUT",
			url: $(this).closest("form").get(0).action
		}).done(function(msg)
		{
			group.find("SPAN.moderated").removeClass('moderated').addClass('active');
			heading.find('BUTTON.approveComment').addClass('hidden');
			heading.find('BUTTON.disapproveComment').removeClass('hidden');
		});
		return false;
	});
	$('BUTTON.disapproveComment').click(function()
	{
		var group = $(this).closest(".commentGroup").first();
		var heading = $(this).closest(".sectionSubHeading").first();
		$.ajax({
			type: "DELETE",
			url: $(this).closest("form").get(0).action
		}).done(function(msg)
		{
			group.find("SPAN.active").removeClass('active').addClass('moderated');
			heading.find('BUTTON.disapproveComment').addClass('hidden');
			heading.find('BUTTON.approveComment').removeClass('hidden');
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