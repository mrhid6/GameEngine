package com.mrhid6.render.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.asset.AnimatedAsset;
import com.mrhid6.asset.AssetLoader;
import com.mrhid6.entities.AnimatedWorldObject;
import com.mrhid6.entities.Camera;
import com.mrhid6.models.AnimatedModel;
import com.mrhid6.models.RawModel;
import com.mrhid6.shaders.EntityShader;
import com.mrhid6.textures.ModelTexture;
import com.mrhid6.utils.Maths;

public class AnimatedWorldObjectRenderer extends SubRenderer{
	
	private EntityShader shader;
	
	Matrix4f resueableMatrix4f = new Matrix4f();
	
	public static final Matrix4f fixAxesMatrix = new Matrix4f();
	static {
		fixAxesMatrix.setIdentity();
		fixAxesMatrix.rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
		fixAxesMatrix.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1));
	}
	
	public AnimatedWorldObjectRenderer(Matrix4f projectionMatrix) {
		shader = new EntityShader();
		shader.start();
		shader.loadFixPositionMatrix(fixAxesMatrix);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	/**
	 * Prepares the screen for rendering
	 */
	
	public void render(Map<String, List<AnimatedWorldObject>> worldObjects){
		
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(MasterRenderer.getInstance().getSkyColor());
		shader.loadLights(MasterRenderer.getInstance().getLights());
		shader.loadViewMatrix(Camera.getInstance());
		
		for(String assetName : worldObjects.keySet()){
			
			AnimatedAsset asset = (AnimatedAsset)AssetLoader.getAsset(assetName);
			prepareAnimatedModel(asset.getModel());
			
			List<AnimatedWorldObject> batch = worldObjects.get(assetName);
			for(AnimatedWorldObject entity: batch){

				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getAsset().getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTextureModel();
		}
	}
	
	private void prepareAnimatedModel(AnimatedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		shader.loadFakeLightingVariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		shader.loadIsAnimated(true);
		shader.loadBindShapeMatrix(model.getBindShapeMatrix());
		
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTextureModel(){
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(AnimatedWorldObject worldObj){
	
		Matrix4f transformationMatrix = Maths.setTransformationMatrix(resueableMatrix4f,worldObj.getPosition(), 
				worldObj.getRotX(), worldObj.getRotY(), worldObj.getRotZ(), worldObj.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadBoneMatrices(worldObj.getAsset().getArmature().getBoneTransforms());
	}
	
	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
}
