package com.mrhid6.render.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mrhid6.asset.PlayerAsset;
import com.mrhid6.asset.StaticWorldObjectAsset;
import com.mrhid6.entities.Camera;
import com.mrhid6.entities.Entity;
import com.mrhid6.entities.Light;
import com.mrhid6.entities.Player;
import com.mrhid6.entities.WorldObject;
import com.mrhid6.models.TexturedModel;
import com.mrhid6.shaders.StaticShader;
import com.mrhid6.textures.GuiTexture;
import com.mrhid6.water.WaterFrameBuffers;
import com.mrhid6.water.WaterTile;

public class MasterRenderer {

	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	
	private EntityRenderer entityRenderer;
	private WorldObjectRenderer worldObjRenderer;
	private GuiRenderer guiRenderer;
	private TerrainRenderer terrianRenderer;
	private SkyboxRenderer skyboxRenderer;

	
	public Vector3f skyColor = new Vector3f(0.5444f, 0.62f, 0.69f);
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel,List<WorldObject>> worldObjects = new HashMap<TexturedModel, List<WorldObject>>();
	private ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private ArrayList<Light> lights = new ArrayList<Light>();
	
	private static MasterRenderer instance;
	
	public MasterRenderer() {

		enableCulling();
		createProjectionMatrix();
		
		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		worldObjRenderer = new WorldObjectRenderer(shader, projectionMatrix);
		terrianRenderer = new TerrainRenderer(projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
		guiRenderer = new GuiRenderer();
		
		instance = this;
	}
	
	public static MasterRenderer getInstance() {
		return instance;
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(Vector4f clipPlane){
		
		renderWaterFBO();
		
		render3d(clipPlane);
		render2d();
		
		entities.clear();
		worldObjects.clear();
		
	}
	
	public void render3d(Vector4f clipPlane){
		prepare();
		
		Camera camera = Camera.getInstance();
		
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(skyColor);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		worldObjRenderer.render(worldObjects);
		shader.stop();
		
		
		terrianRenderer.setClipPlane(clipPlane);
		terrianRenderer.render();
		
		skyboxRenderer.render();
		
	}
	
	public Vector3f getSkyColor() {
		return skyColor;
	}
	
	public void render2d(){
		guiRenderer.render(guis);
		
		guis.clear();
	}
	
	public void renderWaterFBO(){
		
		Camera camera = Camera.getInstance();
		WaterFrameBuffers waterFBOs = WaterRenderer.getInstance().getWaterFBOs();
		ArrayList<WaterTile> waters = WaterRenderer.getInstance().getWaters();
		
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		waterFBOs.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - waters.get(0).getPosition().getY());
		camera.getPosition().y -= distance;
		camera.invertPitch();
		render3d(new Vector4f(0, 1, 0, -waters.get(0).getPosition().getY()+1f));
		camera.getPosition().y += distance;
		camera.invertPitch();
		
		
		waterFBOs.bindRefractionFrameBuffer();
		render3d(new Vector4f(0, -1, 0, waters.get(0).getPosition().getY()+1f));
		
		waterFBOs.unbindCurrentFrameBuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}
	
	public void processGui(GuiTexture gui){
		guis.add(gui);
	}
	
	public void addLight(Light light){
		lights.add(light);
	}
	
	public ArrayList<Light> getLights() {
		return lights;
	}
	
	public void processPlayer(Player player){
		try{
		PlayerAsset playerAsset = player.getPlayerAsset();
		TexturedModel entityModel = new TexturedModel(playerAsset.getModel(), playerAsset.getTexture());
		List<Entity> batch = entities.get(entityModel);
		
		if(batch != null){
			batch.add(player);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(player);
			entities.put(entityModel, newBatch);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		
		if(batch != null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}*/
	
	public void processWorldObject(WorldObject object){
		StaticWorldObjectAsset objectAsset = object.getAsset();
		TexturedModel entityModel = new TexturedModel(objectAsset.getModel(), objectAsset.getTexture());
		List<WorldObject> batch = worldObjects.get(entityModel);
		
		if(batch != null){
			batch.add(object);
		}else{
			List<WorldObject> newBatch = new ArrayList<WorldObject>();
			newBatch.add(object);
			worldObjects.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp(){
		shader.cleanUp();
		skyboxRenderer.cleanUp();
		terrianRenderer.cleanUp();
		guiRenderer.cleanUp();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 1);
	}
	
	
	/**
	 * Creates Projection Matrix
	 * 
	 */
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
