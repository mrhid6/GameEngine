package com.mrhid6.guis;

import java.util.ArrayList;

import com.mrhid6.render.renderer.MasterRenderer;

public class GuiManager {
	
	private static GuiManager instance;
	
	private ArrayList<GuiBasic> guis = new ArrayList<GuiBasic>();
	
	public GuiManager() {
		
		guis.add(new GuiHealthBar());
		
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
	
}