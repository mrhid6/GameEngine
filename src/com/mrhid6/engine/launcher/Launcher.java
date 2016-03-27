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
	private static final String TITLE = "GameEngine";

	private boolean installNeeded = false;

	public Launcher() {
		// Determine if new install/update or exisiting

		checkIfInstallDirExists();
	}

	private void checkIfInstallDirExists(){

		String installDir = null;

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
						JSONObject config = loadJSON(CGFFile);
						GameVersion version = new GameVersion(config.getString("version"));
						
						PhpConnection php = new PhpConnection("http://api-repo.hostxtra.co.uk/Game/Build.json");
						php.connect();
						String SrvVersionData = php.readData();
						php.disconnect();
						
						config = new JSONObject(SrvVersionData);
						
						GameVersion SrvVersion = new GameVersion(config.getString("version"));
						
						System.out.println(version.equals(SrvVersion));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					// If config file doesn't exist
					installNeeded = true;
				}

			}
			System.out.println(installDir);
			System.out.println(installNeeded);
		}
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
