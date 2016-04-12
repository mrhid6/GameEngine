package com.mrhid6.asset;

import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONObject;

import com.mrhid6.log.Logger;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Loader;

public class AssetLoader {

	public static HashMap<String, Asset> assets = new HashMap<String, Asset>();

	public AssetLoader(){
		loadAsset("grass");
	}

	public static void loadAsset(String assetName){
		Logger.info("Loading Asset: "+assetName);
		
		if(assetExists(assetName)){
			Logger.info("Using Cached Asset: "+assetName);
			return;
		}

		Loader loader = Loader.getInstance();

		String basePath = "D:\\Program Files\\Irontide\\assets";
		String assetFilePath = basePath+"\\"+assetName+".zip";

		ZipFile zip = null;
		AssetType assetType = null;

		try{
			zip = new ZipFile(assetFilePath);
			ZipEntry assetEntry = zip.getEntry("asset.json");

			JSONObject obj = loader.loadJSONFR(zip.getInputStream(assetEntry));

			JSONObject assetconfig = obj.getJSONObject("assetInfo");
			assetType = AssetType.getType(assetconfig.getInt("assetType"));

			if(assetType == AssetType.StaticWorldObject){
				loadSWOAsset(assetconfig, zip);
			}else if(assetType == AssetType.Player){
				loadPlayerAsset(assetconfig, zip);
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(zip != null){
				try {
					zip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

	public static void addAsset(Asset asset){
		Logger.info("Adding Asset: "+asset.getAssetName()+" To Cache Array");
		assets.put(asset.getAssetName(), asset);
	}

	// Static World Object (SWO) Loader..

	private static void loadSWOAsset(JSONObject assetconfig, ZipFile zip){
		Loader loader = Loader.getInstance();

		String assetInfoName = assetconfig.getString("assetName");
		Logger.info("Loading SWO Asset: "+assetInfoName);
		
		StaticWorldObjectAsset worldObj = new StaticWorldObjectAsset(assetInfoName);
		
		boolean isTransparent = assetconfig.getBoolean("isTransparent");
		boolean hasFakeLighting = assetconfig.getBoolean("hasFakeLighting");
		float renderDistance = Float.parseFloat(assetconfig.getString("renderDistance"));
		float height = Float.parseFloat(assetconfig.getString("height"));
		
		
		worldObj.setRenderDistance(renderDistance);
		worldObj.setHeight(height);
	
		try{
			String modelFile = assetconfig.getString("modelFile");
			ZipEntry modelEntry = zip.getEntry(modelFile);

			if(modelEntry != null){
				worldObj.setModel(loader.loadObjAsset(zip.getInputStream(modelEntry)));
			}
			
			String textureFile = assetconfig.getString("textureFile");
			ZipEntry textureEntry = zip.getEntry(textureFile);
			if(textureEntry != null){
				String identifier = assetInfoName+":"+textureFile;
				ModelTexture texture = new ModelTexture(loader.loadTexture(identifier,zip.getInputStream(textureEntry)));
				texture.setHasTransparency(isTransparent);
				texture.setUseFakeLighting(hasFakeLighting);
				worldObj.setTexture(texture);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		AssetLoader.addAsset(worldObj);
	}
	
	// Player Loader..

		private static void loadPlayerAsset(JSONObject assetconfig, ZipFile zip){
			Loader loader = Loader.getInstance();

			String assetInfoName = assetconfig.getString("assetName");
			Logger.info("Loading Player Asset: "+assetInfoName);
			
			PlayerAsset playerAsset = new PlayerAsset(assetInfoName);
		
			try{
				String modelFile = assetconfig.getString("modelFile");
				ZipEntry modelEntry = zip.getEntry(modelFile);

				if(modelEntry != null){
					playerAsset.setModel(loader.loadObjAsset(zip.getInputStream(modelEntry)));
				}
				
				String textureFile = assetconfig.getString("textureFile");
				ZipEntry textureEntry = zip.getEntry(textureFile);
				if(textureEntry != null){
					String identifier = assetInfoName+":"+textureFile;
					ModelTexture texture = new ModelTexture(loader.loadTexture(identifier,zip.getInputStream(textureEntry)));
					playerAsset.setTexture(texture);
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			AssetLoader.addAsset(playerAsset);
		}
}
