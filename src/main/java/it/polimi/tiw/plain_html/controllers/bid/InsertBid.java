package it.polimi.tiw.plain_html.controllers.bid;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.BidBean;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.dao.BidDao;
import it.polimi.tiw.plain_html.exceptions.AuctionException;
import it.polimi.tiw.plain_html.exceptions.BidException;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MultipartConfig
@WebServlet("/bid/add")
public class InsertBid extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public InsertBid() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BidDao bidDao = new BidDao(connection);
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        HttpSession session = request.getSession();
        try {
            int auctionID = Integer.parseInt(request.getParameter("auction-id-for-bid"));
            int userID = ((UserBean) session.getAttribute("user")).getUserID();
            float newBidValue = Float.parseFloat(request.getParameter("value"));
            float oldMaxValue = bidDao.getMaxBidFromAuctionID(auctionID);
            AuctionBean auction = auctionDAO.getAuction(auctionID);
            if((oldMaxValue == 0.0F && newBidValue<auction.getInitialPrice()) ||
                    newBidValue < oldMaxValue + auction.getMinRaise() ||
                    auction.getCreator()==userID) {
                throw new BidException("Offerta che non rispetta i vincoli.", auctionID);
            }

            int bidID = bidDao.insertBid(auctionID, userID, newBidValue, request.getParameter("address"));
            BidBean bid = bidDao.getBid(bidID);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = new Gson().toJson(bid);
            response.getWriter().println(json);
        } catch (BidException throwables) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("ERROR: "+throwables.getMessage());
        } catch (SQLException sqlException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("ERROR: "+sqlException.getMessage());
        }
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
