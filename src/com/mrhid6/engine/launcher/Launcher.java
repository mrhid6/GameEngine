package com.mrhid6.engine.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

import com.mrhid6io.connection.PhpConnection;
import com.mrhid6io.utils.Utils;
import com.mrhid6io.utils.WinRegistry;

public class Launcher {

	private static final String FS = File.separator;
	public static final String TITLE = "GameEngine";
	
	private String installDir = null;
	private boolean installNeeded = false;
	
	private static GameVersion currentVersion = new GameVersion("0.0.0");
	private static GameVersion serverVersion = new GameVersion("0.0.0");
	
	private LauncherGUI gui;
	
	public Launcher() {
		// Determine if new install/update or exisiting

		checkIfInstallDirExists();
		
		if(installNeeded == true){
			new Installer(installDir);
		}else{
			openLauncherGUI();
		}
	}
	
	private void openLauncherGUI(){
		try {
			gui = new LauncherGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkIfInstallDirExists(){

		if(Utils.isWindows()){
			
			try {
				String regLocation = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + TITLE + ".exe";
				String value = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, regLocation, "Path");
				if(value != null){
					installDir = value;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(installDir == null || installDir.equalsIgnoreCase("")){
				installDir = System.getenv("ProgramFiles") + FS + TITLE;
			}
		}
		
		PhpConnection php = new PhpConnection("http://api-repo.hostxtra.co.uk/Game/Build.json", 5000);
		php.connect();
		String SrvVersionData = php.readData();
		php.disconnect();
		
		JSONObject config = new JSONObject(SrvVersionData);
		
		serverVersion = new GameVersion(config.getString("version"));

		if(installDir != null){
			File folder = new File(installDir);

			if(!folder.exists()){
				// If install dir dosen't exist then mark for install.
				installNeeded = true;
			}else{
				// If install dir exists then check if correct version.
				
				String CGFFile = installDir + FS + "config.json";
				File cfgFile = new File(CGFFile);

				if(cfgFile.exists()){
					// If config file exists load json file.
					try {
						config = loadJSON(CGFFile);
						currentVersion = new GameVersion(config.getString("version"));
						
						
						if(!currentVersion.equals(serverVersion)){
							installNeeded = true;
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					// If config file doesn't exist
					installNeeded = true;
				}

			}
		}
	}
	
	public static GameVersion getCurrentVersion() {
		return currentVersion;
	}
	
	public static GameVersion getServerVersion() {
		return serverVersion;
	}


	public static void main(String[] args) {
		
		new Launcher();
	}

	public JSONObject loadJSON(String JSONFile) throws IOException{

		BufferedReader reader = new BufferedReader(new FileReader(JSONFile));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line=reader.readLine())!=null) {
			sb.append(line);
			sb.append("\n");
		}

		reader.close();

		String config = sb.toString();
		JSONObject obj = new JSONObject(config);
		return obj;
	}

}
