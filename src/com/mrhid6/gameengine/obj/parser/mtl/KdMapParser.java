package com.mrhid6.gameengine.obj.parser.mtl;


import com.mrhid6.gameengine.obj.Material;
import com.mrhid6.gameengine.obj.Texture;
import com.mrhid6.gameengine.obj.TextureLoader;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;

public class KdMapParser extends LineParser {

	private WavefrontObject object = null;
	private String texName;
	private Texture texture = null;
	
	public KdMapParser(WavefrontObject object)
	{
		this.object = object;
	}
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		
		if (texture != null)
		{
			Material currentMaterial = wavefrontObject.getCurrentMaterial() ;
			currentMaterial.texName = texName;
			currentMaterial.setTexture(texture);
		}
	}

	@Override
	public void parse() {
		String textureFileName = words[words.length-1];
		texName = textureFileName;
		String pathToTextureBinary = object.getContextfolder() +  textureFileName;
		texture = TextureLoader.instance().loadTexture(pathToTextureBinary);
	}

}
