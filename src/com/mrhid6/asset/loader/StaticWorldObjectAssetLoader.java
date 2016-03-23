package com.mrhid6.asset.loader;

import org.json.JSONObject;

import com.mrhid6.asset.AssetLoader;
import com.mrhid6.asset.StaticWorldObjectAsset;
import com.mrhid6.log.Logger;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Loader;

public class StaticWorldObjectAssetLoader{
	
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
			
			Logger.info("Asset Loaded '" + assetName+"'");
			
		}else{
			Logger.info("Asset '"+assetName+"' Exisits Skipped Load");
		}
		
	}
	
}
