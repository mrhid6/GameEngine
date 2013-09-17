package com.mrhid6.gameengine.obj.parser.mtl;

import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.Vertex;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;


public class KaParser extends LineParser {

	Vertex ka = null;
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material currentMaterial = wavefrontObject.getCurrentMaterial() ;
		currentMaterial.setKa(ka);

	}

	@Override
	public void parse() {
		ka = new Vertex();
		
		try
		{
			ka.setX(Float.parseFloat(words[1]));
			ka.setY(Float.parseFloat(words[2]));
			ka.setZ(Float.parseFloat(words[3]));
		}
		catch(Exception e)
		{
			throw new RuntimeException("VertexParser Error");
		}
	}

}
