package com.mrhid6.irontide.guis;

import java.util.ArrayList;

import com.mrhid6.irontide.render.renderer.MasterRenderer;

public class GuiManager {
	
	private static GuiManager instance;
	
	private ArrayList<GuiBasic> guis = new ArrayList<GuiBasic>();
	
	private GuiDebug debugScreen;
	
	public GuiManager() {
		
		guis.add(new GuiHealthBar());
		debugScreen = new GuiDebug();
		
		instance = this;
	}
	
	public static GuiManager getInstance() {
		return instance;
	}
	
	public void addGui(GuiBasic gui){
		guis.add(gui);
	}
	
	public void processGuis(){
		
		MasterRenderer renderer = MasterRenderer.getInstance();
		
		for(int i=0;i<guis.size();i++){
			GuiBasic t = guis.get(i);
			renderer.processGui(t.getTexture());
		}
	}
	
	public GuiDebug getDebugScreen() {
		return debugScreen;
	}
	
	public void update(){
		debugScreen.update();
	}
	
}
