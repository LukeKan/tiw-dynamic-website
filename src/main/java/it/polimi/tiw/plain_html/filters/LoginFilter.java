package it.polimi.tiw.plain_html.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter("/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        String loginURI = request.getContextPath() + "/tiw-js/login.html";

        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean resourceRequest =
                request.getRequestURI().contains(".css") ||
                request.getRequestURI().contains(".js") ||
                request.getRequestURI().contains(".jpg");

        if (loggedIn  || resourceRequest) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI);
        }
    }
}

