package com.mrhid6.render.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.entities.Camera;
import com.mrhid6.entities.WorldObject;
import com.mrhid6.models.RawModel;
import com.mrhid6.models.TexturedModel;
import com.mrhid6.shaders.StaticShader;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Maths;

public class WorldObjectRenderer {
	
	private StaticShader shader;
	
	public WorldObjectRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	/**
	 * Prepares the screen for rendering
	 */
	
	public void render(Map<TexturedModel, List<WorldObject>> worldObjs){
		for(TexturedModel model : worldObjs.keySet()){
			prepareTexturedModel(model);
			List<WorldObject> batch = worldObjs.get(model);
			
			for(WorldObject entity: batch){
				
				float distance = Maths.distance(Camera.getInstance().getPosition(), entity.getPosition());
				
				if(distance <= entity.getRenderDistance()){
					prepareInstance(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
			}
			
			unbindTextureModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model){
		RawModel rawModel = model.getRawModel();
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		
		shader.loadNumberOfRows(texture.getNumberOfRows());
		
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLoading(texture.isUseFakeLighting());
		shader.loadShineVaribles(texture.getShineDamper(), texture.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTextureModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(WorldObject worldObj){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(worldObj.getPosition(), 
				worldObj.getRotX(), worldObj.getRotY(), worldObj.getRotZ(), worldObj.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(worldObj.getTextureXOffset(), worldObj.getTextureYOffset());
	}
	
}
