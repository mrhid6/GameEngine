package com.mrhid6.irontide.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import com.mrhid6.irontide.render.renderer.FontRenderer;
import com.mrhid6.irontide.settings.Constants;
import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6.irontide.utils.Loader;

public class TextMaster {

	public static FontType ARIAL;

	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();

	private static FontRenderer renderer;

	public static void init(){
		renderer = new FontRenderer();

		TextMaster.ARIAL = new FontType(Loader.getInstance().loadTexture("/textures/arial.png"), "/textures/arial.fnt");

		GUIText text = new GUIText(Constants.TITLE+" Alpha v"+GameSettings.CURRENTVERSION.displayVersion(), 24f, TextMaster.ARIAL, new Vector2f(GameSettings.WIDTH-180f, GameSettings.HEIGHT-50f), 0.3f, false);
		text.setColour(1, 0, 0);
	}

	public static void render(){
		renderer.render(texts);
	}

	public static void loadText(GUIText text){

		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);

		TextModel model = new TextModel();

		model.initialize(data.getVertexPositions(), data.getTextureCoords());
		model.setVertexCount(data.getVertexCount());
		text.setTextModel(model);

		List<GUIText> textBatch = texts.get(font);

		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}

		textBatch.add(text);
	}
	
	public static void updateText(GUIText text){
		removeText(text);
		loadText(text);
	}

	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		text.getModel().cleanUp();
		
		if(textBatch.isEmpty()){
			texts.remove(text.getFont());
		}
	}

	public static void cleanUp(){
		for(FontType font : texts.keySet()){
			List<GUIText> textBatch = texts.get(font);
			
			for(GUIText text : textBatch){
				text.getModel().cleanUp();
			}
		}
		renderer.cleanUp();
	}

}
