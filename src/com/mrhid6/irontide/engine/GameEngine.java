package com.mrhid6.irontide.engine;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mrhid6.irontide.config.ConfigHelper;
import com.mrhid6.irontide.entities.Light;
import com.mrhid6.irontide.font.TextMaster;
import com.mrhid6.irontide.guis.GuiManager;
import com.mrhid6.irontide.log.Logger;
import com.mrhid6.irontide.mousetools.MouseToolManager;
import com.mrhid6.irontide.render.DisplayManager;
import com.mrhid6.irontide.render.renderer.MasterRenderer;
import com.mrhid6.irontide.render.renderer.WaterRenderer;
import com.mrhid6.irontide.settings.Constants;
import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6.irontide.skill.Skill;
import com.mrhid6.irontide.terrians.TerrainGrid;
import com.mrhid6.irontide.utils.Loader;
import com.mrhid6.irontide.utils.MousePicker;
import com.mrhid6.irontide.world.World;
import com.mrhid6io.utils.Input;

public class GameEngine {


	private static GameEngine instance;

	private MasterRenderer renderer;
	private MousePicker picker;
	private GuiManager guiManager;

	private WaterRenderer waterRenderer;
	private MouseToolManager mouseManager;
	private World theWorld;

	private ConfigHelper config;

	public GameEngine(){
		try{
			config = new ConfigHelper();
			
			if(checkInstall()){

				new Logger();
				Logger.info("Initialized");

				createDisplay();

				TextMaster.init();

				theWorld = new World();
				renderer = new MasterRenderer();
				new TerrainGrid();
				mouseManager = new MouseToolManager();
				waterRenderer = new WaterRenderer(renderer.getProjectionMatrix());
				guiManager = new GuiManager();
				picker = new MousePicker();

				theWorld.initialize();
				picker.initialize();

				loadAssets();
				
				instance = this;

				gameLoop();

				cleanUp();
				Logger.info("Exited");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static GameEngine getInstance() {
		return instance;
	}

	public boolean checkInstall(){
		if(!GameSettings.INSTALLED && !GameSettings.DEBUG_MODE){

			JOptionPane.showMessageDialog(null, "Error: Install has been corrupted please install using the Launcher", "Error Opening "+Constants.TITLE, JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		return true;
	}

	private void createDisplay(){
		DisplayManager.createDisplay();
	}


	// ################################ Temporary Asset Loading ################################
	private void loadAssets(){

		renderer.addLight(new Light(new Vector3f(400, 80, 50), new Vector3f(10, 0, 0), new Vector3f(1, 0.01F, 0.009F)));
		renderer.addLight(new Light(new Vector3f(200, 80, 200), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));
		renderer.addLight(new Light(new Vector3f(300, 80, 200), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));
		renderer.addLight(new Light(new Vector3f(200, 80, 300), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));
		renderer.addLight(new Light(new Vector3f(280, 80, 200), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));

	}
	// ################################ END ################################

	private void gameLoop(){
		Logger.info("GameLoop Started");
		while(!Display.isCloseRequested()){
			update();
			theWorld.update();

			render();
			//update
			DisplayManager.updateDisplay();
		}
		Logger.info("GameLoop Finished");

	}

	private void update(){

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
			new Skill();
		}

		if (Input.getKeyUp(Input.KEY_1)){
			mouseManager.setCurrentTool(MouseToolManager.STANDARD_MOUSE);
		}
		if (Input.getKeyUp(Input.KEY_2)){
			mouseManager.setCurrentTool(MouseToolManager.BRUSH_MOUSE);
		}
		if (Input.getKeyUp(Input.KEY_3)){
			mouseManager.setCurrentTool(MouseToolManager.LEVEL_MOUSE);
		}

		mouseManager.update();
		Input.update();

		guiManager.update();
	}

	private void render(){

		theWorld.prepareForRender();

		guiManager.processGuis();

		renderer.render(new Vector4f(0, -1, 0, 10000000));
		waterRenderer.render(theWorld.getWorldSun());


		renderer.render2d();
	}

	public void cleanUp(){
		try{
			Logger.info("CleanUp Started");
			waterRenderer.cleanUp();
			renderer.cleanUp();
			TerrainGrid.getInstance().cleanUp();
			TextMaster.cleanUp();

			Loader.getInstance().cleanUp();
			DisplayManager.closeDisplay();

			Logger.info("CleanUp Finished");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public MousePicker getPicker() {
		return picker;
	}
	
	public ConfigHelper getConfig() {
		return config;
	}

}
