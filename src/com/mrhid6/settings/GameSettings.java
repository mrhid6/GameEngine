package com.mrhid6.settings;

import com.mrhid6io.utils.Utils;

public class GameSettings {
	
	public static String PROGRAMDATADIR = "";
	public static String INSTALLDIR = "";
	
	public static void loadProgramDataDir(){
		if(Utils.isWindows()){
			GameSettings.PROGRAMDATADIR = System.getenv("ProgramData")+Constants.FS+Constants.TITLE;
		}else if(Utils.isLinux()){
			GameSettings.PROGRAMDATADIR = "/etc/"+Constants.TITLE;
		}
	}
	
	
	
	static{
		GameSettings.loadProgramDataDir();
	}
}
