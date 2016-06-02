package com.mrhid6.irontide.utils.loaders;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	
	private static final int NO_INDEX = -1;
	private static final int MAX_BONE_INFLUENCE = 4;
	
	private Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private int index;
	private float length;
	private ArrayList<Integer> boneIDs;
	private ArrayList<Float> boneWeights;
	
	public Vertex(int index,Vector3f position){
		this.index = index;
		this.position = position;
		this.length = position.length();
		
		boneIDs = new ArrayList<Integer>();
		boneWeights = new ArrayList<Float>();
	}
	
	public int getIndex(){
		return index;
	}
	
	public float getLength(){
		return length;
	}
	
	public boolean isSet(){
		return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
	}
	
	public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	public void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	
	public void setNormalIndex(int normalIndex){
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		if (textureIndex == NO_INDEX) {
			textureIndex = 0;
		}
		return textureIndex;
	}

	public int getNormalIndex() {
		if (normalIndex == NO_INDEX) {
			normalIndex = 0;
		}
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	public void addInfluence(int boneID, float weight) {
		if (boneIDs.size() >= MAX_BONE_INFLUENCE) {
			boneIDs.add(boneID);
			boneWeights.add(weight);
			
			float ppp = Float.MAX_VALUE;
			int indexPPP = -1;
			for (int i = 0; i < boneWeights.size(); i++) {
				if (boneWeights.get(i) < ppp) {
					ppp = boneWeights.get(i);
					indexPPP = i;
				}
			}
			
			float removedWeight = boneWeights.remove(indexPPP);
			float distributedWeight = removedWeight / MAX_BONE_INFLUENCE;
			
			ArrayList<Float> newWeights = new ArrayList<Float>();
			for (int i = 0; i < boneWeights.size(); i++) {
				newWeights.add(boneWeights.get(i) + distributedWeight);
			}
			
			boneWeights.clear();
			for (int i = 0; i < MAX_BONE_INFLUENCE; i++) {
				boneWeights.add(newWeights.get(i));
			}
			
			boneIDs.remove(indexPPP);
		} else {
			boneIDs.add(boneID);
			boneWeights.add(weight);
		}
		if (duplicateVertex != null) {
			duplicateVertex.addInfluence(boneID, weight);
		}
	}
	
	public void fixInfluences() {
		int size = boneIDs.size();
		if (size < MAX_BONE_INFLUENCE) {
			for (int i = size; i < MAX_BONE_INFLUENCE; i++) {
				boneIDs.add(0);
				boneWeights.add(0f);
			}
		}
		if (duplicateVertex != null) {
			duplicateVertex.fixInfluences();
		}
	}
	
	public List<Integer> getBoneIDs() {
		return boneIDs;
	}
	
	public List<Float> getBoneWeights() {
		return boneWeights;
	}
}
