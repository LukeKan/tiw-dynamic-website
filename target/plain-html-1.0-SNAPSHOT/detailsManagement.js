DetailsManagement.container = undefined;



function DetailsManagement(pageOrchestrator, container, containerDetailsBody, containerDetailsBids, newBidContainer, closeAuctionContainer){
    DetailsManagement.pageOrchestrator = pageOrchestrator;
    DetailsManagement.container = container;
    DetailsManagement.containerDetailsBody = containerDetailsBody;
    DetailsManagement.containerDetailsBids = containerDetailsBids;
    DetailsManagement.newBidContainer = newBidContainer;
    DetailsManagement.closeAuctionContainer = closeAuctionContainer;

    this.register_events = function(){
        DetailsManagement.container.children[0].addEventListener('click', (e) => {
            this.dismiss();
        })
        document.getElementById("close-auction-button").addEventListener('click', (e) => {
            makeCall(method="POST", url='/tiw-js/auction/close', formElement=e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        let text = req.responseText;
                        switch (req.status) {
                            case 200:
                                DetailsManagement.show(DetailsManagement.id)
                                DetailsManagement.pageOrchestrator.reload();
                                setCookie("lastAction"+window.user["userID"],"CLOSE");
                                window.messageManager.showSuccess("Asta chiusa con successo")
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
        });
        document.getElementById("new-bid-button").addEventListener('click', (e) => {
            makeCall(method="POST", url='/tiw-js/bid/add', formElement=e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        let text = req.responseText;
                        switch (req.status) {
                            case 200:
                                DetailsManagement.add_bid(bid=JSON.parse(req.responseText), top=true);
                                DetailsManagement.pageOrchestrator.reload();
                                window.messageManager.showSuccess("Offerta inserita con successo");
                                setCookie("lastAction"+window.user["userID"],"BID");
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
        });
    }

    DetailsManagement.show = function (id){
        DetailsManagement.id = id;
        this.hide();
        DetailsManagement.container.style.display = "inline";
        makeCall(method="GET", url='/tiw-js/auction/details?id='+id, formElement=null,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let text = req.responseText;
                    switch (req.status) {
                        case 200:
                            let json = JSON.parse(text)
                            let auction = json["auction"];
                            let winnerBid = json["winnerBid"];
                            let elem = document.createElement("div");
                            elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12")
                            elem.innerHTML =
                                "<div class=\"details-img mb-40\">\n" +
                                    "<h1>" + auction["product"]["productID"] + ' - ' + auction["product"]["name"]+"</h1>" +
                                    "<img alt='' src="+(auction["product"]["image"] != null ?
                                        "'data:image/png;base64," + auction["product"]["image"] : "'url(/plain-html/assets/img/logo/logo.png)") + "' style='width: 400px;height: 400px;margin-top: 20px;margin-left: 35%;'/>" +
                                    "<h4>Prezzo iniziale:"+auction["initialPrice"]+" </h4>" +
                                    "<h4>Rilancio minimo:"+auction["minRaise"]+" </h4>" +
                                    (!auction["closed"] ?
                                        "<h4>Offerta massima:"+(auction["maxBid"] == null ? "0.0" : auction["maxBid"]) +"</h4>"
                                    :"") +
                                    "<h4>Scadenza:"+auction["expiration"]+" </h4>" +
                                    (auction["closed"] && winnerBid!=null ?
                                    "<h4>Aggiudicatario: "+winnerBid["userEmail"]+"</h4>" +
                                    "<h4>Prezzo finale: "+winnerBid["value"]+"</h4>" +
                                    "<h4>Indirizzo: "+winnerBid["address"]+"</h4>"
                                    :"") +
                                    "<h4>Descrizione:</h4><p>"+auction["product"]["description"]+"</p>" +
                                "</div>";
                            DetailsManagement.containerDetailsBody.appendChild(elem);

                            let bids = json["bids"]
                            DetailsManagement.containerDetailsBids.innerHTML="";
                            for (const index in bids){
                                let bid = bids[index];
                                DetailsManagement.add_bid(bid)
                            }


                            if (auction["creator"] === window.user["userID"] && !auction["closed"]
                                && new Date() >new Date(auction["expiration"])){
                                DetailsManagement.closeAuctionContainer.style.display = "inline"
                                DetailsManagement.closeAuctionContainer.children[0].children[0].value = auction["auctionID"];
                            } else {
                                DetailsManagement.closeAuctionContainer.style.display = "none"
                            }

                            if (auction["creator"] !== window.user["userID"] && !auction["closed"]){
                                DetailsManagement.newBidContainer.style.display = "inline";
                                DetailsManagement.newBidContainer.children[0].children[0].value = auction["auctionID"];
                            } else {
                                DetailsManagement.newBidContainer.style.display = "none";
                            }

                            if (!auction["closed"]){
                                var seenAuctions = getCookie("seenAuctions-"+window.user["userID"]);
                                if(seenAuctions===""){
                                    seenAuctions = [];
                                } else {
                                    try {
                                        seenAuctions = JSON.parse(seenAuctions);
                                    } catch (e){
                                        seenAuctions = [];
                                    }
                                }
                                let skip = false;
                                for( const index in seenAuctions){
                                    let sA = seenAuctions[index];

                                    if(sA["auctionID"] === auction["auctionID"]){
                                        skip = true;
                                    }
                                }
                                if(!skip){
                                    auction["product"]["image"]="";
                                    seenAuctions=[auction].concat(seenAuctions);
                                    setCookie("seenAuctions-"+window.user["userID"],JSON.stringify(seenAuctions));
                                    DetailsManagement.pageOrchestrator.reload();
                                }
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

    DetailsManagement.add_bid = function (bid, top=false){
        let elem = document.createElement("div");
        elem.classList.add("col-md-12", "col-lg-12", "col-sm-12", "col-xl-12", "row")
        elem.innerHTML =
            "<div class=\"product-id-open col-md-4 border-right border-left\">" +
            "<p>"+bid["userEmail"]+"</p>\n" +
            "</div>" +
            "<div class=\"product-name-open col-md-4 border-right border-left\">" +
            "<p>"+bid["value"]+"</p>\n" +
            "</div>" +
            "<div class=\"product-name-open col-md-4 border-right border-left\">\n" +
            "<p>"+bid["date"]+"</p>\n" +
            "</div>" ;
        if(top === true){
            DetailsManagement.containerDetailsBids.insertBefore(elem, DetailsManagement.containerDetailsBids.firstChild);
        } else {
            DetailsManagement.containerDetailsBids.appendChild(elem);
        }
    }

    this.dismiss = function (){
        DetailsManagement.container.style.display = "none";
    }
    DetailsManagement.hide = function (){
        DetailsManagement.containerDetailsBody.innerHTML = '';
    }
}