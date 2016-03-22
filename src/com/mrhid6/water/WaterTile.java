package com.mrhid6.water;

import org.lwjgl.util.vector.Vector3f;

public class WaterTile {

	private Vector3f position;
	private Vector3f scale;

	public WaterTile(Vector3f position, Vector3f scale){
		this.position = new Vector3f(position.getX()+scale.getX(), position.getY(), position.getZ()+scale.getZ());
		this.scale = scale;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getScale() {
		return scale;
	}

}
