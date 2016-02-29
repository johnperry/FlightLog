function today() {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var dateInput = document.getElementById("date");
	dateInput.value = year + "." + ((month<10)?"0":"") + month + "." + ((day<10)?"0":"") + day;
}