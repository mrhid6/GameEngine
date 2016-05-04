package com.mrhid6.render.armature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import com.mrhid6.render.DisplayManager;
import com.mrhid6.utils.Maths;
import com.mrhid6.utils.OBJFileLoader;

public class Animation {
	
	private static final float INBETWEEN_ANIMATION_TIME = 0.1f;
	
	private String name;

	private float totalTime;
	private float[] keyFrames;
	private float currentTime;
	
	private HashMap<String, Transform[]> animationTransforms;
	
	private Transform[] currentPose;
	private Transform[] lastPose;
	
	private float currentInterpolationTime;
	private boolean interpolating = false;
	
	private int frame1;
	private int frame2;
	
	private boolean looping;
	private boolean hold;
	private boolean canContinue = false;
	
	private Armature armature;
	
	public Animation(Animation animation, Armature armature) {
		this.name = animation.name;
		this.totalTime = animation.totalTime;
		this.keyFrames = animation.keyFrames;
		this.looping = animation.looping;
		this.hold = animation.hold;
		this.armature = armature;
		this.currentPose = new Transform[0];
		this.animationTransforms = new HashMap<String, Transform[]>();
		for (String name : animation.animationTransforms.keySet()) {
			Transform[] t = new Transform[keyFrames.length];
			for (int i = 0; i < keyFrames.length; i++) {
				t[i] = new Transform(animation.animationTransforms.get(name)[i]);
			}
			this.animationTransforms.put(name, t);
		}
		this.currentPose = new Transform[animation.currentPose.length];
		this.lastPose = animation.lastPose;
	}
	
	public Animation(String name, String[] keyFrames, boolean looping, boolean hold, Armature armature) {
		this.name = name;
		float[] kFrames = new float[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			kFrames[i] = Float.valueOf(keyFrames[i]);
		}
		this.keyFrames = kFrames;
		this.looping = looping;
		this.hold = hold;
		this.totalTime = kFrames[kFrames.length - 1];
		this.armature = armature;
		
		if (name.equalsIgnoreCase("JUMPING")) {
			this.looping = false;
			this.hold = true;
		}
		
		animationTransforms = new HashMap<String, Transform[]>();
		currentPose = new Transform[0];
	}
	
	public void addBoneAnimation(String boneName, String[] strMatrices) {
		Transform[] t = new Transform[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			Matrix4f matrix = new Matrix4f();
			String[] strMatrix = new String[16];
			for (int j = 0; j < strMatrix.length; j++) {
				strMatrix[j] = strMatrices[i * 16 + j];
			}
			OBJFileLoader.readTransformationMatrixFromString(strMatrix, matrix);
			Quaternion q = new Quaternion();
			Quaternion.setFromMatrix(matrix, q);
			
			t[i] = new Transform(q, Maths.createTranslationVector(matrix));
		}
		animationTransforms.put(boneName, t);
		
		currentPose = new Transform[currentPose.length + 1];
	}
	
	public void reset() {
		currentTime = 0;
		currentInterpolationTime = 0;
	}
	
	public void update(boolean add) {
		float elapsedTime = DisplayManager.getFrameTimeSecond();
		if (interpolating) {
			currentInterpolationTime += elapsedTime;
			if (currentInterpolationTime > INBETWEEN_ANIMATION_TIME) {
				interpolating = false;
				currentInterpolationTime = INBETWEEN_ANIMATION_TIME;
			}
			float t = currentInterpolationTime / INBETWEEN_ANIMATION_TIME;
			List<String> keys = new ArrayList<String>(animationTransforms.keySet());
			for (int i = 0; i < keys.size(); i++) {
				String k = keys.get(i);
				Transform trans1 = lastPose[i];
				Transform trans2 = animationTransforms.get(k)[0];
				Transform interpolated = Transform.interpolate(trans1, trans2, t);
				armature.getBone(k).setCurrentLocalTransform(interpolated);
				
				currentPose[i] = interpolated;
			}
			
			if (!interpolating) {
				currentInterpolationTime = 0;
			}
		} else {
			currentTime += elapsedTime;
			
			if (currentTime > totalTime) {
				if (looping && (!hold || hold && canContinue)) {
					currentTime = currentTime - totalTime;
				} else {
					currentTime = totalTime;
				}
			}
			
			float frame = calculateCurrentFrame();
			frame1 = (int) Math.floor(frame);
			frame2 = (int) Math.ceil(frame);
			float t = frame - frame1;
			
			if (frame1 < 0) frame1 = looping ? keyFrames.length - 1 : 0;
			else if (frame1 >= keyFrames.length) frame1 = looping ? 0 : keyFrames.length - 1;
			if (frame2 < 0) frame2 = looping ? keyFrames.length - 1 : 0;
			else if (frame2 >= keyFrames.length) frame2 = looping ? 0 : keyFrames.length - 1;
			List<String> keys = new ArrayList<String>(animationTransforms.keySet());
			for (int i = 0; i < keys.size(); i++) {
				String k = keys.get(i);
				Transform trans1 = animationTransforms.get(k)[frame1];
				Transform trans2 = animationTransforms.get(k)[frame2];
				Transform interpolated = Transform.interpolate(trans1, trans2, t);
				armature.getBone(k).setCurrentLocalTransform(interpolated);
				
				currentPose[i] = interpolated;
			}
		}
	}
	
	public boolean isFinished() {
		return looping || currentTime >= totalTime;
	}
	
	private float calculateCurrentFrame() {
		float currentPercent = currentTime / totalTime;
		return keyFrames.length * currentPercent;
	}
	
	public Transform[] getCurrentPose() {
		return currentPose;
	}
	
	public void setLastPose(Transform[] lastPose) {
		this.lastPose = lastPose;
		this.interpolating = true;
	}
	
	public void unHold() {
		if (hold) {
			canContinue = true;
		}
	}
	
	@Override public String toString() {
		return name;
	}
}
