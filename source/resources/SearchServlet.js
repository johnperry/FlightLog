function setFocus() {
	document.getElementById("earliestDate").focus();
}
window.onload = setFocus;
function clearForm() {
	var table = document.getElementById("FormTable");
	var inputs = table.getElementsByTagName("INPUT");
	for (var i=0; i<inputs.length; i++) {
		var x = inputs[i];
		if (x.type == "text") x.value = "";
		if (x.type == "checkbox") x.checked = false;
	}
}
