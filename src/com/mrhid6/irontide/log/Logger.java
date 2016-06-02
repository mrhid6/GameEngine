package com.mrhid6.irontide.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6io.utils.Utils;

public class Logger {
	
	private static File logFolder;
	private static File logFile;
	
	public Logger() {
		logFolder = Utils.loadOrCreateDirectory(GameSettings.INSTALLDIR+"/logs/");
		logFile = Utils.loadOrCreateFile(GameSettings.INSTALLDIR+"/logs/client.log");
	}
	
	public static void info(String log_message){
		String className = new Exception().getStackTrace()[1].getClassName();
		log(className, LogType.INFO, log_message);
	}
	
	private static void log(String className, LogType type, String log_message){
		String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		String msg_prefix = timeStamp+" - "+type.getTypeName()+" - ["+className+"] - ";
		
		String dataToWrite = msg_prefix + log_message;
		writeToFile(dataToWrite);
	}
	
	public static File getLogFile() {
		return logFile;
	}
	
	public static File getLogFolder() {
		return logFolder;
	}
	
	private static void writeToFile(String data){
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
		    out.println(data);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
}
