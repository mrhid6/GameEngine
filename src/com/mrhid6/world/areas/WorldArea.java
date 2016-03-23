package com.mrhid6.world.areas;

import java.io.IOException;

import org.json.JSONObject;

import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Loader;

public class WorldArea {
	
	private int areaID = -1;
	private String areaName = "";
	
	private boolean valid = false;
	
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
	
	public void validate(){
		if(areaID <=0 || areaName.equals("")){
			valid = false;
			return;
		}
		valid = true;
	}
	
	public void checkValidArea() throws Exception{
		if(valid == false){
			throw new Exception("WorldArea Missing Component");
		}
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
		return "res/map/area_"+idString;
	}
	
}
