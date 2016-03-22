package com.mrhid6.asset;

import java.util.HashMap;

import org.json.JSONObject;

import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Loader;

public class AssetLoader {
	
	public static final String ASSETSLOCATION="res/assets";
	
	public static HashMap<String, Asset> assets = new HashMap<String, Asset>();
	
	public AssetLoader() {}
	
	public static void addAsset(Asset asset){
		assets.put(asset.getAssetName(), asset);
	}
	
	public static void debug(String text){
		System.out.println(text);
	}
	
	public static JSONObject loadAssetConfig(String configPath){
		
		debug("Loading Asset From: "+configPath);
		
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
		
		/*JSONArray arr = obj.getJSONArray("posts");
		for (int i = 0; i < arr.length(); i++)
		{
		    String post_id = arr.getJSONObject(i).getString("post_id");
		}*/
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
	public static class playerAssetLoader{
		
		public static void loadAsset(String assetName){
			Loader loader = Loader.getInstance();
			
			String assetlocation = AssetLoader.ASSETSLOCATION + "/" + assetName;
			JSONObject assetconfig = AssetLoader.loadAssetConfig(assetlocation + "/asset.json");
			
			if(assetconfig != null){
				
				String assetInfoName = assetconfig.getString("assetName");
				PlayerAsset playerAsset = new PlayerAsset(assetInfoName);
				
				String modelFile = assetconfig.getString("modelFile");
				playerAsset.setModel(loader.loadObjAsset(assetlocation + "/" + modelFile));
				
				String textureFile = assetconfig.getString("textureFile");
				
				ModelTexture texture = new ModelTexture(loader.loadTexture(assetlocation + "/" + textureFile));
				
				playerAsset.setTexture(texture);
				
				AssetLoader.addAsset(playerAsset);
				
				AssetLoader.debug("Asset Loaded " + assetName);
				
			}else{
				AssetLoader.debug("Asset Exisits Skipped load");
			}
		}
	}
	public static class staticWorldObjectAssetLoader{
		
		public static void loadAsset(String assetName){
			Loader loader = Loader.getInstance();
			
			String assetlocation = AssetLoader.ASSETSLOCATION + "/" + assetName;
			JSONObject assetconfig = AssetLoader.loadAssetConfig(assetlocation + "/asset.json");
			
			if(assetconfig != null){
				
				String assetInfoName = assetconfig.getString("assetName");
				StaticWorldObjectAsset worldObj = new StaticWorldObjectAsset(assetInfoName);
				
				String modelFile = assetconfig.getString("modelFile");
				worldObj.setModel(loader.loadObjAsset(assetlocation + "/" + modelFile));
				
				String textureFile = assetconfig.getString("textureFile");
				
				boolean isTransparent = assetconfig.getBoolean("isTransparent");
				boolean hasFakeLighting = assetconfig.getBoolean("hasFakeLighting");
				float renderDistance = Float.parseFloat(assetconfig.getString("renderDistance"));
				float height = Float.parseFloat(assetconfig.getString("height"));
				
				ModelTexture texture = new ModelTexture(loader.loadTexture(assetlocation + "/" + textureFile));
				texture.setHasTransparency(isTransparent);
				texture.setUseFakeLighting(hasFakeLighting);
				
				worldObj.setTexture(texture);
				worldObj.setRenderDistance(renderDistance);
				worldObj.setHeight(height);
				AssetLoader.addAsset(worldObj);
				
				AssetLoader.debug("Asset Loaded " + assetName);
				
			}else{
				AssetLoader.debug("Asset Exisits");
			}
			
		}
		
	}
	
}
