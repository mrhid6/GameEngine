package com.mrhid6.render.renderer;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.entities.Camera;
import com.mrhid6.entities.Light;
import com.mrhid6.log.Logger;
import com.mrhid6.models.RawModel;
import com.mrhid6.render.DisplayManager;
import com.mrhid6.shaders.WaterShader;
import com.mrhid6.utils.Loader;
import com.mrhid6.utils.Maths;
import com.mrhid6.water.WaterFrameBuffers;
import com.mrhid6.water.WaterTile;

public class WaterRenderer {
	
	private static final float WAVE_SPEED = 0.03f;
	
	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers waterFBOs;
	private int dudvTexture;
	private int normalMap;
	
	private float moveFactor = 0;
	
	private ArrayList<WaterTile> waters = new ArrayList<WaterTile>();
	
	private static WaterRenderer instance;
	
	public WaterRenderer(Matrix4f projectionMatrix) {
		this.shader = new WaterShader();;
		this.waterFBOs = new WaterFrameBuffers();
		dudvTexture = Loader.getInstance().loadTexture("res/textures/waterDUDV.png");
		normalMap = Loader.getInstance().loadTexture("res/textures/waterNormal.png");
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
		setUpVAO();
		
		instance = this;
	}

	public void render(Light sun) {
		
		prepareRender(sun);
		for (WaterTile tile : waters) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(tile.getPosition(),tile.getScale());
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Light sun){
		Camera camera = Camera.getInstance();
		shader.start();
		shader.loadViewMatrix(camera);
		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSecond();
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLight(sun);
		
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFBOs.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFBOs.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFBOs.getRefractionDepthTexture());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO() {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = Loader.getInstance().loadToVAO(vertices, 2);
	}
	
	public void perpare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public static WaterRenderer getInstance() {
		return instance;
	}
	
	public WaterFrameBuffers getWaterFBOs() {
		return waterFBOs;
	}

	public void cleanUp() {
		Logger.info("CleanUp Started");
		shader.cleanUp();
		waterFBOs.cleanUp();
		Logger.info("CleanUp Finished");
	}
	
	public void processWater(WaterTile water){
		waters.add(water);
	}
	
	public ArrayList<WaterTile> getWaters() {
		return waters;
	}

}
