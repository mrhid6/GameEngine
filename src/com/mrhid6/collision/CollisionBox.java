package com.mrhid6.collision;

import java.util.ArrayList;

import com.mrhid6.models.RawModel;
import com.mrhid6.utils.Loader;

public class CollisionBox {
	
	private ArrayList<Triangle> triangles;
	private float[] positions;
	private int[] indices;
	private RawModel model;
	
	public CollisionBox(ArrayList<Triangle> triangles) {
		this.triangles = triangles;
	}
	
	public CollisionBox(float[] positions, int[] indices) {
		this.positions = positions;
		this.indices = indices;
	}
	
	public CollisionBox() {
		this.triangles = new ArrayList<Triangle>();
	}
	
	public void createRawModel() {
		if (positions != null && indices != null) {
			model = Loader.getInstance().loadToVAO(positions, indices);
			positions = null;
			indices = null;
		}
	}
	
	public void setData(float[] positions, int[] indices) {
		this.positions = positions;
		this.indices = indices;
	}
	
	public void addTriangle(Triangle triangle) {
		if (triangle != null) {
			triangles.add(triangle);
		}
	}
	
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
	
	public RawModel getModel() {
		return model;
	}
}
