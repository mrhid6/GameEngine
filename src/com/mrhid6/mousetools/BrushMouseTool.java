package com.mrhid6.mousetools;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.engine.GameEngine;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Maths;
import com.mrhid6.utils.MousePicker;
import com.mrhid6io.utils.Input;

public class BrushMouseTool extends MouseTool {
	
	private int raduis = 10;
	
	private Vector3f reuseableV3f = new Vector3f();
	
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
		
		if(Mouse.isButtonDown(0)){
			adjustTerrainHeight(0.1F, raduis);
		}
		if(Mouse.isButtonDown(1)){
			adjustTerrainHeight(-0.1F, raduis);
		}
		
		if(Mouse.isButtonDown(2)){
			smoothTerrain(0.1f, raduis);
		}
	}


	private void adjustTerrainHeight(float amount, int raduis){
		MousePicker picker = GameEngine.getInstance().getPicker();
		picker.update();
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		if(terrainPoint !=null){
			Terrain t = TerrainGrid.getInstance().getTerrian(terrainPoint.x, terrainPoint.z);
			float[][] heights = t.getHeights();

			for(int i=-raduis;i<=raduis;i++){
				for(int j=-raduis;j<=raduis;j++){
					
					int x = i+((int)terrainPoint.x/2);
					int z = j+((int)terrainPoint.z/2);
					
					if(Math.sqrt(i * i + j * j)<=raduis){
						if(x>=0 && z>=0 && x<heights.length && z<heights.length){
							float prevHeight = heights[x][z];
							
							reuseableV3f.set(x*2, terrainPoint.y, z*2);
							float distance = (Maths.distance(terrainPoint, reuseableV3f)/raduis)*0.1F;
							float newAmount =(amount>0)?Maths.clampf((amount-distance), 0F, 1F):Maths.clampf((amount+distance), -1F, 0F);
							
							float newheight=prevHeight+newAmount;
							heights[x][z] = heights[x][z]=Maths.clampf(newheight, 0, Terrain.MAX_HEIGHT*2);;
						}
					}
				}
			}

			t.setHeights(heights);
			t.generateTerrain();
		}
	}
	
	private void smoothTerrain(float amount, int raduis){
		MousePicker picker = GameEngine.getInstance().getPicker();
		picker.update();
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		
		if(terrainPoint !=null){
			Terrain t = TerrainGrid.getInstance().getTerrian(terrainPoint.x, terrainPoint.z);
			float[][] heights = t.getHeights();
			
			for(int i=-raduis;i<=raduis;i++){
				for(int j=-raduis;j<=raduis;j++){
					
					int x = i+((int)terrainPoint.x/2);
					int z = j+((int)terrainPoint.z/2);
					
					if(Math.sqrt(i * i + j * j)<=raduis){
						if(x>=0 && z>=0 && x<heights.length && z<heights.length){
							float heightTL = getHeight(heights, x-1, z-1);
							float heightTM = getHeight(heights, x-1, z);
							float heightTR = getHeight(heights, x-1, z+1);
							float heightCL = getHeight(heights, x, z-1);
							//float heightCM = getHeight(heights, x, z);
							float heightCR = getHeight(heights, x, z+1);
							float heightBL = getHeight(heights, x+1, z-1);
							float heightBM = getHeight(heights, x+1, z);
							float heightBR = getHeight(heights, x+1, z+1);
							
							
							//float prevHeight = heights[x][z];
							float average = (heightTL + heightTM + heightTR + heightCL + heightCR + heightBL + heightBM + heightBR) / 8.0f;
							/*float diff = prevHeight - average;*/
							reuseableV3f.set(x*2, terrainPoint.y, z*2);
							float distance = (Maths.distance(terrainPoint, reuseableV3f)/raduis)*0.001F;
							
							heights[x][z]=Maths.clampf(average+distance, 0, Terrain.MAX_HEIGHT*2);
						}
					}
				}
			}
			t.setHeights(heights);
			t.generateTerrain();
			
		}
	}
	
	private float getHeight(float[][] heights, int x, int z){
		if(x<0 || x>=heights.length || z<0 || z>=heights.length){
			return 0;
		}
		
		return heights[x][z];
	}
}
