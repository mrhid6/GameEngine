package com.mrhid6.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.collision.Triangle;
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
	
	public static Matrix4f setTransformationMatrix(Matrix4f matrix, Vector3f transformation, float rx, float ry, float rz, float scale){

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

	public static float randomInRange(float min, float max) {
		return (float) (Math.random() < 0.5 ? ((1-Math.random()) * (max-min) + min) : (Math.random() * (max-min) + min));
	}
	
	public static float[] matrix4fToFloatArray(Matrix4f m){
		float[] v = {m.m00, m.m10, m.m20, m.m30,
	             m.m01, m.m11, m.m21, m.m31,
	             m.m02, m.m12, m.m22, m.m32,
	             m.m03, m.m13, m.m23, m.m33};
		
		return v;
	}
	
	public static void floatArrayToSysout(float[] data){
		for(int i =0;i<data.length;i++){
			System.out.println("index="+i+", data="+data[i]);
		}
	}
	
	public static Vector3f createTranslationVector(Matrix4f matrix) {
		Vector3f vector = new Vector3f();
		vector.x = matrix.m30;
		vector.y = matrix.m31;
		vector.z = matrix.m32;
		return vector;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion q, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.translate(translation, matrix, matrix);
		
		double xx = q.x * q.x;
		double xy = q.x * q.y;
		double xz = q.x * q.z;
		double xw = q.x * q.w;
		double yy = q.y * q.y;
		double yz = q.y * q.z;
		double yw = q.y * q.w;
		double zz = q.z * q.z;
		double zw = q.z * q.w;
	    
		matrix.m00 = (float) (1 - 2 * ( yy + zz ));
		matrix.m01 = (float) 	 (2 * ( xy - zw ));
		matrix.m02 = (float) 	 (2 * ( xz + yw ));
		matrix.m10 = (float) 	 (2 * ( xy + zw ));
		matrix.m11 = (float) (1 - 2 * ( xx + zz ));
		matrix.m12 = (float) 	 (2 * ( yz - xw ));
		matrix.m20 = (float) 	 (2 * ( xz - yw ));
		matrix.m21 = (float)     (2 * ( yz + xw ));
		matrix.m22 = (float) (1 - 2 * ( xx + yy ));
		matrix.m03 = matrix.m13 = matrix.m23 = 0;
		matrix.m33 = 1;
		
	    Matrix4f.scale(new Vector3f(scale,  scale,  scale), matrix, matrix);
	    
		return matrix;
	}
	
	public static boolean pointInTriangle(Vector3f p, Vector3f pa, Vector3f pb, Vector3f pc) {
		Vector3f e10 = Vector3f.sub(pb, pa, null);
		Vector3f e20 = Vector3f.sub(pc, pa, null);
		
		float a = Vector3f.dot(e10, e10);
		float b = Vector3f.dot(e10, e20);
		float c = Vector3f.dot(e20, e20);
		float ac_bb = (a * c) - (b * b);
		Vector3f vp = new Vector3f(p.x - pa.x, p.y - pa.y, p.z - pa.z);
		
		float d = Vector3f.dot(vp, e10);
		float e = Vector3f.dot(vp, e20);
		float x = (d * c) - (e * b);
		float y = (e * a) - (d * b);
		float z = x + y - ac_bb;
		
		return z < 0 && x >= 0 && y >= 0;
	}
	
	public static boolean pointInTriangle(Vector3f p, Triangle t) {
		return pointInTriangle(p, t.getVertices());
	}
	
	public static boolean pointInTriangle(Vector3f p, Vector3f[] triangle) {
		return pointInTriangle(p, triangle[0], triangle[1], triangle[2]);
	}
	
	public static float getLowestRoot(float a, float b, float c, float maxR) {
		float determinant = b * b - 4.0f * a * c;
		
		if (determinant < 0.0f) {
			return -1;
		}
		
		float sqrtD = (float) Math.sqrt(determinant);
		float r1 = (-b - sqrtD) / (2 * a);
		float r2 = (-b + sqrtD) / (2 * a);
		
		if (r1 > r2) {
			float temp = r2;
			r2 = r1;
			r1 = temp;
		}
		
		if (r1 > 0 && r1 < maxR) {
			return r1;
		}
		
		if (r2 > 0 && r2 < maxR) {
			return r2;
		}
		
		return -1;
	}

}
