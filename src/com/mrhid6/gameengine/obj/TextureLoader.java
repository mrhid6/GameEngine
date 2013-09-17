package com.mrhid6.gameengine.obj;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL11;


import de.matthiasmann.twl.utils.PNGDecoder;

public class TextureLoader {

	private static TextureLoader instance = null;
	private static HashMap<String, Integer>textureSheetInt = new HashMap<String, Integer>();
	private Hashtable<String,BufferedImage> bufferedImageCache = new Hashtable <String,BufferedImage>();

	private TextureLoader(){}

	public static Texture loadTexture(String path)
	{
		return loadTexture(path,0,0,0,0);
	}

	private  Texture[] loadAnimation(String path,int cols, int rows, int textWidth, int textHeight, int xOffSet, int yOffSet) 
	{
		Texture[] toReturntextures = new Texture[cols*rows];



		for (int i=0;i< rows ; i++)
			for (int j=0;j< cols ; j++)
			{
				toReturntextures[i*cols+j] = loadTexture(path,j*textWidth+xOffSet,i*textHeight+yOffSet,textWidth,textHeight);
			}

		return toReturntextures;
	}



	private static Texture loadTexture(String path,int xOffSet, int yOffSet, int textWidth, int textHeight) {

		Texture toReturn = null;

		BufferedImage buffImage;
		try{
			buffImage = ImageIO.read(TextureLoader.class.getResourceAsStream(path));

		}catch (Exception e){
			try{
				buffImage =  ImageIO.read(new File(path));

			}catch(Exception e2){
				System.err.println("Could not load path '"+path+"'");
				e.printStackTrace();
				e2.printStackTrace();
				return null;
			}
		}

		if (textWidth == 0)
			textWidth = buffImage.getWidth();
		if (textHeight == 0)
			textHeight = buffImage.getHeight();

		toReturn = new Texture(setupTextures(path),textWidth,textHeight);

		return toReturn;
	}

	Texture[] loadAnimation(String path,int cols, int rows, int textWidth, int textHeight) 
	{
		return loadAnimation(path,cols,rows,textWidth,textHeight,0,0);
	}

	public static TextureLoader instance()
	{
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}

	public static void resetTextureLoader(){
		textureSheetInt.clear();
	}
	public static int setupTextures(String path) {
		return setupTextures(path, false);
	}
	public static int setupTextures(String path, boolean forceLoad) {

		if(textureSheetInt.get(path)!=null && textureSheetInt.containsKey(path) && !forceLoad){
			int texture = textureSheetInt.get(path);
			return texture;
		}

		IntBuffer tmp = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(tmp);
		tmp.rewind();
		try {
			InputStream in = new FileInputStream(path);
			PNGDecoder decoder = new PNGDecoder(in);

			ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
			buf.flip();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmp.get(0));
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
					GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
					GL11.GL_NEAREST);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);

		} catch (java.io.FileNotFoundException ex) {
			System.out.println("Error " + path + " not found");
		} catch (java.io.IOException e) {
			System.out.println("Error decoding " + path);
		}
		tmp.rewind();

		textureSheetInt.put(path, tmp.get(0));
		return tmp.get(0);
	}
}
