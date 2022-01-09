package it.polimi.tiw.plain_html.dao;

import it.polimi.tiw.plain_html.beans.BidBean;
import it.polimi.tiw.plain_html.exceptions.BidException;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BidDao {
    private Connection connection;

    public BidDao(Connection connection) {
        this.connection = connection;
    }

    public List<BidBean> getBidsOfAuction(Integer auctionID) throws SQLException {
        List<BidBean> bids = new ArrayList<>();
        String selectQuery = "SELECT * FROM bid JOIN user ON bid.userID = user.userID where bid.auctionID = ?";
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(selectQuery);
            pstatement.setInt(1, auctionID);
            ResultSet resultBid = pstatement.executeQuery();
            while (resultBid.next()) {
                BidBean bidBean = new BidBean(
                        resultBid.getInt("bidID"),
                        resultBid.getInt("userID"),
                        resultBid.getInt("auctionID"),
                        resultBid.getFloat("value"),
                        new Date(resultBid.getTimestamp("date").getTime()),
                        resultBid.getString("address"));
                bidBean.setUserEmail(resultBid.getString("email"));
                bids.add(0,bidBean);
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                pstatement.close();
                connection.setAutoCommit(true);
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return bids;
    }

    public Float getMaxBidFromAuctionID(Integer auctionID) throws SQLException {
        String queryMaxBid = "SELECT MAX(value) AS value FROM bid WHERE auctionID = ?";
        PreparedStatement pstatement = null;
        float ret = 0.0F;
        pstatement = connection.prepareStatement(queryMaxBid);
        pstatement.setInt(1, auctionID);
        ResultSet resultSet = pstatement.executeQuery();
        if(resultSet.next()) ret = resultSet.getFloat("value");

        return ret;
    }

    public BidBean getBid(int bidID) throws SQLException {
        String queryBid = "SELECT * FROM bid JOIN user ON bid.userID = user.userID WHERE bidID = ?";
        PreparedStatement pstatement = null;
        pstatement = connection.prepareStatement(queryBid);
        pstatement.setInt(1, bidID);
        ResultSet resultBid = pstatement.executeQuery();
        if(resultBid.next()) {
            BidBean bidBean = new BidBean(
                    resultBid.getInt("bidID"),
                    resultBid.getInt("userID"),
                    resultBid.getInt("auctionID"),
                    resultBid.getFloat("value"),
                    new Date(resultBid.getTimestamp("date").getTime()),
                    resultBid.getString("address"));
            bidBean.setUserEmail(resultBid.getString("email"));
            return bidBean;
        }
        return null;
    }

    public int insertBid(Integer auctionID, Integer userID, Float value, String address) throws BidException{
        String queryInsert = "INSERT INTO bid (userID, auctionID, value, date, address) " +
                "VALUES (?,?,?,?,?)";
        PreparedStatement pstatement = null;
        int ret_value=-1;
        try {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
            String currentDateTime = format.format(date);

            pstatement = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS);
            pstatement.setInt(1, userID);
            pstatement.setInt(2, auctionID);
            pstatement.setFloat(3, value);
            pstatement.setString(4, currentDateTime);
            pstatement.setString(5, address);
            if(pstatement.executeUpdate()==0){
                throw new SQLException("Cannot insert new bid.");
            }
            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ret_value =(int)generatedKeys.getLong(1);
                }
                else {
                    throw new BidException("Cannot insert new bid.");
                }
            }
        } catch (SQLException e) {
        } finally {
            try {
                assert pstatement != null;
                pstatement.close();
            } catch (Exception e1) {
            }
        }
        return ret_value;
    }
}
