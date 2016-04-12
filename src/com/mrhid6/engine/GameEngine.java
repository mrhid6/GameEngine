package com.mrhid6.engine;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mrhid6.entities.Light;
import com.mrhid6.guis.GuiManager;
import com.mrhid6.log.Logger;
import com.mrhid6.mousetools.MouseToolManager;
import com.mrhid6.render.DisplayManager;
import com.mrhid6.render.renderer.MasterRenderer;
import com.mrhid6.render.renderer.WaterRenderer;
import com.mrhid6.settings.Constants;
import com.mrhid6.settings.GameSettings;
import com.mrhid6.terrians.TerrainGrid;
import com.mrhid6.utils.Loader;
import com.mrhid6.utils.MousePicker;
import com.mrhid6.water.WaterTile;
import com.mrhid6.world.World;
import com.mrhid6io.utils.Input;

public class GameEngine {


	private static GameEngine instance;

	private MasterRenderer renderer;
	private MousePicker picker;
	private GuiManager guiManager;

	private WaterRenderer waterRenderer;

	private MouseToolManager mouseManager;

	private World theWorld;

	public GameEngine(){
		try{

			if(checkInstall()){
				
				new Logger();
				Logger.info("Initialized");

				createDisplay();
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
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		instance = this;

		gameLoop();

		cleanUp();
		Logger.info("Exited");
	}

	public static GameEngine getInstance() {
		return instance;
	}

	public boolean checkInstall(){
		if(!checkProgramDataConf() || getProgramDataConfInstDir()==null){

			JOptionPane.showMessageDialog(null, "Error: Install has been corrupted please install using the Launcher", "Error Opening "+Constants.TITLE, JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		return true;
	}

	private boolean checkProgramDataConf(){
		String programdataConf = GameSettings.PROGRAMDATADIR + Constants.FS + "config.json";
		File config = new File(programdataConf);

		if(!config.exists()){return false;}

		return true;
	}

	private String getProgramDataConfInstDir(){
		String programdataConf = GameSettings.PROGRAMDATADIR + Constants.FS + "config.json";
		try {
			JSONObject config = Loader.getInstance().loadJSONFR(programdataConf);
			GameSettings.INSTALLDIR = config.getString("installdir");
			return GameSettings.INSTALLDIR;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void createDisplay(){
		DisplayManager.createDisplay();
	}


	// ################################ Temporary Asset Loading ################################
	private void loadAssets(){

		renderer.addLight(new Light(new Vector3f(400, 30, 50), new Vector3f(10, 0, 0), new Vector3f(1, 0.01F, 0.009F)));
		renderer.addLight(new Light(new Vector3f(200, 10, 200), new Vector3f(0, 0, 10), new Vector3f(1, 0.01F, 0.002F)));

		waterRenderer.processWater(new WaterTile(new Vector3f(354, 5, 15), new Vector3f(65,65,35)));
		waterRenderer.processWater(new WaterTile(new Vector3f(0, 10, 15), new Vector3f(65,65,35)));

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
			Vector3f playerPos = theWorld.getWorldPlayer().getPosition();
			System.out.println("x:"+playerPos.getX()+" y:"+playerPos.getY()+" z:"+playerPos.getZ());
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

		theWorld.prepareForRender();

		guiManager.processGuis();

		renderer.render(new Vector4f(0, -1, 0, 10000000));
		waterRenderer.render(theWorld.getWorldSun());
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

	public MousePicker getPicker() {
		return picker;
	}

}
