package com.mrhid6.irontide.log;

public enum LogType {
	
	INFO(1,"INFO"),
	DEBUG(2,"DEBUG"),
	WARNING(3,"WARNING"),
	ERROR(4,"ERROR");
	
	
	private int logLevel;
	private String typeName;
	
	LogType(int logLevel, String typeName){
		this.logLevel = logLevel;
		this.typeName = typeName;
	}
	
	public int getLogLevel() {
		return logLevel;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
