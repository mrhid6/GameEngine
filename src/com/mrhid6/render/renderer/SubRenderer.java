package com.mrhid6.render.renderer;

import org.lwjgl.util.vector.Vector4f;

public abstract class SubRenderer {
	
	protected Vector4f clipPlane;
	
	public abstract void cleanUp();
	
	public void setClipPlane(Vector4f clipPlane) {
		this.clipPlane = clipPlane;
	}
	
}
