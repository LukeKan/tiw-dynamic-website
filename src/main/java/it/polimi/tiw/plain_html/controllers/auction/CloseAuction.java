package it.polimi.tiw.plain_html.controllers.auction;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.exceptions.AuctionException;
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

@MultipartConfig
@WebServlet("/auction/close")
public class CloseAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public CloseAuction() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        HttpSession session = request.getSession();
        try {
            int auctionID = Integer.parseInt(request.getParameter("auctionID"));
            int userID = ((UserBean)session.getAttribute("user")).getUserID();
            long loginTime = (Long) session.getAttribute("loginTime");
            auctionDAO.closeAuction(auctionID,userID, loginTime);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (AuctionException exception){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("ERROR for auction"+exception.getAuctionID()+": "+exception.getMessage());
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
