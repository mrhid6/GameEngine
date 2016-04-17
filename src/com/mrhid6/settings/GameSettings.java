package com.mrhid6.settings;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import com.mrhid6.utils.Loader;
import com.mrhid6io.utils.Utils;

public class GameSettings {
	
	public static String PROGRAMDATADIR = "";
	public static String INSTALLDIR = "";
	
	public static boolean USEGROUNDCLUTTER = false;
	
	public static GameVersion CURRENTVERSION = new GameVersion("0.0.0");
	
	public static void loadProgramDataDir(){
		if(Utils.isWindows()){
			GameSettings.PROGRAMDATADIR = System.getenv("ProgramData")+Constants.FS+Constants.TITLE;
		}else if(Utils.isLinux()){
			GameSettings.PROGRAMDATADIR = "/etc/"+Constants.TITLE;
		}
	}
	
	public static void loadProgramDataConfInstDir(){
		String programdataConf = GameSettings.PROGRAMDATADIR + Constants.FS + "config.json";
		try {
			JSONObject config = Loader.getInstance().loadJSONFR(programdataConf);
			GameSettings.INSTALLDIR = config.getString("installdir");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadCurrentVersionNumber(){
		String CGFFile = GameSettings.INSTALLDIR + Constants.FS + "config.json";
		File cfgFile = new File(CGFFile);

		if(cfgFile.exists()){
			// If config file exists load json file.
			try {
				JSONObject config = Loader.getInstance().loadJSON(CGFFile);
				GameSettings.CURRENTVERSION = new GameVersion(config.getString("version"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static{
		GameSettings.loadProgramDataDir();
		GameSettings.loadProgramDataConfInstDir();
		GameSettings.loadCurrentVersionNumber();
	}
}
