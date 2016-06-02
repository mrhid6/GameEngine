package com.mrhid6.irontide.mousetools;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.engine.GameEngine;
import com.mrhid6.irontide.terrians.Terrain;
import com.mrhid6.irontide.terrians.TerrainGrid;
import com.mrhid6.irontide.utils.Maths;
import com.mrhid6.irontide.utils.MousePicker;
import com.mrhid6io.utils.Input;

public class LevelBrushTool extends MouseTool{
	
	private int raduis = 10;
	
	private float height;
	private boolean heightset = false;
	
	@Override
	public void update() {
		if(Input.getKeyUp(Input.KEY_LBRACKET)){
			raduis--;
			raduis = (int) Maths.minf(raduis, 1.0f);
			System.out.println(raduis);
		}

		if(Input.getKeyUp(Input.KEY_RBRACKET)){
			raduis++;
			raduis = (int) Maths.maxf(raduis, 15.0f);
			System.out.println(raduis);
		}

		if(Input.getMouseDown(0)){
			levelTerrain();
		}
		
		if(Input.getMouseUp(0)){
			height=0;
			heightset = false;
		}
	}
	
	private void levelTerrain(){
		MousePicker picker = GameEngine.getInstance().getPicker();
		picker.update();
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		
		boolean generateNeeded = false;
		
		if(terrainPoint !=null){
			
			if(!heightset){
				height = terrainPoint.y;
			}
			
			for(int i=-raduis;i<=raduis;i++){
				for(int j=-raduis;j<=raduis;j++){
					if(Math.sqrt(i * i + j * j)<=raduis){
						
						float x = terrainPoint.x + (i);
						float z = terrainPoint.z + (j);
						
						Terrain t = TerrainGrid.getInstance().getTerrian(x, z);
						if( t != null){
							
							float relX = x - t.getX();
							float relZ = z - t.getZ();
							
							int intX = (int) relX ;
							int intZ = (int) relZ ;
							
							if(intX>=0 && intZ>=0 && intX<(Terrain.SIZE) && intZ<(Terrain.SIZE)){
								
								float[][] heights = t.getHeights();

								heights[intX][intZ] = heights[intX][intZ]=Maths.clampf(height, 0, Terrain.MAX_HEIGHT*2);
								
								t.setHeights(heights);
								generateNeeded = true;
							}
							
						}
						
					}
				}
			}
			
			if(generateNeeded){
				TerrainGrid.getInstance().generateTerrain();
			}
			
		}
	}

	@Override
	public String getToolName() {
		return "Level Brush Tool";
	}

}
