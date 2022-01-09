package it.polimi.tiw.plain_html.controllers.auction;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.dao.ProductDao;
import it.polimi.tiw.plain_html.exceptions.AuctionException;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@MultipartConfig
@WebServlet("/auction/add")
public class InsertAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public InsertAuction() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        ProductDao productDAO = new ProductDao(connection);
        HttpSession session = request.getSession();
        try {
            connection.setAutoCommit(false);
            Part filePart = request.getPart("product-image");
            InputStream fileContent = filePart.getInputStream();
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(request.getParameter("expiration").replace("T"," "));
            if(dateTime.getTime()<new Date().getTime()) throw new AuctionException("Past Dates are not allowed");


            int productID = productDAO
                    .insertProduct(request.getParameter("product-name"),
                            request.getParameter("product-description"),
                            fileContent);
            int auctionID = auctionDAO
                    .insertAuction((UserBean) session.getAttribute("user"),
                            productID,
                            Float.parseFloat(request.getParameter("initial-price")),
                            Float.parseFloat(request.getParameter("min-raise")),
                            dateTime);

            AuctionBean auction = auctionDAO.getAuction(auctionID);
            connection.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = new Gson().toJson(auction);
            response.getWriter().println(json);
        } catch (Exception exception){
            try {
                connection.rollback();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("ERROR: "+exception.getMessage());
            } catch (SQLException sqlException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("ERROR: "+sqlException.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                response.sendError(500, sqlException.getMessage());
            }
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
