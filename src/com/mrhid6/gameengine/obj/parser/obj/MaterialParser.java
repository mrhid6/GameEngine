package com.mrhid6.gameengine.obj.parser.obj;

import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;

public class MaterialParser extends LineParser 
{
		String materialName = "";

		@Override
		public void incoporateResults(WavefrontObject wavefrontObject) {
			Material newMaterial = wavefrontObject.getMaterials().get(materialName);
			wavefrontObject.getCurrentGroup().setMaterial(newMaterial);
			
		}

		@Override
		public void parse() {
			materialName = words[1];
		}

	

}
