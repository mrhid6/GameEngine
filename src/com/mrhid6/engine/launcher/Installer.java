package com.mrhid6.engine.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.mrhid6.settings.Constants;
import com.mrhid6.settings.GameSettings;
import com.mrhid6io.connection.PhpConnection;
import com.mrhid6io.utils.Utils;

public class Installer {
	private static final String URL = "http://api-repo.hostxtra.co.uk/Game";

	private String installDir;
	private ArrayList<InstallerFile> filesToDownload = new ArrayList<InstallerFile>();

	private float totalSize = 0.0f;

	private InstallerGUI gui;

	private static Installer instance;

	private boolean done = false;

	public Installer(String installDir) {
		this.installDir = installDir;

		gui = new InstallerGUI(this.installDir );

		instance = this;
	}
	
	public void loadInstallFiles(){
		try{
			PhpConnection php = new PhpConnection(URL + "/Files.json", 5000);
			php.connect();
			String SrvFilesData = php.readData();
			php.disconnect();

			JSONObject fileData = new JSONObject(SrvFilesData).getJSONObject("Files");

			for(int i=0;i<fileData.names().length();i++){
				String fileToDownload = fileData.names().getString(i);
				String fileDest = Constants.FS + fileData.getString(fileToDownload).replace("/", Constants.FS);

				filesToDownload.add(new InstallerFile(URL + fileToDownload, fileDest));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		calcSize();
	}

	private void deleteAllFiles(){
		try {
			File f = new File(installDir);
			if(f.exists()){
				for (File c : f.listFiles()){
					delete(c);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()){
				delete(c);
			}
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}


	public static Installer getInstance() {
		return instance;
	}

	private void calcSize(){
		totalSize = 0;
		for(InstallerFile file : filesToDownload){
			totalSize += file.getSize();
		}
		gui.setProgressBarMax((int)totalSize);
	}
	
	public String getInstallDir() {
		return installDir;
	}
	
	public void setInstallDir(String installDir) {
		this.installDir = installDir;
	}

	public void downloadFiles(){
		
		deleteAllFiles();
		
		loadInstallFiles();
		
		Thread th = new Thread(){
			@Override
			public void run() {
				
				for(InstallerFile file : filesToDownload){
					file.download(getInstallDir());
					totalSize += file.getSize();
				}
				
				boolean running = true;
				
				while(running){
					int curVal = 0;
					totalSize = 0;
					boolean downloading = false;

					for(InstallerFile file : filesToDownload){
						curVal += file.getDownloaded();
						
						if(file.getSize()>-1){
							totalSize+=file.getSize();
						}
						
						if(file.getStatus() == InstallerFile.DOWNLOADING){
							downloading = true;
						}
					}
					gui.setProgressBarVal(curVal);
					gui.setProgressBarMax((int)totalSize);

					if(!downloading){
						running = false;
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}


				}

				gui.setDone();
				done = true;
				createConfigFile();
			}
		};

		th.start();
	}

	public boolean isDone() {
		return done;
	}

	private void createConfigFile(){

		String configStr = installDir + Constants.FS + "config.json";
		String progDataconfigstr = GameSettings.PROGRAMDATADIR + Constants.FS + "config.json";
		
		File configFile = Utils.loadOrCreateFile(configStr);
		File progDataconfigFile = Utils.loadOrCreateFile(progDataconfigstr);
		
		
		
		try {
			JSONObject config = new JSONObject();
			config.put("version", Launcher.getServerVersion().displayVersion());

			FileWriter fileWriter = new FileWriter(configFile);  


			fileWriter.write(config.toString());
			fileWriter.flush();  
			fileWriter.close();  

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject config = new JSONObject();
			config.put("installdir", getInstallDir());

			FileWriter fileWriter = new FileWriter(progDataconfigFile);  


			fileWriter.write(config.toString());
			fileWriter.flush();  
			fileWriter.close();  

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
