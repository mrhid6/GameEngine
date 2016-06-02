package com.mrhid6.irontide.collision;

import org.lwjgl.util.vector.Vector3f;

public class Plane {

	private float[] equation;
	private Vector3f origin;
	private Vector3f normal;
	
	public Plane(Vector3f origin, Vector3f normal) {
		this.normal = normal;
		this.origin = origin;
		equation = new float[4];
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x * origin.x + normal.y * origin.y + normal.z * origin.z);
	}
	
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3) {
		normal = Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p3, p1, null), null);
		normal.normalise();
		origin = p1;
		equation = new float[4];
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x * origin.x + normal.y * origin.y + normal.z * origin.z);
	}
	
	public Plane(Triangle triangle) {
		this(triangle.getVertices()[0], triangle.getVertices()[1], triangle.getVertices()[2]);
	}
	
	public boolean isFrontFacingTo(Vector3f direction) {
		double dot = Vector3f.dot(normal, direction);
		return dot <= 0;
	}
	
	public double signedDistanceTo(Vector3f p) {
		return Vector3f.dot(p, normal) + equation[3];
	}
	
	public Vector3f getorigin() {
		return origin;
	}
	
	public Vector3f getNormal() {
		return normal;
	}
}
