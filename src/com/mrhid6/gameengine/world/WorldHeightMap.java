package com.mrhid6.gameengine.world;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.lwjgl.opengl.GL11;

import com.mrhid6.gameengine.GuiText;
import com.mrhid6.gameengine.camera.CameraManager;
import com.mrhid6.gameengine.obj.Texture;
import com.mrhid6.gameengine.obj.TextureLoader;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.render.Render2D;
import com.mrhid6.gameengine.render.Render3D;
import com.mrhid6.gameengine.render.RenderManager;
import com.mrhid6.gameengine.render.RenderObject;
import com.mrhid6.gameengine.render.RenderPriority;
import com.mrhid6.gameengine.utils.Vector3f;



public class WorldHeightMap implements RenderObject{
	WavefrontObject temp;
	HeightMap map;
	private Vector3f[][] strips;
	private Vector3f[][][] lines;
	private Texture tex0;
	private Texture tex1;
	private Texture mask;
	private TerrainQuad terrain;

	public WorldHeightMap() throws Exception {
		map = new HeightMap();

		tex0 = TextureLoader.loadTexture("res/heightmap.png");
		
		terrain = new TerrainQuad("terrain", 65, 513, map.getHeightMap());
		
		RenderManager.addToManager(this);
	}

	@Render3D(priority = RenderPriority.HIGHEST)
	public void render(){
		GL11.glPushMatrix();{
			tex0.bind();
			map.render();
		}GL11.glPopMatrix();
	}

	@Render2D
	public void render2d(){
		GuiText.drawText("yaw: "+CameraManager.getCamera().yaw(), 3, 0, false, 0xffffff, 5);
		GuiText.drawText("pitch: "+CameraManager.getCamera().pitch(), 3, 20, false, 0xffffff, 5);
	}
}
