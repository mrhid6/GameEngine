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

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mrhid6.gameengine.utils.Node;
import com.mrhid6.gameengine.utils.Vector2f;
import com.mrhid6.gameengine.utils.Vector3f;

public class TerrainPatch extends Node{


	private String name;
	private int size;
	private Vector3f stepScale;
	private int totalSize;
	private float offsetAmount;
	private Vector2f offset;
	private float[] heightMap;
	private float[][] heightMap2d;
	protected short quadrant = 1;
	private int dl;

	
	//FAILED GEOMIPMAPPING
	public TerrainPatch(String name, int size, Vector3f stepScale, float[] heightMap, Vector3f origin, int totalSize, Vector2f offset, float offsetAmount)
	{

		this.name = name;
		this.size = size;
		this.stepScale = stepScale;
		this.totalSize = totalSize;
		this.offsetAmount = offsetAmount;
		this.offset = offset;
		this.heightMap = heightMap;

		System.out.println(name);

		make2DArray();
		createDisplayList();
	}

	private void make2DArray(){
		heightMap2d = new float[size][size];
		for(int i=0; i<size;i++)
			for(int j=0;j<size;j++)
				heightMap2d[i][j] = heightMap[(j*size) + i];
	}

	@Override
	public String toString() {
		return "TerrainPatch [name=" + name + ", size=" + size + ", stepScale="
				+ stepScale + ", totalSize=" + totalSize + ", offsetAmount="
				+ offsetAmount + ", offset=" + offset + ", heightMap="
				+ Arrays.toString(heightMap) + ", quadrant=" + quadrant + "]";
	}



	public void createDisplayList(){

		dl = glGenLists(1);
		glNewList(dl, GL_COMPILE);
		for (int z = 0; z < heightMap2d.length - 1; z++) {
			glBegin(GL_TRIANGLE_STRIP);
			for (int x = 0; x < heightMap2d[z].length; x++) {

				GL11.glTexCoord2f(z/512.0f, x/512.0f);
				glVertex3f(x, heightMap2d[z][x], z);
				glVertex3f(x, heightMap2d[z + 1][x],z + 1);
			}
			glEnd();
		}
		glEndList();
	}

	public String getName() {
		return name;
	}

	public short getQuadrant()
	{
		return this.quadrant;
	}

	public void setQuadrant(short quadrant)
	{
		this.quadrant = quadrant;
	}

	public void render(){
		GL11.glPushMatrix();{
			GL11.glTranslatef(offset.x, 0, offset.y);
			GL11.glCallList(dl);
		}GL11.glPopMatrix();
	}
}
