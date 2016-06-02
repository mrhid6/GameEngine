package com.mrhid6.irontide.render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.mrhid6.irontide.log.Logger;
import com.mrhid6.irontide.settings.Constants;
import com.mrhid6.irontide.settings.GameSettings;

public class DisplayManager {

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(){

		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);

		try {
			DisplayMode displayMode = null;
			DisplayMode[] modes = Display.getAvailableDisplayModes();

			for (int i = 0; i < modes.length; i++)
			{
				if (modes[i].getWidth() == GameSettings.WIDTH
						&& modes[i].getHeight() == GameSettings.HEIGHT
						&& modes[i].isFullscreenCapable())
				{
					displayMode = modes[i];
				}
			}
			Display.setDisplayMode(displayMode);
			Display.setFullscreen(true);
			Display.create(new PixelFormat(4,4,4,4), attribs);
			Display.setTitle(Constants.TITLE + " Alpha v"+GameSettings.CURRENTVERSION.displayVersion());

			
			Logger.info("Display Created");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glViewport(0, 0, GameSettings.WIDTH, GameSettings.HEIGHT);
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

		//System.out.println(1f/delta);
	}

	public static void closeDisplay(){
		Display.destroy();
		Logger.info("Display Destroyed");
	}

	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

}
