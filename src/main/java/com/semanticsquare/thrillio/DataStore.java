package com.semanticsquare.thrillio;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.constants.Gender;
import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.constants.MovieGenre;
import com.semanticsquare.thrillio.constants.UserType;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.managers.BookmarkManager;
import com.semanticsquare.thrillio.managers.UserManager;
import com.semanticsquare.thrillio.util.IOUtil;

public class DataStore {
	private static List<User> users = new ArrayList<>();
	private static List<List<Bookmark>> bookmarks = new ArrayList<>();
	private static List<UserBookmark> userBookmarks = new ArrayList<>();

	public static List<User> getUsers() {
		return users;
	}

	public static List<List<Bookmark>> getBookmarks() {
		return bookmarks;
	}

	public static void loadData() {
		
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			// new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// try with resources ==> conn and stmt will be closed
		// Connection String -> <protocol>:<sub-protocol>:<data-source-details>
		try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			 loadUsers(stmt);
			 loadWebLinks(stmt);
			 loadMovies(stmt);
			 loadBooks(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void loadUsers(Statement stmt) throws SQLException{
		String query = "select * from User";
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			int id = rs.getInt("id");
			String email = rs.getString("email");
			String password = rs.getString("password");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			int genderId = rs.getInt("gender_id");
			Gender gender = Gender.values()[genderId];
			int userTypeId = rs.getInt("user_type_id");
			UserType userType = UserType.values()[userTypeId];
			
			User user = UserManager.getInstance().createUser(id, email, password, firstName, lastName, gender, userType);
			users.add(user);
		}
	}

	private static void loadWebLinks(Statement stmt) throws SQLException{
		String query = "select * from WebLink";
		ResultSet rs = stmt.executeQuery(query);
		
		List<Bookmark> bookmarkList = new ArrayList<>();
		while (rs.next()) {
			int id = rs.getInt("id");
			String title = rs.getString("title");
			String url = rs.getString("url");
			String host = rs.getString("host");
			int kidFriendly = rs.getInt("kid_friendly_status");
			KidFriendlyStatus kidFriendlyStatus = KidFriendlyStatus.values()[kidFriendly];
			
			Bookmark bookmark = BookmarkManager.getInstance().createWebLink(id, title, url, host, kidFriendlyStatus);
			bookmarkList.add(bookmark);
		}
		bookmarks.add(bookmarkList);
	}

	private static void loadMovies(Statement stmt) throws SQLException{
		String query = "Select m.id, title, release_year, GROUP_CONCAT(DISTINCT a.name SEPARATOR ',') AS cast, GROUP_CONCAT(DISTINCT d.name SEPARATOR ',') AS directors, movie_genre_id, imdb_rating, kid_friendly_status"
				+ " from Movie m, Actor a, Movie_Actor ma, Director d, Movie_Director md "
				+ "where m.id = ma.movie_id and ma.actor_id = a.id and "
				      + "m.id = md.movie_id and md.director_id = d.id group by m.id";
		ResultSet rs = stmt.executeQuery(query);
		
		List<Bookmark> bookmarkList = new ArrayList<>();
		while (rs.next()) {
			int id = rs.getInt("id");
			String title = rs.getString("title");
			int releaseYear = rs.getInt("release_year");
			String[] cast = rs.getString("cast").split(",");
			String[] directors = rs.getString("directors").split(",");			
			int genre_id = rs.getInt("movie_genre_id");
			MovieGenre genre = MovieGenre.values()[genre_id];
			double imdbRating = rs.getDouble("imdb_rating");
			int kidFriendly = rs.getInt("kid_friendly_status");
			KidFriendlyStatus kidFriendlyStatus = KidFriendlyStatus.values()[kidFriendly];
			
			Bookmark bookmark = BookmarkManager.getInstance().createMovie(id, title, "", releaseYear, cast, directors, genre, imdbRating, kidFriendlyStatus);
    		bookmarkList.add(bookmark); 
		}
		bookmarks.add(bookmarkList);
	}
	
	private static void loadBooks(Statement stmt) throws SQLException{
		/* bookmarks[2][0] = BookmarkManager.getInstance().createBook(4000,	"Walden",	1854,	"Wilder Publications", new String[] {"Henry David Thoreau"},	BookGenre.PHILOSOPHY,	4.3);
		bookmarks[2][1] = BookmarkManager.getInstance().createBook(4001,	"Self-Reliance and Other Essays",	1993,	"Dover Publications", new String[] {"Ralph Waldo Emerson"},	BookGenre.PHILOSOPHY,	4.5);
		bookmarks[2][2] = BookmarkManager.getInstance().createBook(4002,	"Light From Many Lamps",	1988,	"Touchstone", new String[] {"Lillian Eichler Watson"},	BookGenre.PHILOSOPHY,	5.0);
		bookmarks[2][3] = BookmarkManager.getInstance().createBook(4003,	"Head First Design Patterns",	2004,	"O'Reilly Media", new String[] {"Eric Freeman", "Bert Bates", "Kathy Sierra", "Elisabeth Robson"},	BookGenre.TECHNICAL,	4.5);
		bookmarks[2][4] = BookmarkManager.getInstance().createBook(4004,	"Effective Java Programming Language Guide",	2007,	"Prentice Hall", new String[] {"Joshua Bloch"},	BookGenre.TECHNICAL,	4.9); */
		
		/*List<String> data = new ArrayList<>();
		IOUtil.read(data, "Book");
		for (String row : data) {
			String[] values = row.split("\t");
			String[] authors = values[4].split(",");
			if (bookmarks.size() == 2) bookmarks.add(2, new ArrayList<Bookmark>());
			bookmarks.get(2).add(BookmarkManager.getInstance().createBook(Long.parseLong(values[0]), values[1], Integer.parseInt(values[2]), values[3], authors, BookGenre.valueOf(values[5]), Double.parseDouble(values[6])));
			
		}*/
		
		String query = "Select b.id, title, image_url, publication_year, name, GROUP_CONCAT(a.name SEPARATOR ',') AS authors, book_genre_id, " +
				"amazon_rating, kid_friendly_status from Book b, Author a, Book_Author ba where b.id = ba.book_id and ba.author_id = a.id group by b.id";
		ResultSet rs = stmt.executeQuery(query);
		
		List<Bookmark> bookmarkList = new ArrayList<>();
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
			
			/*
			 Date date = rs.getDate("created_date");
			 Timetamp stamp = rs.getTimestamp("created_date");
			 sysout("localDateTime = " + stamp.toLocalDateTime());
			 */
			
			Bookmark bookmark = BookmarkManager.getInstance().createBook(id, title, publicationYear, publisher, imageUrl, authors, genre, amazonRating, kidFriendlyStatus);
			bookmarkList.add(bookmark);
		}
		bookmarks.add(bookmarkList);
	}
		
		
	
	public static void add(UserBookmark userBookmark) {
		userBookmarks.add(userBookmark);
	}
}
