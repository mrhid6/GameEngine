package com.mrhid6.gameengine.obj.parser.mtl;

import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.Vertex;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;


public class KsParser extends LineParser {

	Vertex ks = null;
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material currentMaterial = wavefrontObject.getCurrentMaterial() ;
		currentMaterial.setKs(ks);

	}

	@Override
	public void parse() {
		ks = new Vertex();
		
		try
		{
			ks.setX(Float.parseFloat(words[1]));
			ks.setY(Float.parseFloat(words[2]));
			ks.setZ(Float.parseFloat(words[3]));
		}
		catch(Exception e)
		{
			throw new RuntimeException("VertexParser Error");
		}
	}

}
