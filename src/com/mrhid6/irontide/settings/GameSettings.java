package com.mrhid6.irontide.settings;

import java.io.File;

import com.mrhid6.irontide.config.ConfigHelper;
import com.mrhid6io.config.Config;
import com.mrhid6io.config.ConfigGroup;

public class GameSettings {
	
	// Game General Settings
	
	public static GameVersion CURRENTVERSION = new GameVersion("0.0.0");
	public static String INSTALLDIR = "";
	public static boolean INSTALLED = false;
	public static boolean DEBUG_MODE = true;
	
	// Game Display Settings
	public static boolean FULLSCREEN;
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	
	// Game Detail Settings
	public static boolean USEGROUNDCLUTTER = false;
	public static boolean USEWATERREFLECTIONS = true;
	
	
	
	private static ConfigHelper configHelper;
	private static GameSettings instance;
	
	public GameSettings() {
		loadConfig();
		checkInstalled();
		
		instance = this;
	}


	private void loadConfig() {
		
		configHelper = new ConfigHelper();
		
		Config config = configHelper.getConfig();
		
		ConfigGroup general = config.getGroup("General");
		String version = general.getString("version", "0.0.0");
		
		CURRENTVERSION = new GameVersion(version);
		if(CURRENTVERSION.isFailed()){ CURRENTVERSION=new GameVersion("0.0.0"); }
		
		version = CURRENTVERSION.displayVersion();
		general.set("version", version);
		
		INSTALLDIR = general.getString("installfolder", Constants.WIN_DEFAULT_INSTALL_DIR);
		general.set("installfolder", INSTALLDIR);
		
		ConfigGroup display = config.getGroup("Display");
		
		FULLSCREEN = display.getBoolean("fullscreen", true);
		display.set("fullscreen", FULLSCREEN);
		
		WIDTH = display.getInt("width", 1920);
		display.set("width", WIDTH);
		
		HEIGHT = display.getInt("height", 1080);
		display.set("height", HEIGHT);
		
		config.saveConfig();
	}
	
	private void checkInstalled(){
		String jarfile = INSTALLDIR + Constants.FS + Constants.TITLE + ".jar";
		File tmp = new File(jarfile);
		
		if(tmp.exists()){
			INSTALLED = true;
		}else{
			INSTALLED = false;
		}
	}
	
	public static ConfigHelper getConfigHelper() {
		return configHelper;
	}
	
	public static GameSettings getInstance() {
		return instance;
	}
}
