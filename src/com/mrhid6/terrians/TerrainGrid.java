package com.mrhid6.terrians;

import java.util.ArrayList;

import com.mrhid6.render.renderer.MasterRenderer;

public class TerrainGrid {
	
	
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
	
	public void processTerrians(){
		MasterRenderer renderer = MasterRenderer.getInstance();
		
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			renderer.processTerrian(t);
		}
	}
	
	public Terrain getTerrian(float x, float z){
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			
			if( x>=t.getX() && x<t.getX()+Terrain.SIZE && z>=t.getZ() && z<t.getZ()+Terrain.SIZE){
				return t;
			}
		}
		
		return null;
	}
	
	public void saveAllTerrains(){
		for(int i=0;i<terrians.size();i++){
			Terrain t = terrians.get(i);
			t.saveTerrain();
		}
	}
}
