package com.mrhid6.engine.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

import com.mrhid6.settings.Constants;
import com.mrhid6io.connection.PhpConnection;
import com.mrhid6io.utils.Utils;

public class Launcher {
	
	private String installDir = null;
	private boolean installNeeded = false;
	
	
	private String ProgramDataDir = "";
	
	private static GameVersion currentVersion = new GameVersion("0.0.0");
	private static GameVersion serverVersion = new GameVersion("0.0.0");
	
	private LauncherGUI gui;

	private static Launcher instance;
	
	public Launcher() {
		// Determine if new install/update or exisiting

		if(Utils.isWindows()){
			ProgramDataDir = System.getenv("ProgramData")+Constants.FS+Constants.TITLE;
		}else if(Utils.isLinux()){
			ProgramDataDir = "/etc/"+Constants.TITLE;
		}
		
		checkIfInstallDirExists();
		
		instance = this;
		
		if(installNeeded == true){
			new Installer(installDir);
		}else{
			openLauncherGUI();
		}
	}
	
	public static Launcher getInstance() {
		return instance;
	}
	
	public String getProgramDataDir() {
		return ProgramDataDir;
	}
	
	public void openLauncherGUI(){
		try {
			gui = new LauncherGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createProgramDataDir(){
		File dirFile = new File(ProgramDataDir);
		
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
	}
	
	private boolean checkProgramDataConf(){
		String programdataConf = ProgramDataDir + Constants.FS + "config.json";
		File config = new File(programdataConf);
		
		if(!config.exists()){return false;}
		
		return true;
	}
	
	private String getProgramDataConfInstDir(){
		String programdataConf = ProgramDataDir + Constants.FS + "config.json";
		
		try {
			JSONObject config = loadJSON(programdataConf);
			return config.getString("installdir");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void checkIfInstallDirExists(){
		
		createProgramDataDir();
		
		if(Utils.isWindows()){
			
			if(checkProgramDataConf()){
				String value = getProgramDataConfInstDir();
				if(value != null){
					installDir = value;
				}
			}
			
			if(installDir == null || installDir.equalsIgnoreCase("")){
				installDir = Constants.WIN_DEFAULT_INSTALL_DIR;
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
				
				String CGFFile = installDir + Constants.FS + "config.json";
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
