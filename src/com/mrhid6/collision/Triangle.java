package com.mrhid6.collision;

import org.lwjgl.util.vector.Vector3f;

public class Triangle {

	private Vector3f[] vertices;
	private Vector3f[] eSpaceVertices;
	
	public Triangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
		vertices = new Vector3f[3];
		vertices[0] = vertex1;
		vertices[1] = vertex2;
		vertices[2] = vertex3;
	}
	
	public Triangle(Triangle t) {
		Vector3f[] verts = t.getVertices();
		vertices = new Vector3f[3];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vector3f(verts[i].x, verts[i].y, verts[i].z);
		}
	}
	
	public Vector3f[] getVertices() {
		return vertices;
	}
	
	public void setESpaceVertices(Vector3f[] verts) {
		eSpaceVertices = verts;
	}
	
	public Vector3f[] getESpaceVertices() {
		return eSpaceVertices;
	}
	
	public String toString() {
		return vertices[0] + ", " + vertices[1] + ", " + vertices[2];
	}
}
