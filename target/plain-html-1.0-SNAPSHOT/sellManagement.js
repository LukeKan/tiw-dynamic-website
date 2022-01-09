SellManagement.openAuctionContainer = undefined;
SellManagement.closedAuctionContainer = undefined;

function SellManagement(container, openAuctionContainer, closedAuctionContainer){
    SellManagement.container = container;
    SellManagement.openAuctionContainer = openAuctionContainer;
    SellManagement.closedAuctionContainer = closedAuctionContainer;

    this.register_events = function(){
        document.getElementById("new-auction-button").addEventListener('click', (e) => {
            var form = e.target.closest("form");
            if (form.checkValidity()) {
                this.add(form);
                setCookie("lastAction"+window.user["userID"],"SELL");
            } else {
                form.reportValidity();
            }
        });
    }

    this.show = function (){
        SellManagement.container.style.display = "inline";
        makeCall(method="GET", url='/tiw-js/sell', formElement=null,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let text = req.responseText;
                    switch (req.status) {
                        case 200:
                            let json = JSON.parse(text);
                            let openAuctions = json["open"];
                            let closedAuctions = json["closed"];
                            SellManagement.closedAuctionContainer.innerHTML = '';
                            SellManagement.openAuctionContainer.innerHTML = '';
                            for (const index in openAuctions) {
                                let auction = openAuctions[index];
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
                                SellManagement.openAuctionContainer.appendChild(elem);
                            }
                            for (const index in closedAuctions){
                                let auction = closedAuctions[index];
                                let elem = document.createElement("div");
                                elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12")
                                elem.innerHTML =
                                    "<div class='table-row container' >" +
                                    "  <a href='#' onclick='popupDisplay("+ auction['auctionID']+")' class='col-md-12 row'>" +
                                    "    <div class='product-id-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["product"]["productID"]+"</p>" +
                                    "    </div>" +
                                    "    <div class='product-name-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["product"]["name"]+"</p>" +
                                    "    </div>" +
                                    "    <div class='max-bid-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["maxBid"]+"</p>" +
                                    "    </div>" +
                                    "    <div class='expiration-closed col-md-3 border-right border-left'>" +
                                    "      <p>"+auction["expiration"]+"</p>" +
                                    "    </div>" +
                                    "   </a>" +
                                    " </div>";

                                SellManagement.closedAuctionContainer.appendChild(elem);
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
        );
    }

    this.hide = function (){
        SellManagement.container.style.display = "none";
    }
    this.refresh = function (){
        SellManagement.closedAuctionContainer.innerHTML = '';
        SellManagement.openAuctionContainer.innerHTML = '';
    }

    this.add = function (form){
        makeCall(method="POST", url='/tiw-js/auction/add', formElement=form,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let text = req.responseText;
                    switch (req.status) {
                        case 200:
                            let auction = JSON.parse(text);
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
                                "            <p>0</p>" +
                                "        </div>" +
                                "        <div class='expiration-open col-md-3 border-right border-left'>" +
                                "            <p>"+dateDifferenceToString(new Date(auction["expiration"]).getTime()-window.loginTime.getTime())+"</p>" +
                                "        </div>" +
                                "    </a>" +
                                "</div>";
                            SellManagement.openAuctionContainer.appendChild(elem);
                            window.messageManager.showSuccess("Asta aggiunta con successo.");
                            break;
                        case 400: // bad request
                        case 500: // Server error
                        case 404: // Not Found
                            window.messageManager.showError(text);
                            break;
                    }
                }
            }
        );
    }
}