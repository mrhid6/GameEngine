package com.mrhid6.irontide.render.renderer;

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

import com.mrhid6.irontide.entities.AnimatedEntity;
import com.mrhid6.irontide.entities.AnimatedPlayer;
import com.mrhid6.irontide.entities.AnimatedWorldObject;
import com.mrhid6.irontide.entities.Camera;
import com.mrhid6.irontide.entities.Entity;
import com.mrhid6.irontide.entities.Light;
import com.mrhid6.irontide.entities.WorldObject;
import com.mrhid6.irontide.font.TextMaster;
import com.mrhid6.irontide.models.TexturedModel;
import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6.irontide.shaders.StaticShader;
import com.mrhid6.irontide.textures.GuiTexture;
import com.mrhid6.irontide.utils.Maths;
import com.mrhid6.irontide.water.WaterFrameBuffers;
import com.mrhid6.irontide.water.WaterTile;

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
	private Map<String,List<WorldObject>> worldObjects = new HashMap<String, List<WorldObject>>();
	private Map<String,List<AnimatedWorldObject>> animatedWorldObjects = new HashMap<String, List<AnimatedWorldObject>>();
	private Map<String,List<AnimatedEntity>> animatedEntities = new HashMap<String, List<AnimatedEntity>>();
	private ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();

	private ArrayList<Light> lights = new ArrayList<Light>();

	private static MasterRenderer instance;

	private Vector4f reuseable4f = new Vector4f();
	private AnimatedWorldObjectRenderer animatedWorldObjRenderer;
	private AnimatedEntityRenderer animatedEntityRenderer;

	public MasterRenderer() {

		enableCulling();
		createProjectionMatrix();

		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		worldObjRenderer = new WorldObjectRenderer(shader, projectionMatrix);
		animatedWorldObjRenderer = new AnimatedWorldObjectRenderer(projectionMatrix);
		animatedEntityRenderer = new AnimatedEntityRenderer(projectionMatrix);
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
		
		animatedWorldObjRenderer.setClipPlane(clipPlane);
		animatedWorldObjRenderer.render(animatedWorldObjects);
		
		animatedEntityRenderer.setClipPlane(clipPlane);
		animatedEntityRenderer.render(animatedEntities);
		
		terrianRenderer.setClipPlane(clipPlane);
		terrianRenderer.render();

		skyboxRenderer.render();

	}

	public void render2d(){
		guiRenderer.render(guis);
		guis.clear();

		TextMaster.render();
	}

	public void renderWaterFBO(){

		Camera camera = Camera.getInstance();

		ArrayList<WaterTile> waters = WaterRenderer.getInstance().getWaters();

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		for(int i=0;i<waters.size();i++){

			WaterTile tile = waters.get(i);
			WaterFrameBuffers waterFBOs = WaterRenderer.getInstance().getWaterFBOs().get(i);

			if(GameSettings.USEWATERREFLECTIONS){
				waterFBOs.bindReflectionFrameBuffer();
				float distance = 2 * (camera.getPosition().y - tile.getPosition().getY());
				camera.getPosition().y -= distance;
				camera.invertPitch();
				reuseable4f.set(0, 1, 0, -tile.getPosition().getY()+1f);
				render3d(reuseable4f);
				camera.getPosition().y += distance;
				camera.invertPitch();
			}

			waterFBOs.bindRefractionFrameBuffer();
			reuseable4f.set(0, -1, 0, tile.getPosition().getY()+1f);
			render3d(reuseable4f);
			waterFBOs.unbindCurrentFrameBuffer();
		}
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}


	public Vector3f getSkyColor() {
		return skyColor;
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

	/*public void processPlayer(Player player){
		try{
			PlayerAsset playerAsset = player.getAsset();
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
	}*/

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
		if(object == null || object.getAsset() == null || object.getAsset().getModel() == null) return;

		float distance = Maths.distance(Camera.getInstance().getPosition(), object.getPosition());

		if(distance <= object.getRenderDistance()){

			//StaticWorldObjectAsset objectAsset = object.getAsset();
			//TexturedModel entityModel = new TexturedModel(objectAsset.getModel(), objectAsset.getTexture());

			List<WorldObject> batch = worldObjects.get(object.getName());

			if(batch != null){
				batch.add(object);
			}else{
				List<WorldObject> newBatch = new ArrayList<WorldObject>();
				newBatch.add(object);
				worldObjects.put(object.getName(), newBatch);
			}
		}
	}

	public void processAnimatedWorldObject(AnimatedWorldObject object){
		if(object == null || object.getAsset() == null || object.getAsset().getModel() == null) return;


		//StaticWorldObjectAsset objectAsset = object.getAsset();
		//TexturedModel entityModel = new TexturedModel(objectAsset.getModel(), objectAsset.getTexture());

		List<AnimatedWorldObject> batch = animatedWorldObjects.get(object.getName());

		if(batch != null){
			batch.add(object);
		}else{
			List<AnimatedWorldObject> newBatch = new ArrayList<AnimatedWorldObject>();
			newBatch.add(object);
			animatedWorldObjects.put(object.getName(), newBatch);
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

	public void processAnimatedEntity(AnimatedPlayer entity) {
		if(entity == null || entity.getAsset() == null || entity.getAsset().getModel() == null) return;


		//StaticWorldObjectAsset objectAsset = object.getAsset();
		//TexturedModel entityModel = new TexturedModel(objectAsset.getModel(), objectAsset.getTexture());

		List<AnimatedEntity> batch = animatedEntities.get(entity.getName());

		if(batch != null){
			batch.add(entity);
		}else{
			List<AnimatedEntity> newBatch = new ArrayList<AnimatedEntity>();
			newBatch.add(entity);
			animatedEntities.put(entity.getName(), newBatch);
		}
	}
}
