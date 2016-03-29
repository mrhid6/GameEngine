package com.mrhid6.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.entities.Camera;

public class Maths {

	private static Vector3f reuseableV3f = new Vector3f();
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		reuseableV3f.set(scale.x, scale.y, 1f);
		Matrix4f.scale(reuseableV3f, matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	/**
	 * 
	 * @param transformation - Transformation Vector3f
	 * @param rx - Rotation X Float
	 * @param ry - Rotation Y Float
	 * @param rz - Rotation Z Float
	 * @param scale - Scale Float
	 * @return Matrix4f - Transformation Matrix
	 */

	public static Matrix4f createTransformationMatrix(Vector3f transformation, Vector3f scale){

		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(transformation, matrix, matrix);

		Matrix4f.scale(scale, matrix, matrix);

		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f transformation, float rx, float ry, float rz, float scale){

		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(transformation, matrix, matrix);

		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		
		reuseableV3f.set(scale, scale, scale);
		Matrix4f.scale(reuseableV3f, matrix, matrix);

		return matrix;
	}

	/**
	 * @param camera - Takes in Camera Object
	 * @return Matrix4f
	 */

	public static Matrix4f createViewMatrix(Camera camera){

		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);

		Vector3f cameraPos = camera.getPosition();
		reuseableV3f.set(-cameraPos.x,	-cameraPos.y, -cameraPos.z);
		Matrix4f.translate(reuseableV3f, viewMatrix, viewMatrix);

		return viewMatrix;
	}

	public static float distanceSquared(Vector3f v1, Vector3f v2){
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		double dz = v1.z - v2.z;
		return (float)(dx * dx + dy * dy + dz * dz);
	}

	public static float distance(Vector3f v1,Vector3f v2){
		return (float)Math.sqrt(distanceSquared(v1,v2));
	}

	public static float maxf(float value, float max){
		if(value>max) value=max;
		return value;
	}

	public static float minf(float value, float min){
		if(value<min) value=min;
		return value;
	}

	public static float clampf(float value, float min, float max){
		value=Maths.maxf(value, max);
		value=Maths.minf(value, min);
		return value;
	}

}
