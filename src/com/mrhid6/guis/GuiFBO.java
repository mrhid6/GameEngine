package com.mrhid6.guis;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.render.DisplayManager;
import com.mrhid6.textures.GuiTexture;

public class GuiFBO extends GuiBasic{
	
	
	public GuiFBO(int texture, int id) {
		
		float scaleX = DisplayManager.WIDTH/5;
		float scaleY = DisplayManager.HEIGHT/5;
		float posX = (id==1)?20f:40+scaleX;
		float posY = 80f;
		
		this.texture = new GuiTexture(texture, new Vector2f(posX,posY), new Vector2f(scaleX,scaleY));
	}
}
