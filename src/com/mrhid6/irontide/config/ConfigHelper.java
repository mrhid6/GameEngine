package com.mrhid6.irontide.config;

import java.io.File;
import java.io.IOException;

import com.mrhid6.irontide.settings.Constants;
import com.mrhid6io.config.Config;
import com.mrhid6io.utils.Utils;

public class ConfigHelper {
	
	private String dataFolder = null;
	private String configFile = null;
	
	private Config config;
	
	private boolean Error = false;
	
	public ConfigHelper() {
		init();
	}
	
	public void init(){
		initDataFolder();
		initConfigFile();
		
		if(!hasErrored()){
			load();
		}else{
			System.err.println("Config has got an error!");
		}
	}
	
	public void load(){
		config = new Config(configFile);
		config.loadConfig();
		
		config.saveConfig();
	}
	
	private void initDataFolder(){
		if(Utils.isWindows()){
			dataFolder = System.getenv("ProgramData")+Constants.FS+Constants.TITLE;
		}else if(Utils.isLinux()){
			dataFolder = "/etc/"+Constants.TITLE;
		}
		
		
		if(dataFolder != null){
			createDataFolder();
		}else{
			Error = true;
		}
	}
	
	private void initConfigFile(){
		if(dataFolder != null){
			configFile = dataFolder + Constants.FS + "config.itd";
			createConfigFile();
		}
	}
	
	private void createDataFolder(){
		File tmp = new File(dataFolder);
		
		if(!tmp.exists()){
			tmp.mkdirs();
		}
	}
	
	private void createConfigFile(){
		File tmp = new File(configFile);
		
		if(!tmp.exists()){
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new ConfigHelper();
	}
	
	
	
	public Config getConfig() {
		return config;
	}
	
	public String getDataFolder() {
		return dataFolder;
	}
	
	public boolean hasErrored() {
		return Error;
	}
	
}
