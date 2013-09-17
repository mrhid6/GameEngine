package com.mrhid6.gameengine.world;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class HeightMap {

	private float[][] data;
	private int heightmapDisplayList;

	public HeightMap() throws Exception
	{
		try {
			// Load the heightmap-image from its resource file
			BufferedImage img = ImageIO.read(new File("res/heightmap.bmp"));
			// Initialise the data array, which holds the heights of the heightmap-vertices, with the correct dimensions
			data = new float[img.getWidth()][img.getHeight()];
			// Lazily initialise the convenience class for extracting the separate red, green, blue, or alpha channels
			// an int in the default RGB color model and default sRGB colourspace.
			Color colour;
			// Iterate over the pixels in the image on the x-axis
			for (int z = 0; z < data.length; z++) {
				// Iterate over the pixels in the image on the y-axis
				for (int x = 0; x < data[z].length; x++) {
					// Retrieve the colour at the current x-location and y-location in the image
					colour = new Color(img.getRGB(z, x));
					// Store the value of the red channel as the height of a heightmap-vertex in 'data'. The choice for
					// the red channel is arbitrary, since the heightmap-image itself only has white, gray, and black.
					data[z][x] = colour.getRed();
				}
			}
			
			data = smooth(data,img.getWidth(),img.getHeight(),1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		heightmapDisplayList = glGenLists(1);
		// TODO: Add alternative VBO rendering for pseudo-compatibility with version 3 and higher.
		glNewList(heightmapDisplayList, GL_COMPILE);
		// Scale back the display list so that its proportions are acceptable.
		glScalef(0.2f, 0.06f, 0.2f);
		// Iterate over the 'strips' of heightmap data.
		for (int z = 0; z < data.length - 1; z++) {
			// Render a triangle strip for each 'strip'.
			glBegin(GL_TRIANGLE_STRIP);
			for (int x = 0; x < data[z].length; x++) {

				GL11.glTexCoord2f(z/512.0f, x/512.0f);
				// Take a vertex from the current strip
				glVertex3f(x, data[z][x], z);
				// Take a vertex from the next strip
				glVertex3f(x, data[z + 1][x], z + 1);
			}
			glEnd();
		}
		glEndList();
	}

	public float meanNeighbour(float [][] input, int w, int h, int x, int y) {

		float sum = 0;
		int number = 0;
		for(int j=0;j<3;++j){
			for(int i=0;i<3;++i){
				if( (x-1+i)>=0 && (y-1+j)>=0 && (x-1+i)<w && (y-1+j)<h ){
					sum = sum + input[x-1+i][y-1+j];
					++number;
				}
			}
		}
		if(number==0) return 0;
		return (sum/number);
	}

	public void render(){
		GL11.glCallList(heightmapDisplayList);
	}

	public float [][] smooth(float [][] input,
			int width, int height, int iterations){
		float [][] temporary = new float [width][height];
		float [][] outputArrays = new float [width][height];
		temporary = (float [][]) input.clone();
		for (int its=0;its<iterations;++its){
			for(int j=0;j<height;++j){
				for(int i=0;i<width;++i){
					outputArrays[i][j] = meanNeighbour(temporary,
							width,height,i,j);
				}
			}
			for(int j=0;j<height;++j){
				for(int i=0;i<width;++i){
					temporary[i][j]=outputArrays[i][j];
				}
			}
		}
		return outputArrays;
	}

}