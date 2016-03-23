package com.mrhid6.engine;

import java.util.ArrayList;
import java.util.Random;

import javax.naming.directory.InvalidAttributesException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mrhid6.asset.loader.StaticWorldObjectAssetLoader;
import com.mrhid6.entities.Camera;
import com.mrhid6.entities.Light;
import com.mrhid6.entities.Player;
import com.mrhid6.entities.WorldObject;
import com.mrhid6.guis.GuiManager;
import com.mrhid6.log.Logger;
import com.mrhid6.mousetools.MouseToolManager;
import com.mrhid6.render.DisplayManager;
import com.mrhid6.render.renderer.MasterRenderer;
import com.mrhid6.render.renderer.WaterRenderer;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Loader;
import com.mrhid6.utils.MousePicker;
import com.mrhid6.water.WaterTile;
import com.mrhid6.world.areas.GoblinVillage;
import com.mrhid6io.utils.Input;

public class GameEngine {


	private static GameEngine instance;

	private Camera camera;

	private MasterRenderer renderer;
	private TerrainGrid terriangrid;
	private Player player;
	private ArrayList<WorldObject> worldObjs = new ArrayList<WorldObject>();
	private MousePicker picker;
	private GuiManager guiManager;

	private WaterRenderer waterRenderer;

	private MouseToolManager mouseManager;

	public GameEngine(){
		Logger.info("Initialized");
		try{

			createDisplay();
			renderer = new MasterRenderer();
			terriangrid = new TerrainGrid();
			mouseManager = new MouseToolManager();
			waterRenderer = new WaterRenderer(renderer.getProjectionMatrix());

			loadAssets();

			createPlayer();
			createCamera();

			generateWorldObjects();


		}catch(Exception e){
			e.printStackTrace();
		}

		picker = new MousePicker(camera, renderer.getProjectionMatrix());

		instance = this;

		gameLoop();

		cleanUp();
		Logger.info("Exited");
	}

	public static GameEngine getInstance() {
		return instance;
	}

	private void createDisplay(){
		DisplayManager.createDisplay();
	}

	private void createPlayer() throws InvalidAttributesException{
		player = new Player(new Vector3f(365, 5, 52), 0, 70, 0, 1);

	}

	private void createCamera(){
		camera = new Camera(player);
	}

	// ################################ Temporary Asset Loading ################################
	private void loadAssets(){
		StaticWorldObjectAssetLoader.loadAsset("grass");

		Light sun = new Light(new Vector3f(0, 1000, -700f), new Vector3f(0.6F, 0.6F, 0.6F));
		renderer.addLight(sun);
		renderer.addLight(new Light(new Vector3f(400, 30, 50), new Vector3f(10, 0, 0), new Vector3f(1, 0.01F, 0.009F)));
		renderer.addLight(new Light(new Vector3f(200, 10, 200), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));

		guiManager = new GuiManager();
		new GoblinVillage();

		waterRenderer.processWater(new WaterTile(new Vector3f(354, 5, 15), new Vector3f(65,65,35)));

	}

	private void generateWorldObjects(){
		Random rand = new Random();

		for(int i=0;i < 5000;i++){

			if(i % 5 == 0){

				float x = (float)rand.nextInt(1600-0+1)+0;
				float z = (float)rand.nextInt(1600-0+1)+0;

				Terrain t = terriangrid.getTerrian(x, z);
				if(t !=null){
					float y = t.getHeightOfTerrain(x, z);
					worldObjs.add(new WorldObject("grass", new Vector3f(x, y, z), 0, 0, 0, 2));
				}
			}

		}
	}
	// ################################ END ################################

	private void gameLoop(){
		Logger.info("GameLoop Started");
		while(!Display.isCloseRequested()){
			update();

			//game logic
			player.move();
			camera.move();

			render();
			//update
			DisplayManager.updateDisplay();
		}
		Logger.info("GameLoop Finished");

	}

	private void update(){

		picker.update();

		if (Input.getKeyUp(Input.KEY_F1)){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		if (Input.getKeyUp(Input.KEY_F2)){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}

		if (Input.getKeyUp(Input.KEY_R)){
			TerrainGrid.getInstance().generateTerrain();
		}

		if (Input.getKeyUp(Input.KEY_F9)){
			TerrainGrid.getInstance().saveAllTerrains();
		}

		if (Input.getKeyUp(Input.KEY_F12)){
			System.out.println("x:"+player.getPosition().getX()+" y:"+player.getPosition().getY()+" z:"+player.getPosition().getZ());
		}

		if (Input.getKeyUp(Input.KEY_1)){
			mouseManager.setCurrentTool(MouseToolManager.STANDARD_MOUSE);
		}
		if (Input.getKeyUp(Input.KEY_2)){
			mouseManager.setCurrentTool(MouseToolManager.BRUSH_MOUSE);
		}

		mouseManager.update();
		Input.update();
	}

	private void render(){
		renderer.processPlayer(player);

		for(WorldObject worldObj : worldObjs){
			renderer.processWorldObject(worldObj);
		}

		guiManager.processGuis();


		renderer.render(new Vector4f(0, -1, 0, 10000000));
		waterRenderer.render(renderer.getLights().get(0));
	}

	public void cleanUp(){
		try{
			Logger.info("CleanUp Started");
			waterRenderer.cleanUp();
			renderer.cleanUp();
			Loader.getInstance().cleanUp();
			DisplayManager.closeDisplay();
			Logger.info("CleanUp Finished");
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public Player getPlayer() {
		return player;
	}

	public MousePicker getPicker() {
		return picker;
	}

}
