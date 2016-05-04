package com.mrhid6.render.armature;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.utils.QuaternionUtils;
import com.mrhid6.utils.VectorUtils;

public class Transform {

	private Quaternion q;
	private Vector3f t;
	
	public Transform(Transform transform) {
		this.q = new Quaternion(transform.q.x, transform.q.y, transform.q.z, transform.q.w);
		this.t= new Vector3f(transform.t.x, transform.t.y, transform.t.z);
	}
	
	public Transform(Quaternion q, Vector3f t) {
		this.q = q;
		this.t = t;
	}
	
	public Transform() {
		q = new Quaternion();
		t = new Vector3f();
	}
	
	public void setRotation(Quaternion rotation) {
		this.q = rotation;
	}
	
	public void setTranslation(Vector3f translation) {
		this.t = translation;
	}
	
	public Quaternion getRotation() {
		return q;
	}
	
	public Vector3f getTranslation() {
		return t;
	}
	
	public static Transform interpolate(Transform jt1, Transform jt2, float t) {
		Quaternion interpolatedQ = QuaternionUtils.slerp(jt1.getRotation(), jt2.getRotation(), t);
		Vector3f interpolatedT = VectorUtils.lerp(jt1.getTranslation(), jt2.getTranslation(), t);
		return new Transform(interpolatedQ, interpolatedT);
	}
}
