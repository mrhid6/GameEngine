package com.mrhid6.gameengine.obj.parser.mtl;

import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;


public class NsParser extends LineParser {

	float ns;
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) 
	{
		Material currentMaterial = wavefrontObject.getCurrentMaterial() ;
		currentMaterial.setShininess( ns );

	}

	@Override
	public void parse() 
	{
		try
		{
			ns = Float.parseFloat( words[1] );
		}
		catch(Exception e)
		{
			throw new RuntimeException("VertexParser Error");
		}
	}

}
