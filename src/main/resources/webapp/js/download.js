var expandoMap = new Object();
var exceptionMap = new Object();

function isExpandoOpen(el)
{
	var classes = el.className.split(" ");
	for (var i = 0; i < classes.length; i++)
		if (classes[i] == "expando-shown")
			return true;
	return false;
}

function hideExpandoElements(key)
{
	var arr = expandoMap[key];
	if (arr && arr.length);
		for (var i = 0; i < arr.length; i++)
			arr[i].style.display = "none";
}

function showExpandoElements(key)
{
	var arr = expandoMap[key];
	if (arr && arr.length);
	{
		for (var i = 0; i < arr.length; i++)
		{
			var ex = exceptionMap[arr[i].id];
			var allow = true;			
			if (ex && ex.style && !isExpandoOpen(ex))
				allow = false;
			if (allow) arr[i].style.display = "";
		}
	}
}

function addExpandoTarget(key, el)
{
	var arr = expandoMap[key];
	if (!(arr && arr.length))
	{
		expandoMap[key] = arr = new Array();
	}
	arr[arr.length] = el;	
}

$(document).ready(function() {
	$('A.package-expando').click(function()
	{
		var el = $(this).get(0);
		var idParts = el.id.split("-");
		var packageKey = "pkg" + idParts[1];
		if (isExpandoOpen(el))
		{
			el.className = "package-expando expando-hidden";			
			hideExpandoElements(packageKey);
		}
		else
		{
			el.className = "package-expando expando-shown";
			showExpandoElements(packageKey);
		}
		el.blur();
		return false;		
	});
	$('A.fileset-expando').click(function()
	{
		var el = $(this).get(0);
		var idParts = el.id.split("-");
		var filesetKey = "fs" + idParts[1] + "_" + idParts[2];
		if (isExpandoOpen(el))
		{
			el.className = "fileset-expando expando-hidden";			
			hideExpandoElements(filesetKey);
		}
		else
		{
			el.className = "fileset-expando expando-shown";
			showExpandoElements(filesetKey);
		}
		el.blur();
		return false;		
	});
	$('.fileset-target').each(function()
	{
		var el = $(this).get(0);
		var idParts = el.id.split("-");		
		var packageKey = "pkg" + idParts[1];

		// hide if necessary
		var ex = document.getElementById('packageexpand-' + idParts[1]);
		if (!isExpandoOpen(ex)) el.style.display = "none";		
		
		// add to target array
		addExpandoTarget(packageKey, el);		
	});
	$('.file-target').each(function()
	{		
		var el = $(this).get(0);
		var idParts = el.id.split("-");		
		var filesetKey = "fs" + idParts[1] + "_" + idParts[2];
		var packageKey = "pkg" + idParts[1];
		
		// hide if necessary
		var ex = document.getElementById('filesetexpand-' + idParts[1] + "-" + idParts[2]);
		if (!isExpandoOpen(ex)) el.style.display = "none";
		
		// add fileset expander to check list
		exceptionMap[el.id] = ex;
		
		ex = document.getElementById('packageexpand-' + idParts[1]);
		if (!isExpandoOpen(ex)) el.style.display = "none";		
		
		// add to target arrays
		addExpandoTarget(filesetKey, el);
		addExpandoTarget(packageKey, el);
	});
});