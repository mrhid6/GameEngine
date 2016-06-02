package com.mrhid6.irontide.water;

import org.lwjgl.util.vector.Vector3f;

public class WaterTile {

	private Vector3f position;
	private Vector3f scale;
	private float tiling;

	public WaterTile(Vector3f position, Vector3f scale){
		this.position = new Vector3f(position.getX()+scale.getX(), position.getY(), position.getZ()+scale.getZ());
		this.scale = scale;
		this.tiling = (float) Math.sqrt(this.scale.getX()/3.8f);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getScale() {
		return scale;
	}
	
	public float getTiling() {
		return tiling;
	}

}
