package com.mrhid6.entities;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.asset.AnimatedAsset;
import com.mrhid6.render.armature.Animation;

public class AnimatedEntity extends Entity{
	
	private AnimatedAsset asset;
	private HashMap<Integer, Animation> animations = new HashMap<Integer, Animation>();;
	
	public AnimatedEntity(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(position, rotX, rotY, rotZ, scale);
	}
	
	public void setAsset(AnimatedAsset asset) {
		this.asset = asset;
	}
	
	public AnimatedAsset getAsset() {
		return asset;
	}
	
	public HashMap<Integer, Animation> getAnimations() {
		return animations;
	}

}
