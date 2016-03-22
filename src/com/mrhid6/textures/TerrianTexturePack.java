package com.mrhid6.textures;

public class TerrianTexturePack {
	
	private TerrianTexture baseTexture;
	private TerrianTexture rTexture;
	private TerrianTexture gTexture;
	private TerrianTexture bTexture;
	private TerrianTexture splatTexture;
	
	
	public TerrianTexturePack(TerrianTexture baseTexture, TerrianTexture rTexture, TerrianTexture gTexture, TerrianTexture bTexture,
			TerrianTexture splatTexture) {
		this.baseTexture = baseTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
		this.splatTexture = splatTexture;
	}


	public TerrianTexture getBaseTexture() {
		return baseTexture;
	}
	
	public TerrianTexture getrTexture() {
		return rTexture;
	}

	public TerrianTexture getgTexture() {
		return gTexture;
	}

	public TerrianTexture getbTexture() {
		return bTexture;
	}
	
	public TerrianTexture getSplatTexture() {
		return splatTexture;
	}
	
}
