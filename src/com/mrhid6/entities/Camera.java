package com.mrhid6.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.mousetools.MouseToolManager;
import com.mrhid6.render.DisplayManager;

public class Camera {
	private final float MINZOOM=26;
	private final float MAXZOOM=86;
	private final float DEFAULT_DISTANCE = 50;

	private float distanceFromPlayer = DEFAULT_DISTANCE;
	private float newdistanceFromPlayer = DEFAULT_DISTANCE;

	private float angleAroundPlayer = 0;
	private float rotationSpeed = 0F;

	private Vector3f position = new Vector3f(100,35,50);
	private float pitch;
	private float yaw;
	private float roll;

	private Player player;

	private boolean resetAngle = false;
	private static Camera instance;

	public Camera(Player player) {
		this.player = player;
		instance = this;
	}

	public void move(){
		calculateZoom();
		animateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		animateAngle();

		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();

		calculateCameraPosition(horizontalDistance, verticalDistance);

		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);

		if(player.hasMoved() && !resetAngle){
			resetAngle = true;
		}
		
	}
	
	public void invertPitch(){
		this.pitch = -pitch;
	}
	
	public static Camera getInstance() {
		return instance;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = (player.getPosition().y + verticDistance) + 10F;
	}

	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	public void resetCamera(){
		newdistanceFromPlayer = DEFAULT_DISTANCE;
	}

	private void animateZoom(){

		if(distanceFromPlayer!=newdistanceFromPlayer){
			if(distanceFromPlayer>newdistanceFromPlayer){
				distanceFromPlayer-=5f;
			}else{
				distanceFromPlayer+=5f;
			}
			
			if( (distanceFromPlayer > newdistanceFromPlayer && distanceFromPlayer < newdistanceFromPlayer + 5f) || (distanceFromPlayer < newdistanceFromPlayer && distanceFromPlayer > newdistanceFromPlayer - 5f)){
				distanceFromPlayer = newdistanceFromPlayer;
			}
		}

		if(distanceFromPlayer < MINZOOM){
			distanceFromPlayer = MINZOOM;
		}

		if(distanceFromPlayer > MAXZOOM){
			distanceFromPlayer = MAXZOOM;
		}
	}

	private void animateAngle(){

		if(angleAroundPlayer != 0 && !Mouse.isButtonDown(0) && resetAngle){

				float halfAngle = angleAroundPlayer / 2;

				if(halfAngle > 0 && halfAngle <=90){
					rotationSpeed-=(50F * DisplayManager.getFrameTimeSecond());
				}else{
					rotationSpeed+=(50F * DisplayManager.getFrameTimeSecond());
				}
				
				angleAroundPlayer += rotationSpeed * DisplayManager.getFrameTimeSecond();

			if( (angleAroundPlayer > 0 && angleAroundPlayer <0.5F) || (angleAroundPlayer < 0 && angleAroundPlayer > -0.5F)){
				angleAroundPlayer = 0;
			}
		}

		if(angleAroundPlayer == 0){
			rotationSpeed = 0;
			resetAngle = false;
		}

		angleAroundPlayer = angleAroundPlayer%360;
	}

	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		newdistanceFromPlayer -= zoomLevel;

		if(newdistanceFromPlayer < MINZOOM){
			newdistanceFromPlayer = MINZOOM;
		}

		if(newdistanceFromPlayer > MAXZOOM){
			newdistanceFromPlayer = MAXZOOM;
		}
	}

	private void calculatePitch(){
		if(Mouse.isButtonDown(1) && MouseToolManager.getInstance().getCurrentTool() == MouseToolManager.STANDARD_MOUSE ){
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(1) && MouseToolManager.getInstance().getCurrentTool() == MouseToolManager.STANDARD_MOUSE){
			float angleChange= Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
			angleAroundPlayer = angleAroundPlayer%360;
			resetAngle = false;
		}
	}

}
