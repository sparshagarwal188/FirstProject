package com.semanticsquare.thrillio.managers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import com.semanticsquare.thrillio.constants.BookGenre;
import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.constants.MovieGenre;
import com.semanticsquare.thrillio.dao.BookmarkDao;
import com.semanticsquare.thrillio.entities.Book;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.Movie;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.entities.UserBookmark;
import com.semanticsquare.thrillio.entities.WebLink;
import com.semanticsquare.thrillio.partner.Shareable;
import com.semanticsquare.thrillio.util.HttpConnect;
import com.semanticsquare.thrillio.util.IOUtil;

public class BookmarkManager {
	private static BookmarkManager instance = new BookmarkManager();
	private static BookmarkDao dao = new BookmarkDao();

	private BookmarkManager() {
	}

	public static BookmarkManager getInstance() {
		return instance;
	}

	public Movie createMovie(int id, String title, String profileUrl, int releaseYear, String[] cast,
			String[] directors, MovieGenre genre, double imdbRating, KidFriendlyStatus kidFriendlyStatus) {
		Movie movie = new Movie();
		movie.setId(id);
		movie.setTitle(title);
		movie.setProfileUrl(profileUrl);
		movie.setReleaseYear(releaseYear);
		movie.setCast(cast);
		movie.setDirectors(directors);
		movie.setGenre(genre);
		movie.setImdbRating(imdbRating);
		movie.setKidFriendlyStatus(kidFriendlyStatus);

		return movie;
	}

	public Book createBook(int id, String title, int publicationYear, String publisher, String imageUrl, String[] authors, BookGenre genre,
			double amazonRating, KidFriendlyStatus kidFriendlyStatus) {
		Book book = new Book();
		book.setId(id);
		book.setTitle(title);
		book.setPublicationYear(publicationYear);
		book.setPublisher(publisher);
		book.setImageUrl(imageUrl);
		book.setAuthors(authors);
		book.setGenre(genre);
		book.setAmazonRating(amazonRating);
		book.setKidFriendlyStatus(kidFriendlyStatus);

		return book;
	}

	public WebLink createWebLink(int id, String title, String url, String host, KidFriendlyStatus kidFriendlyStatus) {
		WebLink weblink = new WebLink();
		weblink.setId(id);
		weblink.setTitle(title);
		weblink.setUrl(url);
		weblink.setHost(host);
		weblink.setKidFriendlyStatus(kidFriendlyStatus);

		return weblink;
	}

	public List<List<Bookmark>> getBookmarks() {
		return dao.getBookmarks();
	}

	public void saveUserBookmark(User user, Bookmark bookmark) {
		UserBookmark userBookmark = new UserBookmark();
		userBookmark.setUser(user);
		userBookmark.setBookmark(bookmark);
		
		if(bookmark instanceof WebLink) {
			try {
				String url = ((WebLink)bookmark).getUrl();
				if(!(url.endsWith(".pdf"))) {
					String webpage = HttpConnect.download(url);
					if(webpage != null) {
						IOUtil.write(webpage, bookmark.getId());
					}
				}
				
			} catch(MalformedURLException e) {
				e.printStackTrace();
			} catch(URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		dao.saveUserBookmark(userBookmark);
	}

	public void setKidFriendlyStatus(User user, KidFriendlyStatus kidFriendlyStatus, Bookmark bookmark) {
		bookmark.setKidFriendlyStatus(kidFriendlyStatus);
		bookmark.setKidFriendlyMarkedBy(user);
		dao.updateKidFriendlyStatus(bookmark);
		System.out.println("Status - " + kidFriendlyStatus + " Marked by - " + user.getEmail() + " , " + bookmark);

	}
	
	public void share(User user, Bookmark bookmark) {
		bookmark.setSharedBy(user);
		System.out.println("Data to be shared : ");
		System.out.println(((Shareable)bookmark).getItemData());
		/*if(bookmark instanceof Book) {
			System.out.println(((Book)bookmark).getItemData());
		} else if (bookmark instanceof WebLink) {
			System.out.println(((WebLink)bookmark).getItemData());
		}*/
		dao.shareByInfo(bookmark);
	}

	public Collection<Bookmark> getBooks(boolean isBookmarked, int id) {
		// TODO Auto-generated method stub
		return dao.getBooks(isBookmarked, id);
	}
	
	public Bookmark getBook(int id) {
		return dao.getBook(id);
	}
}
