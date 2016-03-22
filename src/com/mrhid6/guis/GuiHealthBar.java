package com.mrhid6.guis;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.textures.GuiTexture;
import com.mrhid6.utils.Loader;

public class GuiHealthBar extends GuiBasic {
	
	
	public GuiHealthBar() {
		Loader loader = Loader.getInstance();
		this.texture = new GuiTexture(loader.loadTexture("res/textures/health.png"), new Vector2f(20, 20f), new Vector2f(256.0F, 61.0f));
	}
}
