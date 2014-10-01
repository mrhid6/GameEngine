package com.mrhid6.engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.entities.Camera;
import com.mrhid6.entities.Entity;
import com.mrhid6.entities.Light;
import com.mrhid6.models.RawModel;
import com.mrhid6.models.TexturedModel;
import com.mrhid6.renderengine.DisplayManager;
import com.mrhid6.renderengine.Loader;
import com.mrhid6.renderengine.OBJLoader;
import com.mrhid6.renderengine.Renderer;
import com.mrhid6.shaders.StaticShader;
import com.mrhid6.textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);
		
		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));
		
		ModelTexture texture = texturedModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(0.5f);
		
		Entity entity = new Entity(texturedModel, new Vector3f(0, -5, -20), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(0, 5, -15), new Vector3f(1, 1, 1));
		
		Camera camera = new Camera();
		
		
		while(!Display.isCloseRequested()){
			
			renderer.perpare();
			//game logic
			entity.increaseRotation(0, 1, 0);
			camera.move();
			
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera);
			//render
			renderer.render(entity, shader);
			
			shader.stop();
			//update
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
