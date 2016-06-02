package com.mrhid6.irontide.render;

import java.util.ArrayList;

import com.mrhid6.irontide.collision.CollisionBox;
import com.mrhid6.irontide.render.armature.Animation;
import com.mrhid6.irontide.render.armature.Armature;

public class ModelData {

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] boneIDs;
	private float[] boneWeights;
	private int[] indices;
	private float furthestPoint;
	
	private ArrayList<CollisionBox> collisionBoxes;
	
	private Armature armature;
	private ArrayList<Animation> animations;

	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float furthestPoint) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.furthestPoint = furthestPoint;
	}
	
	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float furthestPoint, ArrayList<CollisionBox> collisionBoxes) {
		this(vertices, textureCoords, normals, indices, furthestPoint);
		this.collisionBoxes = collisionBoxes;
	}
	
	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float[] boneIDs, float[] boneWeights,
			float furthestPoint, Armature armature, ArrayList<Animation> animations) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.boneIDs = boneIDs;
		this.boneWeights = boneWeights;
		this.furthestPoint = furthestPoint;
		this.armature = armature;
		this.animations = animations;
	}
	
	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float[] boneIDs, float[] boneWeights,
			float furthestPoint, Armature armature, ArrayList<Animation> animations, ArrayList<CollisionBox> collisionBoxes) {
		this(vertices, textureCoords, normals, indices, boneIDs, boneWeights, furthestPoint, armature, animations);
		this.collisionBoxes = collisionBoxes;
	}
	
	public Armature createArmature() {
		Armature a = new Armature(this.armature);
		a.setArmatureToBones();
		a.toString();
		return a;
	}
	
	public ArrayList<Animation> createAnimations(Armature armature) {
		Armature a = armature;
		ArrayList<Animation> animations = new ArrayList<Animation>();
		for (int i = 0; i < this.animations.size(); i++) {
			animations.add(new Animation(this.animations.get(i), a));
		}
		return animations;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public float getFurthestPoint() {
		return furthestPoint;
	}
	
	public float[] getBoneIDs() {
		return boneIDs;
	}
	
	public float[] getBoneWeights() {
		return boneWeights;
	}
	
	public ArrayList<CollisionBox> getCollisionBoxes() {
		return collisionBoxes;
	}
	
	public Armature getArmature() {
		return armature;
	}
	
	public ArrayList<Animation> getAnimations() {
		return animations;
	}
}
