package com.semanticsquare.thrillio.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.semanticsquare.thrillio.DataStore;
import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.Movie;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.entities.WebLink;
import com.semanticsquare.thrillio.managers.BookmarkManager;

public class BookmarkDao {
	public List<List<Bookmark>> getBookmarks() {
		return DataStore.getBookmarks();
	}
	
	public void saveUserBookmark(UserBookmark userBookmark) {
		//DataStore.add(userBookmark);
		
		try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			if (userBookmark.getBookmark() instanceof WebLink) 
				saveUserWebLink(userBookmark, stmt);
			else if (userBookmark.getBookmark() instanceof Movie)
				saveUserMovie(userBookmark, stmt);
			else
				saveUserBook(userBookmark, stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void saveUserWebLink(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "insert into User_WebLink(user_id, weblink_id) values(" 
				+ userBookmark.getUser().getId() + ", " + userBookmark.getBookmark().getId() + ")";
		stmt.executeUpdate(query);
		
	}

	private void saveUserMovie(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "insert into User_Movie(user_id, movie_id) values(" 
				+ userBookmark.getUser().getId() + ", " + userBookmark.getBookmark().getId() + ")";
		stmt.executeUpdate(query);
		
	}

	private void saveUserBook(UserBookmark userBookmark, Statement stmt) throws SQLException {
		String query = "insert into User_Book(user_id, book_id) values(" 
				+ userBookmark.getUser().getId() + ", " + userBookmark.getBookmark().getId() + ")";
		stmt.executeUpdate(query);
		
	}

	public List<WebLink> getAllWeblinks() {
		List<WebLink> result = new ArrayList<>();
		List<List<Bookmark>> bookmarks = DataStore.getBookmarks();
		List<Bookmark> weblinks = bookmarks.get(0);
		for (Bookmark weblink : weblinks) {
			result.add((WebLink)weblink);
		}
		return result;
	}
	
	public List<WebLink> getWeblinks(WebLink.DownloadStatus downloadStatus) {
		List<WebLink> result = new ArrayList<>();
		List<WebLink> allWeblinks = getAllWeblinks();
		for (WebLink weblink : allWeblinks) {
			if (weblink.getDownloadStatus().equals(downloadStatus)) result.add(weblink);
		}
		return result;
	}

	public void updateKidFriendlyStatus(Bookmark bookmark) {
		int userId = bookmark.getKidFriendlyMarkedBy().getId();
		int kidFriendlyStatus = bookmark.getKidFriendlyStatus().ordinal();
		String tableToUpdate = "Book";
		if (bookmark instanceof Movie) {
			tableToUpdate = "Movie";
		} else if (bookmark instanceof WebLink) {
			tableToUpdate = "WebLink";
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			String query = "update  " + tableToUpdate + " set kid_friendly_status = " + kidFriendlyStatus + ", kid_friendly_marked_by = " + userId + " where id = " + bookmark.getId();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void shareByInfo(Bookmark bookmark) {
		int userId = bookmark.getSharedBy().getId();
		String tableToUpdate = "Book";
		if (bookmark instanceof WebLink) {
			tableToUpdate = "WebLink";
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			String query = "update  " + tableToUpdate + " set shared_by = " + userId + " where id = " + bookmark.getId();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Collection<Bookmark> getBooks(boolean isBookmarked, int userId) {
		Collection<Bookmark> result = new ArrayList<>();
		
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			
			String query = "";
			if (!isBookmarked) {
				query = "Select b.id, title, image_url, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, " +
						"amazon_rating, kid_friendly_status from Book b, Author a, Book_Author ba, Publisher p where b.id = ba.book_id and ba.author_id = a.id and p.id = b.publisher_id and " + 
						"b.id NOT IN (select ub.book_id from User u, User_Book ub where u.id = " + userId +
						" and u.id = ub.user_id) group by b.id";				
			} else {
				query = "Select b.id, title, image_url, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, " +
						"amazon_rating, kid_friendly_status from Book b, Author a, Book_Author ba, Publisher p where b.id = ba.book_id and ba.author_id = a.id and p.id = b.publisher_id and " + 
						"b.id IN (select ub.book_id from User u, User_Book ub where u.id = " + userId + " and u.id = ub.user_id) group by b.id";
			}
			
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String imageUrl = rs.getString("image_url");
				int publicationYear = rs.getInt("publication_year");
				String publisher = rs.getString("name");		
				String[] authors = rs.getString("authors").split(",");			
				int genre_id = rs.getInt("book_genre_id");
				BookGenre genre = BookGenre.values()[genre_id];
				double amazonRating = rs.getDouble("amazon_rating");
				int kidFriendly = rs.getInt("kid_friendly_status");
				KidFriendlyStatus kidFriendlyStatus = KidFriendlyStatus.values()[kidFriendly];
				
				//System.out.println("id: " + id + ", title: " + title + ", publication year: " + publicationYear + ", authors: " + String.join(", ", authors) + ", genre: " + genre + ", amazonRating: " + amazonRating);
				
				Bookmark bookmark = BookmarkManager.getInstance().createBook(id, title, publicationYear, publisher, imageUrl, authors, genre, amazonRating, kidFriendlyStatus);
				result.add(bookmark);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Bookmark getBook(int id) {
		Bookmark bookmark = null;
		
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			
			String query = "Select b.id, title, image_url, publication_year, p.name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, " +
					"amazon_rating, kid_friendly_status from Book b, Author a, Book_Author ba, Publisher p where b.id = ba.book_id and ba.author_id = a.id " +
					"and p.id = b.publisher_id and b.id = " + id;
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next()) {
				int bookId = rs.getInt("id");
				String title = rs.getString("title");
				String imageUrl = rs.getString("image_url");
				int publicationYear = rs.getInt("publication_year");
				String publisher = rs.getString("name");		
				String[] authors = rs.getString("authors").split(",");			
				int genre_id = rs.getInt("book_genre_id");
				BookGenre genre = BookGenre.values()[genre_id];
				double amazonRating = rs.getDouble("amazon_rating");
				int kidFriendly = rs.getInt("kid_friendly_status");
				KidFriendlyStatus kidFriendlyStatus = KidFriendlyStatus.values()[kidFriendly];
				
				bookmark = BookmarkManager.getInstance().createBook(bookId, title, publicationYear, publisher, imageUrl, authors, genre, amazonRating, kidFriendlyStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bookmark;
	}
}
