package com.mrhid6.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.render.renderer.FontRenderer;
import com.mrhid6.settings.Constants;
import com.mrhid6.settings.GameSettings;
import com.mrhid6.utils.Loader;

public class TextMaster {
	
	
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	
	private static FontRenderer renderer;
	
	public static void init(){
		renderer = new FontRenderer();
		
		FontType font = new FontType(Loader.getInstance().loadTexture("/textures/arial.png"), "/textures/arial.fnt");
		GUIText text = new GUIText(Constants.TITLE+" Alpha v"+GameSettings.CURRENTVERSION.displayVersion(), 0.75f, font, new Vector2f(Constants.WIDTH-160f, Constants.HEIGHT-22f), 0.3f, false);
		text.setColour(1, 0, 0);
	}
	
	public static void render(){
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = Loader.getInstance().loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		
		List<GUIText> textBatch = texts.get(font);
		
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		
		textBatch.add(text);
	}
	
	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		
		if(textBatch.isEmpty()){
			texts.remove(text.getFont());
		}
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}
	
}
