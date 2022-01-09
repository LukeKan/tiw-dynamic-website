package it.polimi.tiw.plain_html.beans;

import java.util.Date;

public class AuctionBean {
    private final Integer auctionID;
    private final Integer creator;
    private  ProductBean product;
    private final float initialPrice;
    private final float minRaise;
    private Date expiration;
    private Boolean closed;
    private Float maxBid;

    public AuctionBean(Integer auctionID, Integer creator, ProductBean product, float initialPrice, float minRaise, Date expiration, Boolean closed) {
        this.auctionID = auctionID;
        this.creator = creator;
        this.product = product;
        this.initialPrice = initialPrice;
        this.minRaise = minRaise;
        this.expiration = expiration;
        this.closed = closed;
    }

    public Integer getAuctionID() {
        return auctionID;
    }

    public Integer getCreator() {
        return creator;
    }

    public ProductBean getProduct() {
        return product;
    }

    public void setProduct(ProductBean product) {
        this.product = product;
    }

    public float getInitialPrice() {
        return initialPrice;
    }

    public float getMinRaise() {
        return minRaise;
    }

    public Date getExpiration() {
        return expiration;
    }

    public Boolean getClosed() {
        return closed;
    }

    public Float getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(Float maxBid) {
        this.maxBid = maxBid;
    }
}
