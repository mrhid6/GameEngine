package com.mrhid6.gameengine.camera;

import com.mrhid6.gameengine.Settings;

public class CameraManager {
	
	private static Camera activeCamera;
	
	private static Camera worldCamera;
	
	public static void Init(){
		worldCamera = new EulerCamera.Builder().setPosition(0, 0,-32).setRotation(0, 0, 0).setAspectRatio(Settings.ASPECT_RATIO).setFieldOfView(60).build();
		activeCamera = worldCamera;
	}
	
	public static Camera getCamera(){
		return activeCamera;
	}
	
	public static void InitStates(){
		activeCamera.applyOptimalStates();
	}
	
	public static void InitPerspective(){
		activeCamera.applyPerspectiveMatrix();
	}

	public static void handleMouse() {
		getCamera().processMouse(1);
	}

	public static void handleKey() {
		getCamera().processKeyboard(16, 3);
	}
	
	
}
