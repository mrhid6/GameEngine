package com.mrhid6.gameengine;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class GuiText {
	
	private static UnicodeFont[] fonts = new UnicodeFont[10];
	
	public static void drawText(String text, int x,int y, boolean shadow, int color, int size){
		int i = size-1;
		if(i<0 || i>fonts.length-1)
			return;
		
		if(shadow){
			fonts[i].drawString(x+1, y+1, text, Color.black);
		}
		fonts[i].drawString(x, y, text, new Color(color));
	}
	
	public static int getStringHeight(String text, int size){
		
		int i = size-1;
		
		if(i<0 || i>fonts.length-1)
			return 0;
		
		return fonts[i].getHeight(text);
	}
	
	public static int getStringWidth(String text, int size){

		int i = size+1;

		if(i<0 || i>fonts.length-1)
			return 0;
		
		return fonts[i].getWidth(text);
	}
	
	public static void init(){
		for(int i=0;i<fonts.length;i++){
			Font awtFont = new Font("Arial", Font.BOLD, 10+i);
			fonts[i] = new UnicodeFont(awtFont);
			fonts[i].getEffects().add(new ColorEffect(java.awt.Color.white));
			fonts[i].addAsciiGlyphs();
		    try {
		    	fonts[i].loadGlyphs();
		    } catch (SlickException ex) {}
		}
	}
}
