package com.mrhid6.models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.mrhid6.api.ICleanUpable;

public class TerrainModel implements ICleanUpable{
	
	
	private int vaoID;
	private int pos_vboID, tex_vboID, nor_vboID;
	
	private FloatBuffer positionsBuff;
	private FloatBuffer textureCoordsBuff;
	private FloatBuffer normalsBuff;
	
	private int ind_vboID;
	private IntBuffer indicesBuff;
	
	private boolean initialzied = false;
	private int vertexCount;
	
	public TerrainModel(){
	}
	
	public void initialize(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		
		if(!initialzied){
			
			createVAO();
			
			createIndicesVBO(indices);
			createPositionsVBO(0, 3, positions);
			createTextCoordsVBO(1, 2, textureCoords);
			createNormalsVBO(2, 3, normals);

			GL30.glBindVertexArray(0);
			
			vertexCount = indices.length;
			
			initialzied = true;
		}
	}
	
	private void createVAO(){
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
	}
	
	public void updateTerrainModel(float[] positions, float[] textureCoords, float[] normals, int[] indices){
		if(initialzied){
			updateFloatBuffer(positionsBuff, positions);
			updateFloatBuffer(textureCoordsBuff, textureCoords);
			updateFloatBuffer(normalsBuff, normals);
			updateIntBuffer(indicesBuff, indices);
			
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ind_vboID);
			GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indicesBuff); 
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pos_vboID);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positionsBuff); 
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tex_vboID);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, textureCoordsBuff);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nor_vboID);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, normalsBuff);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}
	
	private void updateFloatBuffer(FloatBuffer buffer, float[] data){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
	}
	
	private void updateIntBuffer(IntBuffer buffer, int[] data){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
	}
	
	private void createIndicesVBO(int[] indices){
		ind_vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ind_vboID);
		
		indicesBuff = BufferUtils.createIntBuffer(indices.length);
		indicesBuff.put(indices);
		indicesBuff.flip();

		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuff, GL15.GL_DYNAMIC_DRAW);
	}
	
	private void createPositionsVBO(int attributeNumber, int coordinateSize, float[] data){
		pos_vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pos_vboID);

		positionsBuff = BufferUtils.createFloatBuffer(data.length);
		positionsBuff.put(data);
		positionsBuff.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionsBuff, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void createTextCoordsVBO(int attributeNumber, int coordinateSize, float[] data){
		tex_vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tex_vboID);

		textureCoordsBuff = BufferUtils.createFloatBuffer(data.length);
		textureCoordsBuff.put(data);
		textureCoordsBuff.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoordsBuff, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void createNormalsVBO(int attributeNumber, int coordinateSize, float[] data){
		nor_vboID = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nor_vboID);

		normalsBuff = BufferUtils.createFloatBuffer(data.length);
		normalsBuff.put(data);
		normalsBuff.flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalsBuff, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public void cleanUp() {
		GL30.glDeleteVertexArrays(vaoID);
		GL15.glDeleteBuffers(pos_vboID);
		GL15.glDeleteBuffers(tex_vboID);
		GL15.glDeleteBuffers(nor_vboID);
		GL15.glDeleteBuffers(ind_vboID);
	}

	
}