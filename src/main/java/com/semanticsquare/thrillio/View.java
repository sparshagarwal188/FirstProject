package com.semanticsquare.thrillio;

import java.util.List;

import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.constants.UserType;
import com.semanticsquare.thrillio.controllers.BookmarkController;
import com.semanticsquare.thrillio.entities.Bookmark;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.partner.Shareable;

public class View {
	public static void browse(User user, List<List<Bookmark>> bookmarks) {
		System.out.println("\n" + user.getEmail() + " is browsing items");
		int bookmarkCount = 0;

		for (List<Bookmark> bookmarkList : bookmarks) {
			for (Bookmark bookmark : bookmarkList) {
				boolean isBookmarked = getBookmarkDecision(bookmark);
				if (isBookmarked) {
					bookmarkCount++;
					BookmarkController.getInstance().saveUserBookmark(user, bookmark);
					System.out.println("New item bookmarked -- " + bookmark);
				}

				// mark as kid-friendly
				if (user.getUserType().equals(UserType.EDITOR) || user.getUserType().equals(UserType.CHIEF_EDITOR)) {
					if (bookmark.isKidFriendlyEligible()
							&& bookmark.getKidFriendlyStatus().equals(KidFriendlyStatus.UNKNOWN)) {
						KidFriendlyStatus kidFriendlyStatus = getKidFriendlyStatusDecision();
						if (!kidFriendlyStatus.equals(KidFriendlyStatus.UNKNOWN)) {
							BookmarkController.getInstance().setKidFriendlyStatus(user, kidFriendlyStatus, bookmark);
						}
					}

					if (bookmark.getKidFriendlyStatus().equals(KidFriendlyStatus.APPROVED)
							&& bookmark instanceof Shareable) {
						boolean isShared = getShareDecision();
						if(isShared) {
							BookmarkController.getInstance().share(user, bookmark);
						}
					}
				}
			}
		}
	}

	private static boolean getBookmarkDecision(Bookmark bookmark) {
		return Math.random() < 0.5 ? true : false;
	}

	private static KidFriendlyStatus getKidFriendlyStatusDecision() {
		double random = Math.random();
		return random < 0.4 ? KidFriendlyStatus.APPROVED
				: (random < 0.8 ? KidFriendlyStatus.REJECTED : KidFriendlyStatus.UNKNOWN);
	}
	
	private static boolean getShareDecision() {
		return Math.random() < 0.5 ? true : false;
	}
}

/*
 * public static void bookmark(User user, Bookmark[][] bookmarks) {
 * System.out.println("\n" + user.getEmail() + " is bookmarking"); for (int i =
 * 0; i < DataStore.USER_BOOKMARK_LIMIT; i++) { int typeOffset =
 * (int)(Math.random() * DataStore.BOOKMARK_TYPES_COUNT); int bookmarkOffset =
 * (int)(Math.random() * DataStore.BOOKMARK_COUNT_PER_TYPE);
 * 
 * Bookmark bookmark = bookmarks[typeOffset][bookmarkOffset];
 * 
 * System.out.println(bookmark); } } }
 */
