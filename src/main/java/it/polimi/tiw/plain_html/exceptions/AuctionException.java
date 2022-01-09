package it.polimi.tiw.plain_html.exceptions;

public class AuctionException extends Exception{
    private int auctionID;
    public AuctionException(String message) {
        super(message);
    }
    public AuctionException(String message, int auctionID) {
        super(message);
        this.auctionID = auctionID;
    }

    public int getAuctionID() {
        return auctionID;
    }
}
