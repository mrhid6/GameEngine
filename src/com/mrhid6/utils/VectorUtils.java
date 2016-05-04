package com.mrhid6.utils;

import org.lwjgl.util.vector.Vector3f;

public class VectorUtils {

	public static Vector3f lerp(Vector3f t1, Vector3f t2, float t) {
		if (t == 0f) {
			return t1;
		} else if (t == 1f) {
			return t2;
		}

		float alpha = 1f - t;
		Vector3f r = new Vector3f(t1.x * alpha, t1.y * alpha, t1.z * alpha);
		Vector3f ret = Vector3f.add(r, new Vector3f(t2.x * t, t2.y * t, t2.z * t), null);
		return ret;
	}
}
