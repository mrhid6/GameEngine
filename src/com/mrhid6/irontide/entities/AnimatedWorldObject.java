package com.mrhid6.irontide.entities;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.asset.AnimatedAsset;
import com.mrhid6.irontide.render.armature.Animation;
import com.mrhid6.irontide.render.armature.Transform;


public class AnimatedWorldObject extends Entity{
	
	AnimatedAsset asset;
	private String name;
	private HashMap<Integer, Animation> animations;
	
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
	
	public AnimatedWorldObject(String name, Vector3f position) {
		super(position, 0, 0, 0, 10);
		this.name = name;
	}

	public void setAsset(AnimatedAsset asset) {
		this.asset = asset;
	}
	
	public AnimatedAsset getAsset() {
		return asset;
	}
	
	public String getName() {
		return name;
	}
	
	public void init(){
		this.animations = new HashMap<Integer, Animation>();
		for (Animation anim : asset.getAnimations()) {
			String aniName = anim.toString();
			Integer action = actions.get(aniName.toUpperCase());
			if (action != null) {
				this.animations.put(action, anim);
			}
		}
		
		this.movementAnimation = IDLE;
	}
	
	public void update() {
		
		asset.getArmature().calculateWorldMatrices();
		
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
