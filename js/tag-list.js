Behaviour.register({
	'A.delete' : function(el)
	{
		el.onclick = function()
		{
			return confirm("Are you sure you want to delete this tag?\nThis action cannot be undone.");
		};
	}
});
