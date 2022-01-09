package it.polimi.tiw.plain_html.beans;

import java.util.Date;

public final class BidBean implements Comparable<BidBean>{
    private Integer bidID;
    private Integer userID;
    private String userEmail;
    private Integer auctionID;
    private float value;
    private Date date;
    private String address;

    public BidBean(Integer bidID, Integer userID, Integer auctionID, float value, Date date, String address) {
        this.bidID = bidID;
        this.userID = userID;
        this.auctionID = auctionID;
        this.value = value;
        this.date = date;
        this.address = address;
    }

    public Integer getBidID() {
        return bidID;
    }

    public Integer getUserID() {
        return userID;
    }

    public Integer getauctionID() {
        return auctionID;
    }

    public float getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public int compareTo(BidBean o) {
        if(value < o.getValue()) return -1;
        else if (value > o.getValue()) return 1;
        return 0;
    }
}
