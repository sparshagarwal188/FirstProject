package com.semanticsquare.thrillio.constants;

public enum MovieGenre {
	
	CLASSICS("classics"),
	DRAMA("drama"),
	SCIFI_AND_FANTASY("scifi and fantasy"),
	COMEDY("comedy"),
	CHILDREN_AND_FAMILY("children and family"),
	ACTION_AND_ADVENTURE("action and adventure"),
	THRILLERS("thrillers"),
	MUSIC_AND_MUSICALS("music and musicals"),
	TELEVISION("television"),
	HORROR("horror"),
	SPECIAL_INTEREST("special interest"),
	INDEPENDENT("independent"),
	SPORTS_AND_FITNESS("sports and fitness"),
	ANIME_AND_ANIMATION("anime and animation"),
	GAY_AND_LESBIAN("gay and lesbian"),
	CLASSIC_MOVIE_MUSICALS("classic movie musicals"),
	FAITH_AND_SPIRITUALITY("Faith & Spirituality"),
    FOREIGN_DRAMAS("Foreign Dramas"),
    FOREGIN_ACTION_AND_ADVENTURE("Foreign Action & Adventure"),
    FOREGIN_THRILLERS("Foreign Thrillers"),
    TV_SHOWS("TV Shows"),
    DRAMAS("Dramas"),
    ROMANTIC_MOVIES("Romantic Movies"),
    COMEDIES("Comedies"),
    DOCUMENTARIES("Documentaries"),
    FOREIGN_MOVIES("Foreign Movies");
	
	private MovieGenre(String name) {
		this.name = name;
	}
	private String name;
	public String getName() {
		return name;
	}
 }
