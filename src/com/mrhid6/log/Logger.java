package com.mrhid6.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.mrhid6io.utils.Utils;

public class Logger {
	
	private static File logFolder;
	private static File logFile;
	
	public Logger() {
		logFolder = Utils.loadOrCreateDirectory("logs/");
		logFile = Utils.loadOrCreateFile("logs/client.log");
	}
	
	public static void info(String log_message){
		log(LogType.INFO, log_message);
	}
	
	public static void log(LogType type, String log_message){
		String msg_prefix = type.getTypeName()+" - ";
		
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
