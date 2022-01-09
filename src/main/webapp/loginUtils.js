function toggleForm(){
    if(document.getElementById("login-container").style.display === "none"){
        document.getElementById("login-container").style.display = "inline";
        document.getElementById("register-container").style.display = "none";
    } else {
        document.getElementById("login-container").style.display = "none";
        document.getElementById("register-container").style.display = "inline";
    }
}