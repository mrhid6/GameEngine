package com.mrhid6.world;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.asset.AssetLoader;
import com.mrhid6.entities.Camera;
import com.mrhid6.entities.Light;
import com.mrhid6.entities.Player;
import com.mrhid6.entities.WorldObject;
import com.mrhid6.log.Logger;
import com.mrhid6.render.renderer.MasterRenderer;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Loader;
import com.mrhid6.world.areas.WorldArea;

public class World {
	
	
	private static World instance;
	
	private WorldArea worldArea;
	private Light worldSun;
	private Player worldPlayer;
	private Camera worldCamera;
	
	private ArrayList<WorldObject> worldObjs = new ArrayList<WorldObject>();
	
	// Temp Variables;
	private final int areaToLoad = 1;
	
	public World() {
		
		worldArea = new WorldArea(areaToLoad);
		instance = this;
	}
	
	public void initialize(){
		Logger.info("Initialized");
		
		worldSun = new Light(new Vector3f(0, 1000, -700f), new Vector3f(0.6F, 0.6F, 0.6F));
		MasterRenderer.getInstance().addLight(worldSun);
		
		worldArea.initialize();
		TerrainGrid.getInstance().generateTerrain();
		
		loadPlayer();
		loadDooDads();
	}
	
	private void loadDooDads(){
		
		Logger.info("Loading Doodads");
		
		String configFile = worldArea.getAreaURL()+"/doodads.json";
		
		try {
			JSONObject jsonfile = Loader.getInstance().loadJSON(configFile);
			JSONArray worldObjs = jsonfile.getJSONArray("WorldObjects");
			JSONObject staticObjs = worldObjs.getJSONObject(0).getJSONObject("static");
			
			for(int i=0;i<staticObjs.length();i++){
				String key = staticObjs.names().get(i).toString();
				JSONObject worldObj = staticObjs.getJSONObject(key);
				String assetName = worldObj.getString("name");
				
				AssetLoader.loadAsset(assetName);
				
				float x = (float) worldObj.getDouble("x");
				float y = (float) worldObj.getDouble("y");
				float z = (float) worldObj.getDouble("z");
				
				this.worldObjs.add(new WorldObject("grass", new Vector3f(x, y, z), 0, 0, 0, 2));
			}
			
			Logger.info("Finished Loading Doodads");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update(){
		worldPlayer.move();
		worldCamera.move();
	}
	
	private void loadPlayer(){
		Logger.info("Loading Player");
		worldPlayer = new Player(new Vector3f(365, 5, 52), 0, 70, 0, 1);
		worldCamera = new Camera(worldPlayer);
		Logger.info("Finished Loading Player");
	}
	
	public void prepareForRender(){
		for(WorldObject worldObj : worldObjs){
			MasterRenderer.getInstance().processWorldObject(worldObj);
		}
		MasterRenderer.getInstance().processPlayer(worldPlayer);
	}
	
	public static World getInstance() {
		return instance;
	}
	
	public Player getWorldPlayer() {
		return worldPlayer;
	}
	
	public Light getWorldSun() {
		return worldSun;
	}
	
	public WorldArea getWorldArea() {
		return worldArea;
	}
}
