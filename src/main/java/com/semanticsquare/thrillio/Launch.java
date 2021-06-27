package com.semanticsquare.thrillio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.semanticsquare.thrillio.backgroundjobs.WebpageDownloaderTask;
import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;
import com.semanticsquare.thrillio.util.StringUtil;

public class Launch {
	
	private static List<User> users;
	private static List<List<Bookmark>> bookmarks;

	private static void loadData() {
		System.out.println("1. Loading data...");
		DataStore.loadData();
		
		users = UserManager.getInstance().getUsers();
		bookmarks = BookmarkManager.getInstance().getBookmarks();
		
//		System.out.println("Printing users...");
//		printUserData();
//		System.out.println("Printing bookmarks");
//		printBookmarkData();
	}
	
	private static void printUserData() {
		for (User user : users) {
			System.out.println(user);
		}
	}
	
	private static void printBookmarkData() {
		for (List<Bookmark> bookmarkList : bookmarks) {
			for (Bookmark bookmark : bookmarkList) {
				System.out.println(bookmark);
			}
		}
	}
	
	private static void start() {
		// System.out.println("\n 2. Bookmarking...");
		for (User user : users) {
			View.browse(user,  bookmarks);
		}
	}
	
	private static void runDownloaderJob() {
		WebpageDownloaderTask task = new WebpageDownloaderTask(true);
		(new Thread(task)).start();
	}
	
	public static void main(String[] args) {
		//loadData();
		//start();
		//runDownloaderJob();
		System.out.println("encoded : " + StringUtil.encodePassword("test"));
	}
	
}
