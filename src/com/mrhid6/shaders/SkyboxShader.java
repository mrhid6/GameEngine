package com.mrhid6.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.entities.Camera;
import com.mrhid6.render.DisplayManager;
import com.mrhid6.utils.Maths;


public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/shaders/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/skyboxFragmentShader.glsl";
	
	private static final float ROTATE_SPEED = 0.25f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColour;
	private int location_cubeMap;
	private int location_cubeMap2;
	private int location_blendFactor;

	private float rotation = 0;
	
	private Vector3f reuseableV3f = new Vector3f();
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSecond();
		reuseableV3f.set(0, 1, 0);
		Matrix4f.rotate((float) Math.toRadians(rotation), reuseableV3f, matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColour = super.getUniformLocation("fogColour");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadFogColour(Vector3f fogColour){
		super.loadVector(location_fogColour, fogColour);
	}
	
	public void loadBlendFactor(float blend){
		super.loadFloat(location_blendFactor, blend);
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}

}
