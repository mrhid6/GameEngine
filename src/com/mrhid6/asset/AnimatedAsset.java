package com.mrhid6.asset;

import java.util.ArrayList;

import com.mrhid6.models.AnimatedModel;
import com.mrhid6.render.armature.Animation;
import com.mrhid6.render.armature.Armature;

public class AnimatedAsset extends Asset{

	private AnimatedModel model;
	private Armature armature;
	private ArrayList<Animation> animations;

	public AnimatedAsset(String assetName) {
		super(assetName);
	}

	public void setModel(AnimatedModel model) {
		this.model = model;
	}
	
	public AnimatedModel getModel() {
		return model;
	}

	public void setArmature(Armature armature) {
		this.armature = armature;
	}
	
	public Armature getArmature() {
		return armature;
	}

	public void setAnimations(ArrayList<Animation> animations) {
		this.animations = animations;
	}
	
	public ArrayList<Animation> getAnimations() {
		return animations;
	}
	
	public AnimatedAsset clone(){
		AnimatedAsset asset = new AnimatedAsset(getAssetName());
		
		Armature a = new Armature(this.armature);
		a.setArmatureToBones();
		a.toString();
		
		ArrayList<Animation> animations = new ArrayList<Animation>();
		for (int i = 0; i < this.animations.size(); i++) {
			animations.add(new Animation(this.animations.get(i), a));
		}
		asset.setModel(model);
		asset.setArmature(a);
		asset.setAnimations(animations);
		return asset;
	}
	
}
