package com.mrhid6.render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.mrhid6.log.Logger;

public class DisplayManager {

	public static final int WIDTH = 1360;
	public static final int HEIGHT = 768;
	private static final int FPS_CAP = 120;
	private static final String TITLE = "Game V2";
	public static final float RATIO = (float) WIDTH/ (float) HEIGHT;
	
	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(){
		
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(4,4,4,4), attribs);
			Display.setTitle(TITLE);
			Logger.info("Display Created");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
	}
	public static float getFrameTimeSecond(){
		return delta;
	}
	public static void updateDisplay(){
		
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}

	public static void closeDisplay(){
		Display.destroy();
		Logger.info("Display Destroyed");
	}
	
	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

}
