package com.mrhid6.mousetools;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.engine.GameEngine;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Maths;
import com.mrhid6.utils.MousePicker;
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

		if(terrainPoint !=null){

			Terrain t = TerrainGrid.getInstance().getTerrian(terrainPoint.x, terrainPoint.z);
			if( t != null){
				
				if(!heightset){
					height=terrainPoint.y;
				}
				
				float[][] heights = t.getHeights();
				
				for(int i=-raduis;i<=raduis;i++){
					for(int j=-raduis;j<=raduis;j++){

						int x = i+((int)terrainPoint.x/2);
						int z = j+((int)terrainPoint.z/2);

						if(Math.sqrt(i * i + j * j)<=raduis){
							if(x>=0 && z>=0 && x<heights.length && z<heights.length){
								heights[x][z] = heights[x][z]=Maths.clampf(height, 0, Terrain.MAX_HEIGHT*2);;
							}
						}
					}
				}

				t.setHeights(heights);
				t.generateTerrain();
			}
		}
	}

	@Override
	public String getToolName() {
		return "Level Brush Tool";
	}

}
