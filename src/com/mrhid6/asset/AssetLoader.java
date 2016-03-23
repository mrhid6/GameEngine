package com.mrhid6.asset;

import java.util.HashMap;

import org.json.JSONObject;

import com.mrhid6.log.Logger;
import com.mrhid6.utils.Loader;

public class AssetLoader {
	
	public static final String ASSETSLOCATION="res/assets";
	
	public static HashMap<String, Asset> assets = new HashMap<String, Asset>();
	
	public AssetLoader() {}
	
	public static void addAsset(Asset asset){
		assets.put(asset.getAssetName(), asset);
	}
	
	public static JSONObject loadAssetConfig(String configPath){
		
		Logger.info("Loading Asset From: "+configPath);
		
		try{
			
			JSONObject obj = Loader.getInstance().loadJSON(configPath);
			JSONObject JSONobj = obj.getJSONObject("assetInfo");
			
			String tmpAssetName = JSONobj.getString("assetName");
			
			if(assetExists(tmpAssetName)) JSONobj = null;
			
			
			return JSONobj;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean assetExists(String assetName){
		
		for(String name : assets.keySet()){
			if(assets.get(name).getAssetName().equalsIgnoreCase(assetName)){
				return true;
			}
		}
		
		return false;
	}
	
	public static Asset getAsset(String assetName){
		
		if(assetExists(assetName)){
			return assets.get(assetName);
		}
		
		return null;
	}
	
}
