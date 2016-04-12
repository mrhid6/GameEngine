package com.mrhid6.textures;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.settings.Constants;

public class GuiTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		super();
		this.texture = texture;
		
		float screenWidth = (float) Constants.WIDTH;
		float screenHeight = (float) Constants.HEIGHT;
		
		
		this.position = new Vector2f(-1.0f+(position.getX()/screenWidth), 1.0f+(-position.getY()/screenHeight));
		this.scale = new Vector2f(scale.getX()/screenWidth, scale.getY()/screenHeight);
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}
	
	
	
}
