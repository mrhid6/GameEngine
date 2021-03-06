package com.mrhid6.irontide.render.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.irontide.models.RawModel;
import com.mrhid6.irontide.shaders.GuiShader;
import com.mrhid6.irontide.textures.GuiTexture;
import com.mrhid6.irontide.utils.Loader;
import com.mrhid6.irontide.utils.Maths;

public class GuiRenderer {
	
	private final RawModel quad;
	private GuiShader shader;
	
	public GuiRenderer() {
		
		Loader loader = Loader.getInstance();
		float[] positions = {0, 0, 0, -1, 1, 0, 1, -1};
		
		quad = loader.loadToVAO(positions, 2);
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis){
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(GuiTexture gui : guis){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale() );
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}
	
}
