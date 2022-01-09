(function() { // avoid variables ending up in the global scope

    var pageOrchestrator = new PageOrchestrator();
    window.addEventListener("load", () => {
        if (sessionStorage.getItem("user") == null) {
            window.location.href = "login.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.register_events();
        } // display initial content
    }, false);


    function Menu(name, surname, nameContainer, surnameContainer){
        this.name = name;
        this.surname = surname;
        this.show = function (){
            nameContainer.textContent=name;
            surnameContainer.textContent=surname;
        }

        this.register_events = function (orchestrator){
            document.getElementById("sell-menu-button").addEventListener('click', (e) => {
                orchestrator.sell_show();
            });
            document.getElementById("buy-menu-button").addEventListener('click', (e) => {
                orchestrator.buy_show();
            });
        }
    }

    function Message(message_tab){
        this.message_tab = message_tab;

        this.showError = function (message){
            this.message_tab.style.display = "inline";
            this.message_tab.classList.add("alert", "alert-danger", "error")
            this.message_tab[1].value = message;
        }
        this.showSuccess = function (message){
            this.message_tab.style.display = "inline";
            this.message_tab.classList.add("alert", "alert-success")
            this.message_tab[1].value = message;
        }
        this.dismiss = function (){
            this.message_tab.style.display = "none";
        }
        this.register_events = function (){
            this.message_tab[0].addEventListener('click', (e) => {
                this.dismiss();
            })
        }
    }

    function PageOrchestrator(){
        this.user = JSON.parse(sessionStorage.getItem("user"));
        this.start= function (){
            this.menuManager = new Menu(
                this.user["name"],
                this.user["surname"],
                document.getElementById("user-name"),
                document.getElementById("user-surname")
            );
            this.menuManager.show();

            let message_container = document.getElementById("message-container");
            var messageManager = new Message(message_container);

            let buyContainer = document.getElementById("buy-container");
            let searchAuctionsContainer = document.getElementById("search-table-body");
            let wonAuctionsContainer = document.getElementById("won-auctions-body");
            let seenAuctionsContainer = document.getElementById("seen-auctions-body");
            this.buyManager = new BuyManagement(buyContainer, searchAuctionsContainer, wonAuctionsContainer,
                seenAuctionsContainer);

            let sellContainer = document.getElementById("sell-container");
            let openAuctionContainer = document.getElementById("open-auctions-table-body");
            let closedAuctionContainer = document.getElementById("closed-auctions-table-body");
            this.sellManager = new SellManagement(sellContainer, openAuctionContainer, closedAuctionContainer);

            this.menuManager.show();
            //let lastAction = Cookies.getCookie("lastAction");
            //if(lastAction === "SELL"){
            this.sellManager.show();
            //} else {
            //this.buyManager.show();
            //}

        }

        this.sell_show = function (){
            this.sellManager.show();
            this.buyManager.hide();
        }
        this.buy_show = function (){
            this.sellManager.hide();
            this.buyManager.show();
        }

        this.register_events = function (){
            this.menuManager.register_events(this)
            messageManager.register_events();
            this.buyManager.register_events();
            this.sellManager.register_events();
        }

    }
})();
