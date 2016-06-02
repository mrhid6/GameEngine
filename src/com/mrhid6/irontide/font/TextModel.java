package com.mrhid6.irontide.font;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.mrhid6.irontide.api.ICleanUpable;

public class TextModel implements ICleanUpable{
	
	private int vaoID;
	private int pos_vboID, tex_vboID;
	
	private FloatBuffer positionsBuff;
	private FloatBuffer textureCoordsBuff;
	
	private boolean initialzied = false;
	private int vertexCount;

	public void initialize(float[] positions, float[] textureCoords) {
		if(!initialzied){
			createVAO();
			
			createPositionsVBO(0, 2, positions);
			createTextCoordsVBO(1, 2, textureCoords);

			GL30.glBindVertexArray(0);
			initialzied = true;
		}
		
	}
	
	private void createVAO(){
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
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
	
	public int getVaoID() {
		return vaoID;
	}

	@Override
	public void cleanUp() {
		GL30.glDeleteVertexArrays(vaoID);
		GL15.glDeleteBuffers(pos_vboID);
		GL15.glDeleteBuffers(tex_vboID);
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}

}
