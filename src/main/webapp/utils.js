/**
 * AJAX call management
 */

function makeCall(method, url, formElement=null, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
	  cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
	  req.send();
	} else {
	  req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
	  formElement.reset();
	}
}

function dateDifferenceToString(milliseconds){
	let result = "";
	let seconds = Math.ceil((milliseconds / 1000) % 60 );
	if(seconds !== 0) result = seconds + "s" + result;
	let minutes = Math.ceil(((milliseconds / (1000*60)) % 60));
	if(minutes !== 0) result = minutes + "m " +result;
	let hours   = Math.ceil(((milliseconds / (1000*60*60)) % 24));
	if(hours !== 0) result = hours + "h " +result;
	let days = Math.ceil((milliseconds / (1000*60*60*24)));
	if(days !== 0) result = days + "D " +result;
	return result;
}