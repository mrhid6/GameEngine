package com.mrhid6.irontide.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.entities.Camera;
import com.mrhid6.irontide.entities.Light;
import com.mrhid6.irontide.utils.Maths;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "/shaders/waterVertex.glsl";
	private final static String FRAGMENT_FILE = "/shaders/waterFragment.glsl";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_normalMap;
	private int location_depthMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_lightColour;
	private int location_lightPosition;
	private int location_skyColour;
	private int location_tiling;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPosition = getUniformLocation("lightPosition");
		location_depthMap = getUniformLocation("depthMap");
		location_skyColour = super.getUniformLocation("skyColour");
		location_tiling = super.getUniformLocation("tiling");
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}
	
	public void loadLight(Light sun){
		super.loadVector(location_lightColour, sun.getColour());
		super.loadVector(location_lightPosition, sun.getPosition());
	}
	
	public void loadMoveFactor(float factor){
		super.loadFloat(location_moveFactor, factor);
	}
	
	public void loadTilingFactor(float factor){
		super.loadFloat(location_tiling, factor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}
	
	public void loadSkyColour(Vector3f skyColour){
		super.loadVector(location_skyColour, skyColour);
	}

}
