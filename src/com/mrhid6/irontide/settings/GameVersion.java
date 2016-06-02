package com.mrhid6.irontide.settings;

public class GameVersion {


	private int major, minor, patch;
	private boolean failed = false;

	public GameVersion(String version) {

		String[] data = version.split("\\.");

		if(data.length == 3){
			major = Integer.parseInt(data[0]);
			minor = Integer.parseInt(data[1]);
			patch = Integer.parseInt(data[2]);
		}else{
			failed = true;
		}
	}

	public boolean isFailed() {
		return failed;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}
	
	public String displayVersion(){
		return getMajor() + "." + getMinor() + "." + getPatch();
	}

	@Override
	public boolean equals(Object obj){

		if(obj instanceof GameVersion){
			GameVersion gv2 = (GameVersion)obj;
			return (this.getMajor() == gv2.getMajor() &&
					this.getMinor() == gv2.getMinor() &&
					this.getPatch() == gv2.getPatch());
		}

		return true;
	}

	@Override
	public String toString() {
		return "GameVersion [major=" + major + ", minor=" + minor + ", patch=" + patch + ", failed=" + failed + "]";
	}


}
