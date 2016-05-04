package com.mrhid6.mousetools;

public class MouseToolManager {
	
	public static MouseTool STANDARD_MOUSE = new StandardMouseTool();
	public static MouseTool BRUSH_MOUSE = new BrushMouseTool();
	public static MouseTool LEVEL_MOUSE = new LevelBrushTool();
	
	private MouseTool currentTool = STANDARD_MOUSE;

	private static MouseToolManager instance;
	
	public MouseToolManager() {
		
		instance = this;
	}
	
	public void update(){
		currentTool.update();
	}
	
	
	public static MouseToolManager getInstance() {
		return instance;
	}
	
	public void setCurrentTool(MouseTool currentTool) {
		this.currentTool = currentTool;
	}
	
	public MouseTool getCurrentTool() {
		return currentTool;
	}
}
