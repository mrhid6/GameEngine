package com.mrhid6.gameengine.obj;

import java.util.ArrayList;

public class Group {

	public int indexCount;
	public ArrayList<Integer> indices = new ArrayList<Integer>();
	public ArrayList<Vertex> normals = new ArrayList<Vertex>();
	public ArrayList<TextureCoordinate> texcoords = new ArrayList<TextureCoordinate>();

	public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Face> faces = new ArrayList<Face>();
	private Material material;
	private Vertex min = null;
	private String name;
	
	public Group(String name)
	{
		indexCount = 0;
		this.name = name;
	}
	
	public void addFace(Face face)
	{
		faces.add(face);
	}
	
	public ArrayList<Face> getFaces() {
		return faces;
	}
	
	public Material getMaterial() 
	{
		return material;
	}

	public Vertex getMin() {
		return min;
	}

	public String getName() {
		return name;
	}

	public void pack()
	{
		float minX = 0;
		float minY = 0;
		float minZ= 0;
		Face currentFace = null;
		Vertex currentVertex = null;
		for ( int i=0;i<faces.size();i++)
		{
			currentFace = faces.get(i);
			for(int j=0;j<currentFace.getVertices().length;j++)
			{
				currentVertex = currentFace.getVertices()[j];
				if (Math.abs(currentVertex.getX()) > minX )  
					minX = Math.abs(currentVertex.getX());
				if (Math.abs(currentVertex.getY()) > minY )  
					minY = Math.abs(currentVertex.getY());
				if (Math.abs(currentVertex.getZ()) > minZ )  
					minZ = Math.abs(currentVertex.getZ());
			}
		}
		
		min = new Vertex(minX,minY,minZ);
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}
}
