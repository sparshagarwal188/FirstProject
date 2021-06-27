package com.semanticsquare.thrillio.constants;

public enum KidFriendlyStatus {
	UNKNOWN("unknown"),
	APPROVED("approved"),
	REJECTED("rejected");
	
	private KidFriendlyStatus (String name) {
		this.name = name;
	}
	String name;
	
	public String getName() {
		return name;
	}
}
