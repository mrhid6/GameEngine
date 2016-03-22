package com.mrhid6.mousetools;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.engineTester.MainGameLoop;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Maths;
import com.mrhid6.utils.MousePicker;
import com.mrhid6io.utils.Input;

public class BrushMouseTool extends MouseTool {
	
	private int raduis = 10;
	
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
	}


	private void adjustTerrainHeight(float amount, int raduis){
		MousePicker picker = MainGameLoop.getInstance().getPicker();
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
							float distance = (Maths.distance(terrainPoint, new Vector3f(x*2, terrainPoint.y, z*2))/20)*0.1F;
							float newAmount =(amount>0)?(amount-distance):(amount+distance);
							float newheight=prevHeight+newAmount;
							if(newheight>0 && newheight<Terrain.MAX_HEIGHT*2){
								heights[x][z] = prevHeight+newAmount;
							}
							
							if(heights[x][z]>Terrain.MAX_HEIGHT*2)heights[x][z]=Terrain.MAX_HEIGHT*2;
							if(heights[x][z]<0)heights[x][z]=0;
						}
					}
				}
			}

			t.setHeights(heights);
			t.generateTerrain();
		}
	}
}
