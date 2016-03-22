package com.mrhid6.world.areas;

import com.mrhid6.terrians.TerrainGrid;

public class GoblinVillage extends WorldArea{
	
	public GoblinVillage() {
		
		super(1);
		
		/*TerrianTexture backgroundTexture = new TerrianTexture(loader.loadTexture(getAreaURL()+"/textures/grass.png"));
		TerrianTexture rTexture = new TerrianTexture(loader.loadTexture(getAreaURL()+"/textures/dirt.png"));
		TerrianTexture gTexture = new TerrianTexture(loader.loadTexture(getAreaURL()+"/textures/meadow.png"));
		TerrianTexture bTexture = new TerrianTexture(loader.loadTexture(getAreaURL()+"/textures/path.png"));
		
		TerrianTexturePack texturePack = new TerrianTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainGrid.getInstance().addTerrian(new Terrain(getAreaID(), 0, 0, texturePack));
		*/
		//TerrainGrid.getInstance().addTerrian(new Terrain(getAreaID(), 1, 0, texturePack));
		//TerrainGrid.getInstance().addTerrian(new Terrain(getAreaID(), 0, 1, texturePack));
		//TerrainGrid.getInstance().addTerrian(new Terrain(getAreaID(), 1, 1, texturePack));
		
		TerrainGrid.getInstance().generateTerrain();
		
		//validate();

	}
	
}
