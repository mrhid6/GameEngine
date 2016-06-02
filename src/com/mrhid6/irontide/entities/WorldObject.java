package com.mrhid6.irontide.entities;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.asset.AssetLoader;
import com.mrhid6.irontide.asset.StaticWorldObjectAsset;
import com.mrhid6.irontide.utils.Maths;

public class WorldObject {
	
	private StaticWorldObjectAsset asset;
	private Vector3f position;
	private float rotX,rotY,rotZ;
	private float scale;
	private String name;
	
	private float distanceAlpha = 0.0f;
	
	private int textureIndex = 0;
	
	public WorldObject(String name, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.name = name;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		
		this.asset = (StaticWorldObjectAsset) AssetLoader.getAsset(name);
	}
	
	public String getName() {
		return name;
	}
	
	public WorldObject(String name, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this(name, position, rotX, rotY, rotZ, scale);
		this.textureIndex = index;
	}
	
	public float getTextureXOffset(){
		int column = textureIndex%asset.getTexture().getNumberOfRows();
		return (float)column/(float)asset.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset(){
		int row = textureIndex/asset.getTexture().getNumberOfRows();
		return (float)row/(float)asset.getTexture().getNumberOfRows();
	}

	public StaticWorldObjectAsset getAsset() {
		return asset;
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
	
	public float getRenderDistance(){
		return asset.getRenderDistance();
	}
	
	public void updateDistanceAlpha(float distance){
		float diff = getRenderDistance() / distance;
		float sinkAmount = asset.getHeight() * Maths.clampf(diff, 0.0f, 1.0f);
		position.y = sinkAmount;
	}
	
	public float getDistanceAlpha() {
		return distanceAlpha;
	}
	
}
