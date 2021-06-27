package com.semanticsquare.thrillio.controllers;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;

@WebServlet(urlPatterns = {"/bookmark", "/bookmark/save", "/bookmark/mybooks"})
public class BookmarkController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public BookmarkController() {}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		RequestDispatcher dispatcher = null;
		
		if (request.getSession().getAttribute("userId") != null) {
			int userId = (int) request.getSession().getAttribute("userId");
			if (request.getServletPath().contains("save")) {
				// saving
				
				dispatcher = request.getRequestDispatcher("/mybooks.jsp");
				String bid = request.getParameter("bid");
				
				User user = UserManager.getInstance().getUser(userId);
				Bookmark bookmark = BookmarkManager.getInstance().getBook(Integer.parseInt(bid));
				BookmarkManager.getInstance().saveUserBookmark(user, bookmark);
				
				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(true, userId);
				request.setAttribute("books", list);
				
			} else if (request.getServletPath().contains("mybooks")) {
				// displaying book-marked books
				
				dispatcher = request.getRequestDispatcher("/mybooks.jsp");
				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(true, userId);
				request.setAttribute("books", list);
				
			} else {
				// displaying non-bookmarked books
				
				dispatcher = request.getRequestDispatcher("/browse.jsp");
				Collection<Bookmark> list = BookmarkManager.getInstance().getBooks(false, userId);
				request.setAttribute("books", list);
			}
		} else {
			dispatcher = request.getRequestDispatcher("/login.jsp");
		}
			
		try {
			dispatcher.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void saveUserBookmark(User user, Bookmark bookmark) {
		BookmarkManager.getInstance().saveUserBookmark(user, bookmark);
	}

	public void setKidFriendlyStatus(User user, KidFriendlyStatus kidFriendlyStatus, Bookmark bookmark) {
		BookmarkManager.getInstance().setKidFriendlyStatus(user, kidFriendlyStatus, bookmark);
	}
	
	public void share(User user, Bookmark bookmark) {
		BookmarkManager.getInstance().share(user, bookmark);
	}
}
