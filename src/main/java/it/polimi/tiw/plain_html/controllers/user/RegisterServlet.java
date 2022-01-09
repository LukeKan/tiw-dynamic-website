package it.polimi.tiw.plain_html.controllers.user;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.UserBean;
import it.polimi.tiw.plain_html.dao.UserDAO;
import it.polimi.tiw.plain_html.exceptions.InputParamException;
import it.polimi.tiw.plain_html.exceptions.UserException;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/user/register")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public RegisterServlet() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        UserDAO userDAO = new UserDAO(connection);
        try {
            String email = request.getParameter("email").trim();
            String password = request.getParameter("psw").trim();
            String passwordConf = request.getParameter("psw-conf").trim();
            String name = request.getParameter("name").trim();
            String surname = request.getParameter("surname").trim();
            String cf = request.getParameter("cf").trim();
            if(email.isEmpty() || password.isEmpty() || passwordConf.isEmpty() ||
                    name.isEmpty() || surname.isEmpty() || cf.isEmpty()){
                throw new InputParamException("Parametri input mancanti");
            }
            Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = emailPattern.matcher(email);
            if(!matcher.matches()){
                throw new InputParamException("Formato indirizzo e-mail errato, riprovare");
            }
            if(password.length()<5){
                throw new InputParamException("Password troppo corta, deve superare almeno 5 caratteri");
            }
            if(!password.equals(passwordConf)){
                throw new InputParamException("Le password non corrispondono!");
            }

            UserBean user = userDAO.register(email,
                    password,
                    name,
                    surname,
                    cf
                    );
            if(user == null){
                throw new UserException("Registrazione fallita: qualcosa è andato storto");
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
        } catch (InputParamException | UserException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("ERROR: "+ex.getMessage());
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
