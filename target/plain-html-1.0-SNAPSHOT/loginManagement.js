(function() { // avoid variables ending up in the global scope

    document.getElementById("loginbutton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", '/tiw-js/user/login', e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem('user', message);
                                sessionStorage.setItem('loginTime',new Date().toDateString());
                                window.location.href = "index.html";
                                break;
                            case 400: // bad request
                            case 500: // Server error
                            case 404: // Not Found
                                document.getElementById("error").classList.add("alert", "alert-danger", "error")
                                document.getElementById("errormessage").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

    document.getElementById("registerbutton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", '/tiw-js/user/register', e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {
                            case 200:
                                sessionStorage.setItem('user', message);
                                sessionStorage.setItem('loginTime',new Date().toDateString());
                                window.location.href = "index.html";
                                break;
                            case 400: // bad request
                            case 500: // Server error
                            case 404: // Not Found
                                document.getElementById("error").classList.add("alert", "alert-danger", "error")
                                document.getElementById("errormessage").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

})();