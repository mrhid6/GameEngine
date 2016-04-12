package com.mrhid6.asset;

public enum AssetType {
	
	UnknownObject(-1),
	StaticWorldObject(1),
	Player(2);
	
	private int type;
	
	private AssetType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static AssetType getType(int type){
		for(AssetType t : AssetType.values()){
			if(t.getType() == type){
				return t;
			}
		}
		
		return AssetType.UnknownObject;
	}
	
}
