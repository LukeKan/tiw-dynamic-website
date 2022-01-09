package it.polimi.tiw.plain_html.exceptions;

public class BidException extends Exception{
    private int auctionID;

    public BidException(String message) {
        super(message);
    }

    public BidException(String message, int auctionID) {
        super(message);
        this.auctionID = auctionID;
    }

    public int getAuctionID() {
        return auctionID;
    }

}
