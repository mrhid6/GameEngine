package com.mrhid6.irontide.render.armature;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Matrix4f;

public class Armature {

	private HashMap<String, Bone> bones;
	private String[] nameArray;
	private String rootBone;
	
	private Matrix4f bindShapeMatrix;
	
	public Armature(Armature armature) {
		this.nameArray = armature.nameArray;
		this.bindShapeMatrix = armature.bindShapeMatrix;
		this.rootBone = armature.rootBone;
		this.bones = new HashMap<String, Bone>();
		for (String name : armature.bones.keySet()) {
			this.bones.put(name, new Bone(armature.bones.get(name)));
		}
	}
	
	public Armature() {
		bones = new HashMap<String, Bone>();
	}
	
	public void setArmatureToBones() {
		for (String name : this.bones.keySet()) {
			this.bones.get(name).setArmature(this);
		}
	}
	
	public ArrayList<Matrix4f> getBoneTransforms() {
		ArrayList<Matrix4f> boneTransforms = new ArrayList<Matrix4f>();
		for (String name : nameArray) {
			boneTransforms.add(bones.get(name).getSkinningMatrix());
		}
		
		return boneTransforms;
	}
	
	public ArrayList<Matrix4f> getInvBoneTransforms() {
		ArrayList<Matrix4f> invBoneTransforms = new ArrayList<Matrix4f>();
		for (String name : nameArray) {
			invBoneTransforms.add(bones.get(name).getInverseBindShapeMatrix());
		}
		return invBoneTransforms;
	}
	
	public String[] getNameArray() {
		return nameArray;
	}
	
	public void setBindShapeMatrix(Matrix4f bindShapeMatrix) {
		this.bindShapeMatrix = bindShapeMatrix;
	}
	
	public Matrix4f getBindShapeMatrix() {
		return bindShapeMatrix;
	}
	
	public Matrix4f getSkinningMatrix() {
		return bones.get(rootBone).getSkinningMatrix();
	}
	
	public Matrix4f getRootBoneSkinningMatrix() {
		return bones.get(rootBone).getSkinningMatrix();
	}
	
	public void calculateWorldMatrices() {
		bones.get(rootBone).calculateWorldTransform();
	}
	
	public void addBone(Bone bone) {
		bones.put(bone.getID(), bone);
	}
	
	public boolean hasBone(String ID) {
		return bones.containsKey(ID);
	}
	
	public Bone getBone(String ID) {
		return bones.get(ID);
	}
	
	public void setNameArray(String[] nameArray) {
		this.nameArray = nameArray;
	}
	
	public void setRootBone(String rootBone) {
		this.rootBone = rootBone;
	}
	
	public Bone getRootBone() {
		return bones.get(rootBone);
	}
	
	public boolean hasUpperBodyBone() {
		return bones.containsKey(BoneName.UPPER_BODY.getName());
	}
	
	public Bone getUpperBodyBone() {
		return bones.get(BoneName.UPPER_BODY.getName());
	}
	
	public Bone getMainHandBone() {
		return bones.get(BoneName.MAIN_HAND.getName());
	}
	
	public Bone getOffHandBone() {
		return bones.get(BoneName.OFF_HAND.getName());
	}
	
	public String toString() {
 		if (rootBone == null) {
			return "No joint added to the armature.";
		} else {
			return bones.get(rootBone).toString(0);
		}
	}
}
