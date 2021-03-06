package com.mrhid6.irontide.settings;

import java.io.File;

public final class Constants {
	
	public static final int FPS_CAP = 120;
	public static final String TITLE = "Irontide";
	
	public static final String FS = File.separator;
	
	public static final float SKYBOX_ROTATE_SPEED = 0.05f;
	
	// Game Installer Settings
	public static final int INSTALLER_WIDTH = 500;
	public static final int INSTALLER_HEIGHT = 250;
	public static final String WIN_DEFAULT_INSTALL_DIR=System.getenv("ProgramFiles")+FS+Constants.TITLE;
	

}
