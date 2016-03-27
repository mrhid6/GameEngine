package com.mrhid6.render.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.entities.Camera;
import com.mrhid6.log.Logger;
import com.mrhid6.models.RawModel;
import com.mrhid6.shaders.SkyboxShader;
import com.mrhid6.utils.Loader;
import com.mrhid6.world.areas.WorldArea;

public class SkyboxRenderer {

	private static final float SIZE = 500f;

	private static final float[] VERTICES = {        
			-SIZE,  SIZE, -SIZE,
			-SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,

			-SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,

			-SIZE, -SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE, -SIZE,  SIZE,
			-SIZE, -SIZE,  SIZE,

			-SIZE,  SIZE, -SIZE,
			SIZE,  SIZE, -SIZE,
			SIZE,  SIZE,  SIZE,
			SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE, -SIZE,

			-SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			SIZE, -SIZE,  SIZE
	};

	private RawModel cube;
	private int texture;
	private SkyboxShader shader;

	public SkyboxRenderer( Matrix4f projectionMatrix) {

		Loader loader = Loader.getInstance();

		cube = loader.loadToVAO(VERTICES, 3);

		String[] TEXTURE_FILES_DAY = new String[6];
		String[] TEXTURE_FILES_NIGHT = new String[6];

		for(int i=0;i<6;i++){
			TEXTURE_FILES_DAY[i] = WorldArea.getAreaURL(1)+"/textures/skybox/day/"+(i+1)+".png";
			TEXTURE_FILES_NIGHT[i] = WorldArea.getAreaURL(1)+"/textures/skybox/night/"+(i+1)+".png";
		}

		texture = loader.loadCubeMap(TEXTURE_FILES_DAY);
		//nightTexture = loader.loadCubeMap(TEXTURE_FILES_NIGHT);

		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(){
		shader.start();
		shader.loadViewMatrix(Camera.getInstance());
		shader.loadFogColour(MasterRenderer.getInstance().getSkyColor());
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();

	}

	private void bindTextures(){
		int texture1 = texture;
		int texture2 = texture;
		float blendFactor = 0;

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}

	public void cleanUp(){
		Logger.info("CleanUp Started");
		shader.cleanUp();
		Logger.info("CleanUp Finished");
	}
}
