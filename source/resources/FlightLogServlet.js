var user = null;
var split = null;

function loaded() {
	setSize();
	user = new User();
	if ((user == null) || !user.isLoggedIn) showLoginPopup('/flightlog');
}
window.onload = loaded;

function setSize() {
	var bodyPos = findObject(document.body);
	var left = document.getElementById("left");
	var right = document.getElementById("right");
	var leftPos = findObject(left);
	var rightPos = findObject(right);
	var h = bodyPos.h - leftPos.y - 10;
	var w = bodyPos.w - leftPos.w -5;
	left.style.height = h;
	right.style.height = h;
	right.style.width = w;
	right.style.top = leftPos.y;
	right.style.left = leftPos.w;
}
window.onresize = setSize;

//Load a servlet in the servlet pane
function loadFrame(url) {
	var right = document.getElementById("right");
	while (right.firstChild) right.removeChild(right.firstChild);
	var iframe = document.createElement("IFRAME");
	iframe.src = url;
	right.appendChild(iframe);
	resize();
}
