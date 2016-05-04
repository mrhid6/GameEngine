package com.mrhid6.utils;

import org.lwjgl.util.vector.Quaternion;

public class QuaternionUtils {
	
	public static Quaternion createFromAxisAngle(float x, float y, float z, float angle) {
		float result = (float) Math.sin(angle / 2.0);

	    x = x * result;
	    y = y * result;
	    z = z * result;

	    float w = (float) Math.cos(angle / 2.0);

	    Quaternion q = new Quaternion(x, y, z, w);
	    q.normalise();
	    return q;
	}

	public static Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
		if (t == 0f) {
			return q1;
		} else if (t == 1f) {
			return q2;
		}

		float cosTheta = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w;
		
		Quaternion qq2 = new Quaternion();
		qq2.x = q2.x; qq2.y = q2.y; qq2.z = q2.z; qq2.w = q2.w;

		if (cosTheta < 0.0f) {
			qq2.x = -qq2.x; qq2.y = -qq2.y; qq2.z = -qq2.z; qq2.w = -qq2.w;
			cosTheta = -cosTheta;
		}

		float beta = 1.0f - t;

		float scale0 = 1.0f - t;
		float scale1 = t;

		if (1.0f - cosTheta > 0.1f) {
			float theta = (float) Math.acos(cosTheta);
			float sinTheta = (float) Math.sin(theta);
			scale0 = (float) Math.sin(theta * beta) / sinTheta;
			scale1 = (float) Math.sin(theta * t) / sinTheta;
		}

		Quaternion qInterpolated = new Quaternion();

		qInterpolated.x = scale0 * q1.x + scale1 * qq2.x;
		qInterpolated.y = scale0 * q1.y + scale1 * qq2.y;
		qInterpolated.z = scale0 * q1.z + scale1 * qq2.z;
		qInterpolated.w = scale0 * q1.w + scale1 * qq2.w;

		return qInterpolated;
	}
}
