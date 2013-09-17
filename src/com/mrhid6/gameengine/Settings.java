package com.mrhid6.gameengine;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.nio.FloatBuffer;

import com.mrhid6.gameengine.utils.BufferTools;

public class Settings {
	public final static int[] SIZE = {1200, 650};
	public final static float ASPECT_RATIO = (float) SIZE[0] / (float) SIZE[1];
	public final static String TITLE = "Working Title!";
	
	public static final FloatBuffer orthoProjectionMatrix = BufferTools.reserveData(16);
	public static final FloatBuffer perProjectionMatrix = BufferTools.reserveData(16);
	public static boolean applyGravity = false;
	public static boolean applyLighting = false;
	
	public static void changePolygonMode(){
		
		int polygonMode = glGetInteger(GL_POLYGON_MODE);
		
		if (polygonMode == GL_LINE) {
			glPolygonMode(GL_FRONT, GL_FILL);
		} else if (polygonMode == GL_FILL) {
			glPolygonMode(GL_FRONT, GL_POINT);
		} else if (polygonMode == GL_POINT) {
			glPolygonMode(GL_FRONT, GL_LINE);
		}
	}
}
