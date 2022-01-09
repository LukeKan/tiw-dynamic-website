BuyManagement.search_buy_container = undefined;
BuyManagement.won_auctions_container = undefined;

function BuyManagement(container, search_buy_container, won_auctions_container, seen_auctions_container){
    BuyManagement.container = container;
    BuyManagement.search_buy_container = search_buy_container;
    BuyManagement.won_auctions_container = won_auctions_container;
    BuyManagement.seen_auctions_container = seen_auctions_container;
    this.register_events = function (){
        document.getElementById("search-button").addEventListener('click', (e) => {
            var form = e.target.closest("form");
            if (form.checkValidity()) {
                this.refresh(form[0].value)
                setCookie("lastAction"+window.user["userID"],"SEARCH");
            } else {
                form.reportValidity();
            }
        });
    }

    this.show = function (){
        BuyManagement.container.style.display = "inline";
        let searchForm = document.getElementById("search-form");
        this.refresh(searchForm[0].value);
    }

    this.hide = function (){
        BuyManagement.container.style.display = "none";
    }

    this.refresh = function (form_search=null){
        makeCall(method="GET", url='/tiw-js/buy?keyword='+form_search, formElement=null,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let text = req.responseText;

                    switch (req.status) {
                        case 200:
                            let json = JSON.parse(text);
                            let auctionSearchRes = json["auctionSearchRes"];
                            let winnerAuctions = json["winnerAuctions"];
                            BuyManagement.search_buy_container.innerHTML = '';
                            BuyManagement.won_auctions_container.innerHTML = '';
                            BuyManagement.seen_auctions_container.innerHTML = '';
                            for (const index in auctionSearchRes) {
                                let auction = auctionSearchRes[index];
                                let elem = document.createElement("div");
                                elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12")
                                elem.innerHTML =
                                    "<div class='table-row container' >" +
                                    "    <a href='#' onclick='popupDisplay("+ auction['auctionID'] + ")' class=\"col-md-12 row\">" +
                                    "         <div class='product-id-open col-md-3 border-right border-left'>" +
                                    "            <p>"+auction["product"]["productID"]+"</p>" +
                                    "        </div>" +
                                    "        <div class='product-name-open col-md-3 border-right border-left'>" +
                                    "            <p>"+auction["product"]["name"]+"</p>" +
                                    "        </div>" +
                                    "        <div class='max-bid-open col-md-3 border-right border-left'>" +
                                    "            <p>"+auction["maxBid"]+"</p>" +
                                    "        </div>" +
                                    "        <div class='expiration-open col-md-3 border-right border-left'>" +
                                    "            <p>"+dateDifferenceToString(new Date(auction["expiration"]).getTime()-window.loginTime.getTime())+"</p>" +
                                    "        </div>" +
                                    "    </a>" +
                                    "</div>";
                                BuyManagement.search_buy_container.appendChild(elem);
                            }
                            for (const index in winnerAuctions){
                                let auction = winnerAuctions[index];
                                let elem = document.createElement("div");
                                elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12")
                                elem.innerHTML =
                                    "<div class='table-row container' >" +
                                    "  <a href='#' onclick='popupDisplay("+ auction['auctionID']+")' class='col-md-12 row '>" +
                                    "    <div class='product-id-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["product"]["productID"]+"</p>" +
                                    "    </div>" +
                                    "    <div class='product-name-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["product"]["name"]+"</p>" +
                                    "    </div>" +
                                    "    <div class='max-bid-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["maxBid"]+"</p>" +
                                    "    </div>" +
                                    "   </a>" +
                                    " </div>";
                                BuyManagement.won_auctions_container.appendChild(elem);
                            }
                            let seenAuctions = JSON.parse(getCookie("seenAuctions-"+window.user["userID"]));
                            for (const index in seenAuctions){
                                let auction = seenAuctions[index];
                                makeCall(method="GET", url='/tiw-js/auction/details?id='+auction["auctionID"], formElement=null,
                                    function(req) {
                                        if (req.readyState == XMLHttpRequest.DONE) {
                                            let text = req.responseText;
                                            let auct = JSON.parse(text);
                                            switch (req.status) {
                                                case 200:
                                                    if (auct["auction"]["closed"]){
                                                        seenAuctions.splice(index,1);
                                                        setCookie("seenAuctions-"+window.user["userID"],JSON.stringify(seenAuctions));
                                                    } else{
                                                        let elem = document.createElement("div");
                                                        elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12")
                                                        elem.innerHTML =
                                                            "<div class='table-row container' >" +
                                                            "  <a href='#' onclick='popupDisplay("+ auction['auctionID']+")' class='col-md-12 row '>" +
                                                            "    <div class='product-id-closed col-md-4 border-right border-left'>" +
                                                            "      <p>"+auction["product"]["productID"]+"</p>" +
                                                            "    </div>" +
                                                            "    <div class='product-name-closed col-md-4 border-right border-left'>" +
                                                            "      <p>"+auction["product"]["name"]+"</p>" +
                                                            "    </div>" +
                                                            "    <div class='max-bid-closed col-md-4 border-right border-left'>" +
                                                            "      <p>"+auction["maxBid"]+"</p>" +
                                                            "    </div>" +
                                                            "   </a>" +
                                                            " </div>";
                                                        BuyManagement.seen_auctions_container.appendChild(elem);
                                                    }
                                                    break;
                                                case 400: // bad request
                                                case 500: // Server error
                                                case 404: // Not Found
                                                    seenAuctions.splice(index,1);
                                                    setCookie("seenAuctions-"+window.user["userID"],JSON.stringify(seenAuctions));
                                                    break;
                                            }
                                        }
                                    }
                                );
                            }
                            break;
                        case 400: // bad request
                        case 500: // Server error
                        case 404: // Not Found
                            window.messageManager.showError(text);
                            break;
                    }
                }
            }
        , reset=false);
    }
}