package com.mrhid6.render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.mrhid6.log.Logger;
import com.mrhid6.settings.Constants;

public class DisplayManager {
	
	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(){
		
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(Constants.WIDTH, Constants.HEIGHT));
			Display.create(new PixelFormat(4,4,4,4), attribs);
			Display.setTitle(Constants.TITLE);
			Logger.info("Display Created");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, Constants.WIDTH, Constants.HEIGHT);
		lastFrameTime = getCurrentTime();
	}
	public static float getFrameTimeSecond(){
		return delta;
	}
	public static void updateDisplay(){
		
		Display.sync(Constants.FPS_CAP);
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
