package com.mrhid6.irontide.render.armature;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.mrhid6.irontide.utils.Maths;
import com.mrhid6.irontide.utils.QuaternionUtils;

public class Bone {
	
	public static float MAX_UPPERBODY_ROTATION = (float) (Math.PI / 2) / 4 * 3;

	private String ID;
	
	private Armature armature;
	private Matrix4f invBindShapeMatrix;
	private Matrix4f worldMatrix;
	
	private Transform localTransform;
	
	private Vector4f extraRotation;
	
	private ArrayList<String> childs;
	
	public Bone(Bone bone) {
		this.ID = bone.ID;
		this.invBindShapeMatrix = bone.invBindShapeMatrix;
		this.worldMatrix = new Matrix4f(bone.worldMatrix);
		this.extraRotation = new Vector4f(bone.extraRotation);
		this.localTransform = new Transform(bone.localTransform);
		this.childs = new ArrayList<String>(bone.childs);
	}
	
	public Bone(Armature armature) {
		this();
		this.armature = armature;
	}
	
	public Bone() {
		childs = new ArrayList<String>();
		extraRotation = new Vector4f();
	}
	
	public void setArmature(Armature armature) {
		this.armature = armature;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public void setParent(String parent) {
		armature.getBone(parent).addChild(ID);
	}
	
	public void addChild(String child) {
		childs.add(child);
	}
	
	public void setInvBindShapeMatrix(Matrix4f invBindShapeMatrix) {
		this.invBindShapeMatrix = invBindShapeMatrix;
	}
	
	public Matrix4f getInverseBindShapeMatrix() {
		return invBindShapeMatrix;
	}
	
	public Matrix4f getSkinningMatrix() {
		if (worldMatrix != null && invBindShapeMatrix != null) {
			return Matrix4f.mul(worldMatrix, invBindShapeMatrix, null);
		} else {
			return null;
		}
	}
	
	public void calculateWorldTransform() {
		Quaternion extra = QuaternionUtils.createFromAxisAngle(extraRotation.x, extraRotation.y, extraRotation.z, extraRotation.w);
		Quaternion q = Quaternion.mul(extra, localTransform.getRotation(), null);
		worldMatrix = Maths.createTransformationMatrix(localTransform.getTranslation(), q, 1);
		for (String child : childs) {
			armature.getBone(child).calculateWorldTransform(worldMatrix);
		}
	}
	
	public void calculateWorldTransform(Matrix4f parentWorldMatrix) {
		Quaternion extra = QuaternionUtils.createFromAxisAngle(extraRotation.x, extraRotation.y, extraRotation.z, extraRotation.w);
		Quaternion q = Quaternion.mul(extra, localTransform.getRotation(), null);
		worldMatrix = Matrix4f.mul(parentWorldMatrix, Maths.createTransformationMatrix(localTransform.getTranslation(), q, 1), null);
		for (String child : childs) {
			armature.getBone(child).calculateWorldTransform(worldMatrix);
		}
	}
	
	public void setCurrentLocalTransform(Transform trans) {
		localTransform = trans;
	}
	
	public void setLocalTransform(Matrix4f transform) {
		Quaternion bindPoseQuat = Quaternion.setFromMatrix(transform, new Quaternion());
		Vector3f bindPoseTrans = Maths.createTranslationVector(transform);
		
		localTransform = new Transform(bindPoseQuat, bindPoseTrans);
	}
	
	public void setExtraRotation(float x, float y, float z, double angle) {
		setExtraRotation(x, y, z, (float) angle);
	}
	
	public void setExtraRotation(float x, float y, float z, float angle) {
		extraRotation.x = x;
		extraRotation.y = y;
		extraRotation.z = z;
		extraRotation.w = angle;
	}
	
	public Vector4f getExtraRotation() {
		return extraRotation;
	}
	
	public String toString(int level) {
		String output = "";
		if (level > 0) {
			output += "\n";
		}
		for (int i = 0; i < level; i++) {
			output += "    ";
		}
		output += ID;
		for (String child : childs) {
			output += armature.getBone(child).toString(level + 1);
		}
		return output;
	}
}
