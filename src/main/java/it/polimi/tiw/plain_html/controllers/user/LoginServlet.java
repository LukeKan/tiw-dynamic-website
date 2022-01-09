package it.polimi.tiw.plain_html.controllers.user;

import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.UserDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import com.google.gson.Gson;

import it.polimi.tiw.plain_html.exceptions.UserException;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;

@WebServlet("/user/login")
@MultipartConfig
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public LoginServlet() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDAO userDAO = new UserDAO(connection);
        String email = request.getParameter("email");
        String psw = request.getParameter("psw");
        try {
            UserBean user = userDAO.login(email,psw);
            if(user == null){
                throw new UserException("Login fallito, password o email errata.");
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("loginTime", new Date(System.currentTimeMillis()).getTime());
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String json = new Gson().toJson(user);
                response.getWriter().println(json);
            }
        } catch (UserException throwables) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
