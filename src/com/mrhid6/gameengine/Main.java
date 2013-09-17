package com.mrhid6.gameengine;

public class Main {
	
	private static GameEngine gameEngine;
	
	public static void main(String[] args) {
		gameEngine = new GameEngine();
	}
	
	public static GameEngine getGameEngine() {
		return gameEngine;
	}
	

}
