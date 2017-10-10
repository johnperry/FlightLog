function today() {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var dateInput = document.getElementById("date");
	dateInput.value = year + "." + ((month<10)?"0":"") + month + "." + ((day<10)?"0":"") + day;
}

function deleteImage(flightID, imageName) {
	var ok = confirm("Are you sure you want to delete\n"+imageName+"\nfrom flight "+flightID+"?");
	if (ok) {
		var url = "/addflight?id="+flightID+"&deleteImage="+imageName;
		window.open(url, "_self");
	}
};