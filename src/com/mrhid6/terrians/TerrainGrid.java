package com.mrhid6.terrians;

import java.util.ArrayList;

import com.mrhid6.api.ICleanUpable;

public class TerrainGrid implements ICleanUpable{
	
	
	private static TerrainGrid instance;
	
	private ArrayList<Terrain> terrians = new ArrayList<Terrain>();
	
	public TerrainGrid() {
		
		instance = this;
	}
	
	public void generateTerrain(){
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			t.generateTerrain();
		}
	}
	
	public static TerrainGrid getInstance() {
		return instance;
	}
	
	public void addTerrian(Terrain t){
		terrians.add(t);
	}
	
	public Terrain getTerrian(float x, float z){
		
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			boolean xMatch = false;
			boolean zMatch = false;
			
			if( x>=t.getX() && x<(t.getX()+Terrain.SIZE)){
				xMatch = true;
			}
			if( z>=t.getZ() && z<(t.getZ()+Terrain.SIZE)){
				zMatch = true;
			}
			
			if(xMatch && zMatch){
				//System.out.println("Match x:"+x+", z:"+z);
				return t;
			}else{
				//System.out.println("id:"+i+",xmatch:"+xMatch+",zmatch:"+zMatch);
				//System.out.println("z:"+z+"tz:"+t.getZ());
			}
			
		}
		
		return null;
	}
	
	public float getHeight(float x, float z){
		Terrain t = getTerrian(x, z);
		
		if(t != null){
			int intX = (int)((x-t.getX())/2);
			int intZ = (int)((z-t.getZ())/2);
			
			float[][] heights = t.getHeights();
			
			return heights[intX][intZ];
		}
		
		return 0;
	}
	
	public void saveAllTerrains(){
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			t.saveTerrain();
		}
	}
	
	public ArrayList<Terrain> getTerrians() {
		return terrians;
	}
	
	@Override
	public void cleanUp(){
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			t.cleanUp();
		}
	}
}
