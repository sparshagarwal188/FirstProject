package com.semanticsquare.thrillio.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.semanticsquare.thrillio.managers.UserManager;

/**
 * Servlet implementation class AuthenticationController
 */
@WebServlet(urlPatterns = {"/auth", "/auth/logout"})
public class AuthenticationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public AuthenticationController() {}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!request.getServletPath().contains("logout")) {
			
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			
			int userId = UserManager.getInstance().authenticateUser(email, password);
			
			if (userId != -1) {
				HttpSession session = request.getSession();
				session.setAttribute("userId", userId);
				
				request.getRequestDispatcher("bookmark/mybooks").forward(request,  response);
			} else {
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		} else {
			request.getSession().invalidate();
			
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
