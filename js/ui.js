/*
	Common behaviours for all pages on the site.
*/
Behaviour.register({
	'A.delete' : function(el)
	{
		// add confirmation dialogs to all delete links
		el.onclick = function()
		{
			return confirm("Are you sure you want to delete this article?\nAll comments will be deleted as well.");
		};
	},
	'A.deleteComment' : function(el)
	{
		// add confirmation dialogs to all delete links
		el.onclick = function()
		{
			return confirm("Are you sure you want to delete this comment?\nThis action cannot be undone.");
		};
	},
	'#j_username' : function(el)
	{
		// focus the username field of the login form, but only if there is only
		// one form on the page
		if (document.forms && document.forms.length == 1)
		{
			el.focus();
			el.select();
		}
	}
});
