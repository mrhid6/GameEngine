package com.mrhid6.irontide.world;

import java.io.IOException;

import org.json.JSONObject;

import com.mrhid6.irontide.terrians.Terrain;
import com.mrhid6.irontide.terrians.TerrainGrid;
import com.mrhid6.irontide.utils.Loader;

public class WorldArea {
	
	private int areaID = -1;
	private String areaName = "";
	
	public WorldArea(int areaID) {
		this.areaID = areaID;
		
	}
	
	public void initialize(){
		loadAreaConfig();
	}
	
	private void loadAreaConfig() {
		String configFile = getAreaURL()+"/"+getShortenedURL()+".json";
		try {
			JSONObject jsonfile = Loader.getInstance().loadJSON(configFile);
			this.areaName = jsonfile.getString("AreaName");
			
			JSONObject terrains = jsonfile.getJSONObject("Terrains");
			for(int i=0;i<terrains.length();i++){
				String key = terrains.names().get(i).toString();
				TerrainGrid.getInstance().addTerrian(new Terrain(getAreaURL()+"/"+terrains.getString(key)));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getAreaID() {
		return areaID;
	}
	
	public String getAreaName() {
		return areaName;
	}
	
	public String getAreaURL(){
		return getAreaURL(areaID);
	}
	
	private String getShortenedURL(){
		String[] strurlArr = getAreaURL().split("/");
		
		return strurlArr[strurlArr.length-1];
	}
	
	public static String getAreaURL(int areaID){
		String idString = (areaID>=10 && areaID<100 )?"0"+areaID:(areaID<10)?"00"+areaID:""+areaID;
		return "/map/area_"+idString;
	}
	
}
