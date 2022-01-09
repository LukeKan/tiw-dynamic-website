package it.polimi.tiw.plain_html.dao;

import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.ProductBean;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.exceptions.AuctionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AuctionDAO {
    private final Connection connection;

    public AuctionDAO(Connection connection) {
        this.connection = connection;
    }

    private void checkOwnageAndExpiration(Integer auctionID, Integer userID, Long loginTime) throws AuctionException, SQLException {
        AuctionBean auctionBean = getAuction(auctionID);
        if(loginTime<auctionBean.getExpiration().getTime()) throw new AuctionException("Not yet expired");
        if(!auctionBean.getCreator().equals(userID)) throw new AuctionException("You are not the owner");
    }

    public ArrayList<AuctionBean> retrieveMyAuctions(Integer userID) throws SQLException {
        ArrayList<AuctionBean> auctions = new ArrayList<>();
        String queryAuction = "SELECT * from AUCTION " +
                "JOIN PRODUCT on PRODUCT.productID = AUCTION.productID" +
                " WHERE creatorID = ? ORDER BY  AUCTION.expiration DESC";
        ResultSet resultAuction = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(queryAuction);
            pstatement.setInt(1, userID);
            resultAuction = pstatement.executeQuery();

            while (resultAuction.next()) {
                Blob image = resultAuction.getBlob("image");
                InputStream imgStream = null;
                if (image != null){
                    imgStream = image.getBinaryStream();
                }
                ProductBean productBean = new ProductBean(
                        resultAuction.getInt("productID"),
                        resultAuction.getString("name"),
                        resultAuction.getString("description"),
                        imgStream
                );
                AuctionBean auctionBean = new AuctionBean(
                        resultAuction.getInt("auctionID"),
                        userID,
                        productBean,
                        resultAuction.getFloat("initialPrice"),
                        resultAuction.getFloat("minRaise"),
                        new Date(resultAuction.getTimestamp("expiration").getTime()),
                        resultAuction.getBoolean("closed"));
                auctions.add(0,auctionBean);
            }
        } catch (SQLException | IOException e) {
            throw new SQLException(e);

        } finally {
            try {
                resultAuction.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close resultAuction");
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return auctions;
    }

    public ArrayList<AuctionBean> retrieveOpenAuctionsOnKeyword(String keyword, long loginTime, int userID) throws SQLException {
        ArrayList<AuctionBean> auctions = new ArrayList<>();
        String query = "SELECT * FROM auction JOIN product " +
                "ON auction.productID=product.productID " +
                "WHERE (product.name LIKE ? OR product.description LIKE ?)  " +
                "AND auction.closed = false " +
                "AND auction.expiration >= ? " +
                "AND auction.creatorID != ? " +
                "ORDER BY auction.expiration DESC";
        ResultSet resultAuction = null;
        PreparedStatement pstatement = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(new Date(loginTime));
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, "%"+keyword+"%");
            pstatement.setString(2, "%"+keyword+"%");
            pstatement.setString(3,currentDateTime);
            pstatement.setInt(4,userID);

            resultAuction = pstatement.executeQuery();
            while (resultAuction.next()) {
                Blob image = resultAuction.getBlob("image");
                InputStream imgStream = null;
                if (image != null){
                    imgStream = image.getBinaryStream();
                }
                ProductBean productBean = new ProductBean(
                        resultAuction.getInt("productID"),
                        resultAuction.getString("name"),
                        resultAuction.getString("description"),
                        imgStream
                );
                AuctionBean auctionBean = new AuctionBean(
                        resultAuction.getInt("auctionID"),
                        resultAuction.getInt("creatorID"),
                        productBean,
                        resultAuction.getFloat("initialPrice"),
                        resultAuction.getFloat("minRaise"),
                        new Date(resultAuction.getTimestamp("expiration").getTime()),
                        resultAuction.getBoolean("closed"));


                auctions.add(0,auctionBean);
            }
        } catch (SQLException | IOException e) {
            throw new SQLException(e);

        } finally {
            try {
                resultAuction.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close resultAuction");
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return auctions;
    }

    public ArrayList<AuctionBean> retrieveWinnerAuctions(Integer userID) throws SQLException {
        ArrayList<AuctionBean> auctions = new ArrayList<>();
        String queryAuction = "SELECT * from AUCTION " +
                "JOIN PRODUCT on PRODUCT.productID = AUCTION.productID" +
                " WHERE auction.closed = true AND " +
                "? in ( SELECT userID FROM bid as B1 " +
                "WHERE auction.auctionID = B1.auctionID and B1.value = (" +
                "SELECT max(B2.value) FROM bid AS B2 " +
                "WHERE B2.auctionID = B1.auctionID)) " +
                "ORDER BY  AUCTION.expiration DESC";
        ResultSet resultAuction = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(queryAuction);
            pstatement.setInt(1, userID);
            resultAuction = pstatement.executeQuery();

            while (resultAuction.next()) {
                Blob image = resultAuction.getBlob("image");
                InputStream imgStream = null;
                if (image != null){
                    imgStream = image.getBinaryStream();
                }
                ProductBean productBean = new ProductBean(
                        resultAuction.getInt("productID"),
                        resultAuction.getString("name"),
                        resultAuction.getString("description"),
                        imgStream
                );
                AuctionBean auctionBean = new AuctionBean(
                        resultAuction.getInt("auctionID"),
                        userID,
                        productBean,
                        resultAuction.getFloat("initialPrice"),
                        resultAuction.getFloat("minRaise"),
                        new Date(resultAuction.getTimestamp("expiration").getTime()),
                        resultAuction.getBoolean("closed"));
                auctions.add(0,auctionBean);
            }
        } catch (SQLException | IOException e) {
            throw new SQLException(e);

        } finally {
            try {
                resultAuction.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close resultAuction");
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return auctions;
    }

    public AuctionBean getAuction(Integer auctionID) {
        String queryAuction = "SELECT * from AUCTION " +
                "JOIN PRODUCT on PRODUCT.productID = AUCTION.productID" +
                " WHERE auctionID = ?";
        ResultSet resultAuction = null;
        PreparedStatement pstatement = null;
        AuctionBean auctionBean = null;
        try {
            pstatement = connection.prepareStatement(queryAuction);
            pstatement.setInt(1, auctionID);

            resultAuction = pstatement.executeQuery();
            if (resultAuction.next()) {
                Blob image = resultAuction.getBlob("image");
                InputStream imgStream = null;
                if (image != null){
                    imgStream = image.getBinaryStream();
                }
                ProductBean productBean = new ProductBean(
                        resultAuction.getInt("productID"),
                        resultAuction.getString("name"),
                        resultAuction.getString("description"),
                        imgStream);
                auctionBean = new AuctionBean(
                        resultAuction.getInt("auctionID"),
                        resultAuction.getInt("creatorID"),
                        productBean,
                        resultAuction.getFloat("initialPrice"),
                        resultAuction.getFloat("minRaise"),
                        new Date(resultAuction.getTimestamp("expiration").getTime()),
                        resultAuction.getBoolean("closed"));
            }
        } catch (SQLException | IOException e) {
        } finally {
            try {
                resultAuction.close();
            } catch (Exception e1) {
            }
            try {
                pstatement.close();
            } catch (Exception e1) {
            }
        }
        return auctionBean;
    }

    public int insertAuction(UserBean user, int productID,
                                     Float initialPrice, Float minRaise, Date expiration) throws AuctionException, SQLException {
        String queryInsAuction = "INSERT INTO AUCTION (creatorID, productID, initialPrice, minRaise, expiration, closed)" +
                "VALUES (?,?,?,?,?,false)";
        PreparedStatement pstatement = null;
        int ret_value=-1;
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
            String expirationDateTime = format.format(expiration);
            pstatement = connection.prepareStatement(queryInsAuction,Statement.RETURN_GENERATED_KEYS);
            pstatement.setInt(1, user.getUserID());
            pstatement.setInt(2, productID);
            pstatement.setFloat(3, initialPrice);
            pstatement.setFloat(4, minRaise);
            pstatement.setString(5, expirationDateTime);
            if(pstatement.executeUpdate()==0){
                throw new AuctionException("Cannot insert new auction.");
            }

            try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ret_value = (int)generatedKeys.getLong(1);
                }
                else {
                    throw new AuctionException("Cannot insert new auction.");
                }
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
        return ret_value;
    }

    public void closeAuction(Integer auctionID, Integer userID, Long loginTime) throws AuctionException, SQLException {
        String queryClose = "UPDATE auction SET auction.closed = true where auction.auctionID = ?";
        PreparedStatement pstatement = null;
        try {
            checkOwnageAndExpiration(auctionID,userID,loginTime);
            pstatement = connection.prepareStatement(queryClose);
            pstatement.setInt(1, auctionID);
            if(pstatement.executeUpdate()==0){
                throw new AuctionException("Cannot close the auction.", auctionID);
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (Exception exception) {
            throw exception;
        } finally {
            try {
                pstatement.close();
            } catch (Exception e1) {
                throw new SQLException("Cannot close statement");
            }
        }
    }

}
