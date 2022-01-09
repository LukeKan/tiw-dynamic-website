package it.polimi.tiw.plain_html.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.dao.BidDao;
import it.polimi.tiw.plain_html.dao.UserDAO;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;
import it.polimi.tiw.plain_html.utils.DateManipulator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/sell")
public class SellServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public SellServlet() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDao bidDao = new BidDao(connection);
        HttpSession session = request.getSession();
        try {
            UserBean currUser = (UserBean) session.getAttribute("user");
            ArrayList<AuctionBean> myAuctions = auctionDAO.retrieveMyAuctions(currUser.getUserID());
            myAuctions.forEach(auction -> {
                try {
                    auction.setMaxBid(bidDao.getMaxBidFromAuctionID(auction.getAuctionID()));
                } catch (Exception exception) {
                    auction.setMaxBid(-1.0f);
                }
            });
            ArrayList<AuctionBean> myOpenAuctions =  new ArrayList<>();
            ArrayList<AuctionBean> myClosedAuctions = new ArrayList<>();
            myAuctions.forEach((elem) -> {
                if (elem.getClosed()) {
                    myClosedAuctions.add(elem);
                } else {
                    myOpenAuctions.add(elem);
                }
            });

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder("{" +
                    "\"open\":" +
                    "[");
            for (AuctionBean auction : myOpenAuctions){
                json.append(new Gson().toJson(auction));
                json.append(",");
                //json.append(DateManipulator.getInstance().dateDifferenceToString())
                //TODO aggiungi le date
            }
            json.deleteCharAt( json.length() - 1 );
            json.append("], \"closed\":[");
            for (AuctionBean auction : myClosedAuctions){
                json.append(new Gson().toJson(auction));
                json.append(",");
                //json.append(DateManipulator.getInstance().dateDifferenceToString())
                //TODO aggiungi le date
            }
            json.deleteCharAt( json.length() - 1 );
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