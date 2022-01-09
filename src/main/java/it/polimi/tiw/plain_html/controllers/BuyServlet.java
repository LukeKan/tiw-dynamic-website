package it.polimi.tiw.plain_html.controllers;


import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.dao.BidDao;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;
import it.polimi.tiw.plain_html.utils.DateManipulator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/buy")
public class BuyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public BuyServlet() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDao bidDao = new BidDao(connection);
        long loginTime = (long) request.getSession().getAttribute("loginTime");
        String keyword = (String) request.getParameter("keyword");
        UserBean user = (UserBean) request.getSession().getAttribute("user");
        try {
            ArrayList<AuctionBean> auctionSearchRes = null;
            if (keyword != null) {
                auctionSearchRes = auctionDAO.retrieveOpenAuctionsOnKeyword(keyword, loginTime, user.getUserID());
                auctionSearchRes.forEach( auction -> {
                    try {
                        auction.setMaxBid(bidDao.getMaxBidFromAuctionID(auction.getAuctionID()));
                    } catch (Exception exception) {
                        auction.setMaxBid(-1.0f);
                    }
                });
            }
            ArrayList<AuctionBean> winnerAuctions = auctionDAO
                    .retrieveWinnerAuctions(
                            user.getUserID());
            winnerAuctions.forEach(auction -> {
                try {
                    auction.setMaxBid(bidDao.getMaxBidFromAuctionID(auction.getAuctionID()));
                } catch (Exception exception) {
                    auction.setMaxBid(-1.0f);
                }
            });
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json= new StringBuilder("{" +
                    "\"auctionSearchRes\":" +
                    "[");
            if(auctionSearchRes != null) {
                for (AuctionBean auction : auctionSearchRes) {
                    json.append(new Gson().toJson(auction));
                    json.append(",");
                    //json.append(DateManipulator.getInstance().dateDifferenceToString())
                    //TODO aggiungi le date
                }
                if (!auctionSearchRes.isEmpty())json.deleteCharAt( json.length() - 1 );
            }
            json.append("], \"winnerAuctions\":[");
            for (AuctionBean auction : winnerAuctions){
                json.append(new Gson().toJson(auction));
                json.append(",");
                //json.append(DateManipulator.getInstance().dateDifferenceToString())
                //TODO aggiungi le date
            }
            if (!winnerAuctions.isEmpty())json.deleteCharAt( json.length() - 1 );
            json.append("]}");

            response.getWriter().println(json);
        } catch (SQLException throwables) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("ERROR: "+throwables.getMessage());
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
