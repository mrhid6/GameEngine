package com.mrhid6.render.armature;

public enum BoneName {

	MAIN_HAND	("Hand_R"),
	OFF_HAND	("Hand_L"),
	UPPER_BODY	("Chest"),
	HEAD		("Head");
	
	private String name;
	
	private BoneName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static BoneName get(String name) {
		name = name.toLowerCase();
		if (name.equals("main_hand")) return MAIN_HAND;
		else if (name.equals("off_hand")) return OFF_HAND;
		else if (name.equals("upper_body")) return UPPER_BODY;
		else if (name.equals("head")) return HEAD;
		else return null;
	}
}
