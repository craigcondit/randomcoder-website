$(function() {
	$('.focusFirst').first().each(function () {
		var tgt = $(this);
		setTimeout(function () {
			tgt.select().focus();
		}, 1);
	});
	$('BUTTON.deleteArticle').click(function ()
	{
		if (!confirm("Are you sure you want to delete this article?\nAll comments will be deleted as well."))
		{
			return false;
		}
		var group = $(this).closest(".article").first();
		$.ajax({
			type: "DELETE",
			url: $(this).closest("form").get(0).action.replace(/\/delete$/, "")
		}).done(function(msg)
		{
			var items = $('.article').length
			if (items > 1) {
				group.animate({height: 0, opacity: 0}, "slow", function () {
					$(this).remove();
				});
			} else {
				document.location.href = '/';
			}
		});
		return false;
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
			url: $(this).closest("form").get(0).action.replace(/\/delete$/, "")
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
			group.find(".sectionHeading").removeClass('moderated');
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
			group.find(".sectionHeading").addClass('moderated');
			heading.find('BUTTON.disapproveComment').addClass('hidden');
			heading.find('BUTTON.approveComment').removeClass('hidden');
		});
		return false;
	});
});
