package com.mrhid6.gameengine.world;

import com.mrhid6.gameengine.utils.Node;
import com.mrhid6.gameengine.utils.Vector2f;
import com.mrhid6.gameengine.utils.Vector3f;

public class TerrainQuad extends Node{


	private Vector2f offset;
	private float offsetAmount;
	private int totalSize;
	private int size;
	private int patchSize;
	private Vector3f stepScale;
	private int quadrant;

	private String name;

	
	//FAILED GEOMIPMAPPING
	public TerrainQuad(String name, int patchSize, int totalSize, float[] heightMap){
		this(name, patchSize, totalSize, Vector3f.UNIT_XYZ, heightMap,totalSize,new Vector2f(),0.0f);

	}

	public String getName() {
		return name;
	}
	protected TerrainQuad(String name, int patchSize, int quadSize, Vector3f scale, float[] heightMap, int totalSize, Vector2f offset, float offsetAmount){
		this.name = name;
		this.offset = offset;
		this.offsetAmount = offsetAmount;
		this.totalSize = totalSize;
		this.size = quadSize;
		this.patchSize = patchSize;
		this.stepScale = scale;
		split(patchSize, heightMap);
	}
	
	protected void split(int blockSize, float[] heightMap)
	{
		if ((this.size >> 1) + 1 <= blockSize)
			createQuadPatch(heightMap);
		else
			createQuad(blockSize, heightMap);
	}

	protected void createQuadPatch(float[] heightMap)
	{
		int quarterSize = this.size >> 2;
		int halfSize = this.size >> 1;
		int split = this.size + 1 >> 1;

		this.offsetAmount += quarterSize;

		float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

		Vector3f origin1 = new Vector3f(-halfSize * this.stepScale.x, 0.0F, -halfSize * this.stepScale.z);

		Vector2f tempOffset1 = new Vector2f();
		tempOffset1.x = this.offset.x;
		tempOffset1.y = this.offset.y;
		tempOffset1.x += origin1.x;
		tempOffset1.y += origin1.z;

		TerrainPatch patch1 = new TerrainPatch(getName() + "Patch1", split, this.stepScale, heightBlock1, origin1, this.totalSize, tempOffset1, this.offsetAmount);

		patch1.setQuadrant((short)1);
		attachChild(patch1);

		float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1, split);

		Vector3f origin2 = new Vector3f(-halfSize * this.stepScale.x, 0.0F, 0.0F);

		Vector2f tempOffset2 = new Vector2f();
		tempOffset2.x = this.offset.x;
		tempOffset2.y = this.offset.y;
		tempOffset2.x += origin1.x;
		tempOffset2.y += quarterSize * this.stepScale.z;

		TerrainPatch patch2 = new TerrainPatch(getName() + "Patch2", split, this.stepScale, heightBlock2, origin2, this.totalSize, tempOffset2, this.offsetAmount);

		patch2.setQuadrant((short)2);
		attachChild(patch2);

		float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0, split);

		Vector3f origin3 = new Vector3f(0.0F, 0.0F, -halfSize * this.stepScale.z);

		Vector2f tempOffset3 = new Vector2f();
		tempOffset3.x = this.offset.x;
		tempOffset3.y = this.offset.y;
		tempOffset3.x += quarterSize * this.stepScale.x;
		tempOffset3.y += origin3.z;

		TerrainPatch patch3 = new TerrainPatch(getName() + "Patch3", split, this.stepScale, heightBlock3, origin3, this.totalSize, tempOffset3, this.offsetAmount);

		patch3.setQuadrant((short)3);
		attachChild(patch3);

		float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1, split - 1, split);

		Vector3f origin4 = new Vector3f(0.0F, 0.0F, 0.0F);

		Vector2f tempOffset4 = new Vector2f();
		tempOffset4.x = this.offset.x;
		tempOffset4.y = this.offset.y;
		tempOffset4.x += quarterSize * this.stepScale.x;
		tempOffset4.y += quarterSize * this.stepScale.z;

		TerrainPatch patch4 = new TerrainPatch(getName() + "Patch4", split, this.stepScale, heightBlock4, origin4, this.totalSize, tempOffset4, this.offsetAmount);

		patch4.setQuadrant((short)4);
		attachChild(patch4);
	}

	public float[] createHeightSubBlock(float[] heightMap, int x, int y, int side)
	{
		float[] rVal = new float[side * side];
		int bsize = (int)Math.sqrt(heightMap.length);
		int count = 0;
		for (int i = y; i < side + y; i++) {
			for (int j = x; j < side + x; j++) {
				if ((j < bsize) && (i < bsize))
					rVal[count] = heightMap[(j + i * bsize)];
				count++;
			}
		}
		return rVal;
	}

	private void createQuad(int blockSize, float[] heightMap){
			int quarterSize = this.size >> 2;

			int split = this.size + 1 >> 1;
			
			Vector2f tempOffset = new Vector2f();
			this.offsetAmount += quarterSize;

			float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

			Vector3f origin1 = new Vector3f(-quarterSize * this.stepScale.x, 0.0F, -quarterSize * this.stepScale.z);

			tempOffset.x = this.offset.x;
			tempOffset.y = this.offset.y;
			tempOffset.x += origin1.x;
			tempOffset.y += origin1.z;

			TerrainQuad quad1 = new TerrainQuad(getName() + "Quad1", blockSize, split, this.stepScale, heightBlock1, this.totalSize, tempOffset, this.offsetAmount);

			quad1.quadrant = 1;
			attachChild(quad1);

			float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1, split);

			Vector3f origin2 = new Vector3f(-quarterSize * this.stepScale.x, 0.0F, quarterSize * this.stepScale.z);

			tempOffset = new Vector2f();
			tempOffset.x = this.offset.x;
			tempOffset.y = this.offset.y;
			tempOffset.x += origin2.x;
			tempOffset.y += origin2.z;

			TerrainQuad quad2 = new TerrainQuad(getName() + "Quad2", blockSize, split, this.stepScale, heightBlock2, this.totalSize, tempOffset, this.offsetAmount);

			quad2.quadrant = 2;
			attachChild(quad2);

			float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0, split);

			Vector3f origin3 = new Vector3f(quarterSize * this.stepScale.x, 0.0F, -quarterSize * this.stepScale.z);

			tempOffset = new Vector2f();
			tempOffset.x = this.offset.x;
			tempOffset.y = this.offset.y;
			tempOffset.x += origin3.x;
			tempOffset.y += origin3.z;

			TerrainQuad quad3 = new TerrainQuad(getName() + "Quad3", blockSize, split, this.stepScale, heightBlock3, this.totalSize, tempOffset, this.offsetAmount);

			quad3.quadrant = 3;
			attachChild(quad3);

			float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1, split - 1, split);

			Vector3f origin4 = new Vector3f(quarterSize * this.stepScale.x, 0.0F, quarterSize * this.stepScale.z);

			tempOffset = new Vector2f();
			tempOffset.x = this.offset.x;
			tempOffset.y = this.offset.y;
			tempOffset.x += origin4.x;
			tempOffset.y += origin4.z;

			TerrainQuad quad4 = new TerrainQuad(getName() + "Quad4", blockSize, split, this.stepScale, heightBlock4, this.totalSize, tempOffset, this.offsetAmount);

			quad4.quadrant = 4;
			attachChild(quad4);
	}

	public void render(){
		for(Node child : getChildren()){

			if(child instanceof TerrainQuad){
				((TerrainQuad)child).render();
			}

			if(child instanceof TerrainPatch){
				((TerrainPatch)child).render();
				return;
			}
		}
	}

}
