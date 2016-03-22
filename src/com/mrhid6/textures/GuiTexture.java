package com.mrhid6.textures;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.render.DisplayManager;

public class GuiTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		super();
		this.texture = texture;
		
		this.position = new Vector2f(-1.0f+(position.getX()/(float)DisplayManager.WIDTH), 1.0f+(-position.getY()/(float)DisplayManager.HEIGHT));
		this.scale = new Vector2f(scale.getX()/(float)DisplayManager.WIDTH, scale.getY()/(float)DisplayManager.HEIGHT);
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
