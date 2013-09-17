package com.mrhid6.gameengine.obj.parser.mtl;

import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;

public class MaterialParser extends LineParser {

	String materialName = "";
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) 
	{
		Material newMaterial = new Material(materialName);
		
		wavefrontObject.getMaterials().put(materialName,newMaterial);
		wavefrontObject.setCurrentMaterial(newMaterial);
	}

	@Override
	public void parse() {
		materialName = words[1];
	}

}
