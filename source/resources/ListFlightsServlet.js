function getAirports(id) {
	req = new AJAX();
	req.GET("/airports", "id="+id+"&"+req.timeStamp(), null);
	if (req.success()) {
		alert(req.responseText());
	}
}
