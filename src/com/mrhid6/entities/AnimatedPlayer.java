package com.mrhid6.entities;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.render.armature.Animation;
import com.mrhid6.render.armature.Transform;


public class AnimatedPlayer extends AnimatedEntity{
	
	private String name;
	
	private static enum Type {
		MOVEMENT, ACTION
	}
	
	private static enum Animations {
		IDLE(Type.MOVEMENT), RUNNING(Type.MOVEMENT), JUMPING(Type.MOVEMENT), ATTACK(Type.ACTION);
		
		private Type type;
		
		private Animations(Type type) { this.type = type; }
		private static Type getType(int animation) { return Animations.values()[animation].type; };
	}
	
	public static final int IDLE 		= 0;
	public static final int RUNNING 	= 1;
	public static final int JUMPING 	= 2;
	public static final int ATTACK	 	= 3;
	
	private int movementAnimation;
	private int actionAnimation;
	private int nextAction;
	
	private static final HashMap<String, Integer> actions = new HashMap<String, Integer>();
	static {
		actions.put("IDLE", IDLE);
		actions.put("RUNNING", RUNNING);
		actions.put("JUMPING", JUMPING);
		actions.put("ATTACK", ATTACK);
	}
	
	public AnimatedPlayer( String name, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(position, rotX, rotY, rotZ, scale);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void init(){
		for (Animation anim : getAsset().getAnimations()) {
			String aniName = anim.toString();
			Integer action = actions.get(aniName.toUpperCase());
			if (action != null) {
				getAnimations().put(action, anim);
			}
		}
		
		this.movementAnimation = IDLE;
	}
	
	public void update(){
		
		HashMap<Integer, Animation> animations = getAnimations();
		getAsset().getArmature().calculateWorldMatrices();
		
		animations.get(movementAnimation).update(true);
		
		if (actionAnimation >= 0) {
			animations.get(actionAnimation).update(true);
			
			if (nextAction >= 0 && animations.get(actionAnimation).isFinished()) {
				setCurrentAction(nextAction);
				nextAction = -1;
			} else if (animations.get(actionAnimation).isFinished()) {
				animations.get(actionAnimation).reset();
				actionAnimation = -1;
			}
		}
		//setCurrentAction(RUNNING);
	}
	
	public void setCurrentAction(int action) {
		HashMap<Integer, Animation> animations = getAnimations();
		Type type = Animations.getType(action);
		if (type == Type.MOVEMENT) {
			if (action != movementAnimation) {
				Animation current = animations.get(movementAnimation);
				Transform[] lastPose = current.getCurrentPose();
				current.reset();
				movementAnimation = action;
				animations.get(movementAnimation).setLastPose(lastPose);
			}
		} else {
			if (action != actionAnimation) {
				if (actionAnimation >= 0) {
					Animation current = animations.get(actionAnimation);
					if (!current.isFinished()) {
						nextAction = action;
					} else {
						Transform[] lastPose = current.getCurrentPose();
						current.reset();
						actionAnimation = action;
						animations.get(actionAnimation).setLastPose(lastPose);
					}
				} else {
					actionAnimation = action;
				}
			}
		}
	}
}
