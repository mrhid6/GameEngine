package com.mrhid6.render.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.entities.Camera;
import com.mrhid6.log.Logger;
import com.mrhid6.models.RawModel;
import com.mrhid6.shaders.TerrainShader;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.textures.TerrianTexturePack;
import com.mrhid6.utils.Maths;

public class TerrainRenderer extends SubRenderer{
	
	private TerrainShader shader;
	
	public TerrainRenderer(Matrix4f projectionMatrix) {
		this.shader = new TerrainShader();
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(){
		
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(MasterRenderer.getInstance().getSkyColor());
		shader.loadLights(MasterRenderer.getInstance().getLights());
		shader.loadViewMatrix(Camera.getInstance());
		
		for(Terrain terrian: TerrainGrid.getInstance().getTerrians()){
			prepareTerrian(terrian);
			loadModelMatrix(terrian);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrian.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTextureModel();
		}
		shader.stop();
	}
	
	private void prepareTerrian(Terrain terrian){
		RawModel rawModel = terrian.getModel();
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrian);
		
		shader.loadShineVaribles(1, 0);
		
	}
	
	private void bindTextures(Terrain terrian){
		
		TerrianTexturePack texturePack = terrian.getTexturePack();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBaseTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getSplatTexture().getTextureID());
	}
	
	private void unbindTextureModel(){
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrian){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrian.getX(), 0, terrian.getZ()), 0, 0, 0, 1);
		
		shader.loadTransformationMatrix(transformationMatrix);
	}

	@Override
	public void cleanUp() {
		Logger.info("CleanUp Started");
		shader.cleanUp();
		Logger.info("CleanUp Finished");
	}
	
}
