package com.mrhid6.gameengine.obj;


	
	
public class Face {

	public static int GL_QUADS =2;
	public static int GL_TRIANGLES =1;
	
	public int[] normIndices;
	public int[] texIndices;
	public int[] vertIndices;
	private Vertex[] normals ;
	private TextureCoordinate[] textures;
	private int type ;
	
	private Vertex[] vertices;

	public int[] getIndices() {
		return vertIndices;
	}
	
	public Vertex[] getNormals() {
		return normals;
	}
	
	public TextureCoordinate[] getTextures() {
		return textures;
	}	
	public int getType() {
		return type;
	}
	public Vertex[] getVertices() {
		return vertices;
	}
	public void setIndices(int[] indices) {
		this.vertIndices = indices;
	}
	public void setNormals(Vertex[] normals) {
		this.normals = normals;
	}
	public void setTextures(TextureCoordinate[] textures) {
		this.textures = textures;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}
}
