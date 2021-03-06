package com.mrhid6.irontide.asset;

import com.mrhid6.irontide.models.RawModel;
import com.mrhid6.irontide.textures.ModelTexture;

public class PlayerAsset extends Asset {

	public PlayerAsset(String assetName) {
		super(assetName);
	}
	
	private RawModel model;
	private ModelTexture texture;
	
	
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
	
}
