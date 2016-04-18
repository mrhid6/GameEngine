package com.mrhid6.render.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.asset.AssetLoader;
import com.mrhid6.asset.StaticWorldObjectAsset;
import com.mrhid6.entities.Camera;
import com.mrhid6.entities.WorldObject;
import com.mrhid6.models.RawModel;
import com.mrhid6.shaders.StaticShader;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Maths;

public class WorldObjectRenderer {
	
	private StaticShader shader;
	
	Matrix4f resueableMatrix4f = new Matrix4f();
	
	public WorldObjectRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	/**
	 * Prepares the screen for rendering
	 */
	
	public void render(Map<String, List<WorldObject>> worldObjects){
		for(String assetName : worldObjects.keySet()){
			
			StaticWorldObjectAsset asset = (StaticWorldObjectAsset)AssetLoader.getAsset(assetName);
			
			prepareTexturedModel(asset.getModel(), asset.getTexture());
			
			List<WorldObject> batch = worldObjects.get(assetName);
			for(WorldObject entity: batch){
				
				float distance = Maths.distance(Camera.getInstance().getPosition(), entity.getPosition());
				
				if(distance <= entity.getRenderDistance()){
					prepareInstance(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, asset.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
			}
			unbindTextureModel();
		}
	}
	
	private void prepareTexturedModel(RawModel rawModel, ModelTexture texture){
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		shader.loadNumberOfRows(texture.getNumberOfRows());
		
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLoading(texture.isUseFakeLighting());
		shader.loadShineVaribles(texture.getShineDamper(), texture.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private void unbindTextureModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(WorldObject worldObj){
		Matrix4f transformationMatrix = Maths.setTransformationMatrix(resueableMatrix4f,worldObj.getPosition(), 
				worldObj.getRotX(), worldObj.getRotY(), worldObj.getRotZ(), worldObj.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(worldObj.getTextureXOffset(), worldObj.getTextureYOffset());
	}
	
}
