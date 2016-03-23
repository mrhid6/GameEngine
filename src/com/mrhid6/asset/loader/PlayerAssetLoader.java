package com.mrhid6.asset.loader;

import org.json.JSONObject;

import com.mrhid6.asset.AssetLoader;
import com.mrhid6.asset.PlayerAsset;
import com.mrhid6.log.Logger;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Loader;

public class PlayerAssetLoader{
	
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
			
			Logger.info("Asset Loaded '" + assetName+"'");
			
		}else{
			Logger.info("Asset '"+assetName+"' Exisits Skipped Load");
		}
	}
}