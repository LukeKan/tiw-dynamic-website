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
            this.message_tab.className = "";
            this.message_tab.classList.add("alert", "alert-danger", "error")
            this.message_tab.children[1].textContent = message;
        }
        this.showSuccess = function (message){
            this.message_tab.style.display = "inline";
            this.message_tab.className = "";
            this.message_tab.classList.add("alert", "alert-success")
            this.message_tab.children[1].textContent = message;
        }
        this.dismiss = function (){
            this.message_tab.style.display = "none";
        }
        this.register_events = function (){
            this.message_tab.children[0].addEventListener('click', (e) => {
                this.dismiss();
            })
        }
    }

    function PageOrchestrator(){
        window.user = JSON.parse(sessionStorage.getItem("user"));
        window.loginTime = new Date(sessionStorage.getItem("loginTime"));
        this.start= function (){
            let name_tab = document.getElementById("user-name")
            let surname_tab = document.getElementById("user-surname")
            this.menuManager = new Menu(
                window.user["name"],
                window.user["surname"],
                name_tab,
                surname_tab
            );
            this.menuManager.show();

            let message_container = document.getElementById("message-container");
            window.messageManager = new Message(message_container);

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

            let detailsContainer = document.getElementById("details-popup");
            let detailsInfoContainer = document.getElementById("details-info-body");
            let detailsBidsContainer = document.getElementById("details-bids-body");
            let newBidContainer = document.getElementById("new-bid-container");
            let closeAuctionContainer = document.getElementById("close-auction-container");
            this.detailsManager = new DetailsManagement(pageOrchestrator,detailsContainer,detailsInfoContainer,detailsBidsContainer,newBidContainer,closeAuctionContainer)

            this.menuManager.show();
            let lastAction = getCookie("lastAction"+window.user["userID"]);
            if(lastAction === "SELL"){
                this.sellManager.show();
            } else {
                this.buyManager.show();
            }
            this.page=lastAction;
        }

        this.sell_show = function (){
            this.sellManager.show();
            this.buyManager.hide();
            this.page="SELL";
        }
        this.buy_show = function (){
            this.sellManager.hide();
            this.buyManager.show();
            this.page="BUY";
        }

        this.register_events = function (){
            this.menuManager.register_events(this)
            window.messageManager.register_events();
            this.buyManager.register_events();
            this.sellManager.register_events();
            this.detailsManager.register_events();
        }

        this.reload = function (){
            if (this.page === "SELL"){
                this.sell_show();
            } else {
                this.buy_show();
            }
        }
    }
})();
function popupDisplay(id){
    DetailsManagement.show(id);
}