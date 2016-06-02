package com.mrhid6.irontide.asset;

import com.mrhid6.irontide.models.RawModel;
import com.mrhid6.irontide.textures.ModelTexture;

public class StaticWorldObjectAsset extends Asset{
	
	
	private RawModel model;
	private ModelTexture texture;
	private float renderDistance = 200.0f;
	private float height = 0f;
	
	public StaticWorldObjectAsset(String assetName) {
		super(assetName);
	}
	
	public RawModel getModel() {
		return model;
	}
	
	public void setModel(RawModel model) {
		this.model = model;
	}
	
	public void setTexture(ModelTexture modelTexture) {
		this.texture = modelTexture;
	}
	
	public ModelTexture getTexture() {
		return texture;
	}

	public float getRenderDistance() {
		return renderDistance;
	}
	
	public void setRenderDistance(float renderDistance) {
		this.renderDistance = renderDistance;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}

}
