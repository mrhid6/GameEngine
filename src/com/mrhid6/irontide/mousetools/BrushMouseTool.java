package com.mrhid6.irontide.mousetools;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.engine.GameEngine;
import com.mrhid6.irontide.terrians.Terrain;
import com.mrhid6.irontide.terrians.TerrainGrid;
import com.mrhid6.irontide.utils.Maths;
import com.mrhid6.irontide.utils.MousePicker;
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
			smoothTerrain(0.1f, raduis/3);
		}
	}


	private void adjustTerrainHeight(float amount, int raduis){
		MousePicker picker = GameEngine.getInstance().getPicker();
		picker.update();
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		
		boolean generateNeeded = false;
		
		if(terrainPoint !=null){
			
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
								
								float prevHeight = heights[intX][intZ];

								reuseableV3f.set(x, terrainPoint.y, z);
								float distance = (Maths.distance(terrainPoint, reuseableV3f)/raduis)*0.1F;
								float newAmount =(amount>0)?Maths.clampf((amount-distance), 0F, 1F):Maths.clampf((amount+distance), -1F, 0F);

								float newheight=prevHeight+newAmount;
								heights[intX][intZ] = Maths.clampf(newheight, 0, Terrain.MAX_HEIGHT*2);
								
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

	private void smoothTerrain(float amount, int raduis){
		MousePicker picker = GameEngine.getInstance().getPicker();
		TerrainGrid tg = TerrainGrid.getInstance();
		
		picker.update();
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		boolean generateNeeded = false;
		
		if(terrainPoint !=null){
			
			for(int i=-raduis;i<=raduis;i++){
				for(int j=-raduis;j<=raduis;j++){
					if(Math.sqrt(i * i + j * j)<=raduis){
						
						float x = terrainPoint.x + (i);
						float z = terrainPoint.z + (j);
						
						
						float heightTM = tg.getHeight(x-2, z);
						float heightCL = tg.getHeight(x, z-2);
						float heightCM = tg.getHeight(x, z);
						float heightCR = tg.getHeight(x, z+2);
						float heightBM = tg.getHeight(x+2, z);
						
						float average = ( heightTM + heightCL + heightCR + heightBM + heightCM) / 5.0f;
						
						Terrain t = TerrainGrid.getInstance().getTerrian(x, z);
						if( t != null){
							t.restitchTerrain();
							
							float relX = x - t.getX();
							float relZ = z - t.getZ();
							
							int intX = (int) relX ;
							int intZ = (int) relZ ;
							
							System.out.println("x:"+intX+", z:"+intZ);
							
							if(intX>=0 && intZ>=0 && intX<(Terrain.SIZE) && intZ<(Terrain.SIZE)){
								float[][] heights = t.getHeights();
						
								heights[intX][intZ]=Maths.clampf(average, 0, Terrain.MAX_HEIGHT*2);
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
		return "Terrain Brush Tool";
	}
}
