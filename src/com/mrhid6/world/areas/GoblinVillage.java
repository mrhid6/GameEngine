package com.mrhid6.world.areas;

import com.mrhid6.terrians.TerrainGrid;

public class GoblinVillage extends WorldArea{
	
	public GoblinVillage() {
		
		super(1);
		
		TerrainGrid.getInstance().generateTerrain();

	}
	
}
