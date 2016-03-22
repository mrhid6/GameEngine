package com.mrhid6.entities;

import org.lwjgl.util.vector.Vector3f;

public class Entity {

	private Vector3f position;
	private float rotX,rotY,rotZ;
	private float scale;
	private boolean hasMoved = false;
	
	public Entity(Vector3f position, float rotX, float rotY,
			float rotZ, float scale) {
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public void increasePosition(float dx, float dy, float dz){
		
		float prevX = this.position.x;
		float prevY = this.position.y;
		float prevZ = this.position.z;
		
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		
		
		float diffX = (prevX>position.x)?(prevX - position.x):(position.x-prevX);
		float diffY = (prevY>position.y)?(prevY - position.y):(position.y-prevY);
		float diffZ = (prevZ>position.z)?(prevZ - position.z):(position.z-prevZ);
		
		if(diffX > 0.005f || diffY > 0.005f || diffZ > 0.005f){
			hasMoved = true;
		}else{
			hasMoved = false;
		}
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void increaseRotation(float dx, float dy, float dz){
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public Vector3f getPosition() {
		return position;
	}


	public void setPosition(Vector3f position) {
		this.position = position;
	}


	public float getRotX() {
		return rotX;
	}


	public void setRotX(float rotX) {
		this.rotX = rotX;
	}


	public float getRotY() {
		return rotY;
	}


	public void setRotY(float rotY) {
		this.rotY = rotY;
	}


	public float getRotZ() {
		return rotZ;
	}


	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}


	public float getScale() {
		return scale;
	}


	public void setScale(float scale) {
		this.scale = scale;
	}
	
	
	
}
