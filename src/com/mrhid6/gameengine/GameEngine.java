package com.mrhid6.gameengine;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.mrhid6.gameengine.camera.CameraManager;
import com.mrhid6.gameengine.render.RenderManager;
import com.mrhid6.gameengine.shader.ShaderManager;
import com.mrhid6.gameengine.world.WorldHeightMap;

public class GameEngine {

	private RenderManager renderManager;

	public GameEngine() {
		try{
			InitDisplay();
			InitShaders();
			InitOpenGL();
			InitCamera();
			new WorldHeightMap();
			while(!Display.isCloseRequested()){
				renderManager.render();
				updateEngine();
				handleInput();
			}
			cleanUp(false, true);
		}catch(Exception e){
			GameCrashed(e);
		}
	}

	public void handleInput(){

		if(Input.getKeyUp(Input.KEY_F1)){
			Settings.changePolygonMode();
		}
		if(Input.getKeyUp(Input.KEY_F12)){
			cleanUp(false,false);
		}
		if(Input.getKeyUp(Input.KEY_G)){
			Settings.applyGravity = !Settings.applyGravity;
		}
		if(Input.getKeyUp(Input.KEY_L)){
			Settings.applyLighting = !Settings.applyLighting;
		}

		handleInputCamera();

		Input.update();
	}

	public void handleInputCamera(){
		if(Input.getMouseUp(0)){
			Input.setCursor(false);
		}else if(Input.getMouseUp(1)){
			Input.setCursor(true);
		}

		if (Input.isMouseGrabbed()) {
			CameraManager.handleMouse();
		}

		CameraManager.handleKey();
	}

	public void updateEngine(){
		Display.update();
		Display.sync(60);
	}

	public void InitDisplay() throws Exception{
		Display.setDisplayMode(new DisplayMode(Settings.SIZE[0], Settings.SIZE[1]));
		Display.setVSyncEnabled(true);
		Display.setTitle("GameEngine");
		Display.create();
		
		GuiText.init();
		
		CameraManager.Init();
		renderManager = RenderManager.newInstance();
	}
	
	public void InitLighting(){
		
	}

	public void InitShaders() throws Exception{
		ShaderManager.Init();
	}
	public void InitOpenGL(){
		glViewport(0, 0, Settings.SIZE[0], Settings.SIZE[1]); // Reset The Current Viewport
		glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Test To Do
		glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations
		glEnable(GL11.GL_TEXTURE_2D);
		glEnable(GL11.GL_BLEND);
		glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glShadeModel(GL11.GL_SMOOTH);       
		glDisable(GL11.GL_LIGHTING);                              
		glClearDepth(1);                                      

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		CameraManager.InitStates();
		glPointSize(2);
		glClearColor(0, 0.75f, 1, 1);
		glEnable(GL_CULL_FACE);

		CameraManager.InitPerspective();
	}

	public void InitCamera(){
		glGetFloat(GL_PROJECTION_MATRIX, Settings.perProjectionMatrix);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, Settings.orthoProjectionMatrix);
		glLoadMatrix(Settings.perProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}

	public void GameCrashed(Exception e){
		e.printStackTrace();
		cleanUp(true, true);
	}

	private void cleanUp(boolean asCrash,boolean end) {
		System.err.println(GLU.gluErrorString(glGetError()));
		Display.destroy();
		if(end)
			System.exit(asCrash ? 1 : 0);
	}

}
