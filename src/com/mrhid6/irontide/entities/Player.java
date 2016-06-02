package com.mrhid6.irontide.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.asset.AnimatedAsset;
import com.mrhid6.irontide.asset.AssetLoader;
import com.mrhid6.irontide.models.AnimatedModel;
import com.mrhid6.irontide.models.RawModel;
import com.mrhid6.irontide.render.DisplayManager;
import com.mrhid6.irontide.render.ModelData;
import com.mrhid6.irontide.render.armature.Armature;
import com.mrhid6.irontide.terrians.Terrain;
import com.mrhid6.irontide.terrians.TerrainGrid;
import com.mrhid6.irontide.textures.ModelTexture;
import com.mrhid6.irontide.utils.Loader;
import com.mrhid6.irontide.utils.loaders.ModelDataFileLoader;

public class Player extends AnimatedPlayer{
	
	private static final float RUN_SPEED = 30;
	private static final float SPRINT_SPEED = 80;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean inAir = false;
	
	public Player( Vector3f position, float rotX, float rotY, float rotZ) {
		super("PLAYER",position, rotX, rotY, rotZ, 5.5f);
		
		ModelData playerData = ModelDataFileLoader.loadMdatFromFile("/textures/testCharacter.mdat");

		RawModel playerModel = Loader.getInstance().loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices(), playerData.getBoneIDs(), playerData.getBoneWeights());
		ModelTexture playerTexture = Loader.getInstance().createModelTexture("/textures/white.png");
		playerTexture.setUseFakeLighting(false);
		playerTexture.setHasTransparency(false);
		AnimatedModel texturedPlayer = new AnimatedModel(playerModel, playerTexture, playerData.getArmature());
		
		AnimatedAsset asset = new AnimatedAsset("PLAYER");
		asset.setModel(texturedPlayer);
		Armature armature = playerData.createArmature();
		asset.setArmature(armature);
		asset.setAnimations(playerData.createAnimations(armature));
		AssetLoader.addAsset(asset);
		
		this.setAsset(asset);
		
		init();
	}
	
	public void move(){
		checkInputs();
		update();
		super.increaseRotation(0, this.currentTurnSpeed * DisplayManager.getFrameTimeSecond(), 0);
		
		float distance = currentSpeed* DisplayManager.getFrameTimeSecond();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSecond(), dz);
		
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSecond();
		
		Terrain terrian = TerrainGrid.getInstance().getTerrian(super.getPosition().x, super.getPosition().z);
		if(terrian !=null){
			float terrianHeight = terrian.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
			
			if(super.getPosition().y < terrianHeight){
				upwardSpeed = 0;
				super.getPosition().y = terrianHeight;
				inAir = false;
			}
		}else{
			upwardSpeed = 0;
			inAir = false;
		}
		
		
	}
	
	private void jump(){ 
		if(!inAir){
			this.upwardSpeed = JUMP_POWER;
			inAir = true;
		}
	}
	
	public void checkInputs(){
		
		float speedMultiplier = 0;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			speedMultiplier = SPRINT_SPEED;
		}
		this.hasMoved = false;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.hasMoved = true;
			setCurrentAction(RUNNING);
			this.currentSpeed = (RUN_SPEED + speedMultiplier);
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.hasMoved = true;
			this.currentSpeed = -(RUN_SPEED + speedMultiplier);
		}else{
			if(!inAir){
				setCurrentAction(IDLE);
				this.currentSpeed = 0;
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.hasMoved = true;
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.hasMoved = true;
			this.currentTurnSpeed = TURN_SPEED;
		}else{
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			setCurrentAction(2);
			this.hasMoved = true;
			jump();
		}
	}

}
