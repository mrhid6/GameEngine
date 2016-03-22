package com.mrhid6.utils;

import com.mrhid6.models.RawModel;
import com.mrhid6.models.TexturedModel;
import com.mrhid6.textures.ModelTexture;

public class ModelUtils {
	
	@Deprecated
	public static TexturedModel createTexturedModel(String modelName){
		
		Loader loader = Loader.getInstance();
		
		RawModel model = loader.loadObjAsset(modelName);
		ModelTexture texture = new ModelTexture(loader.loadTexture(modelName));
		
		return new TexturedModel(model, texture);
	}
	
}
