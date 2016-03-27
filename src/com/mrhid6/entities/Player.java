package com.mrhid6.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.asset.Asset;
import com.mrhid6.asset.AssetLoader;
import com.mrhid6.asset.PlayerAsset;
import com.mrhid6.asset.loader.PlayerAssetLoader;
import com.mrhid6.render.DisplayManager;
import com.mrhid6.terrians.Terrain;
import com.mrhid6.terrians.TerrainGrid;

public class Player extends Entity{
	
	private static final float RUN_SPEED = 30;
	private static final float SPRINT_SPEED = 80;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean inAir = false;
	
	private PlayerAsset playerAsset;
	
	public Player( Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(position, rotX, rotY, rotZ, scale);
		
		PlayerAssetLoader.loadAsset("player");
		Asset pa = AssetLoader.getAsset("player");
		
		if(pa instanceof PlayerAsset){
			setAsset((PlayerAsset)pa);
		}
		
	}
	
	public PlayerAsset getPlayerAsset() {
		return playerAsset;
	}
	
	public void setAsset(PlayerAsset playerAsset) {
		this.playerAsset = playerAsset;
	}
	
	public void move(){
		checkInputs();
		
		super.increaseRotation(0, this.currentTurnSpeed * DisplayManager.getFrameTimeSecond(), 0);
		
		float distance = currentSpeed* DisplayManager.getFrameTimeSecond();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		
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
		super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSecond(), dz);
		
		
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
			this.currentSpeed = (RUN_SPEED + speedMultiplier);
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.hasMoved = true;
			this.currentSpeed = -(RUN_SPEED + speedMultiplier);
		}else{
			this.currentSpeed = 0;
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
			this.hasMoved = true;
			jump();
		}
	}

}
