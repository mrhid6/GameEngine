package com.mrhid6.gameengine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.lwjgl.util.vector.Matrix4f;


public class Utils {
	private static ResourceLocator resourceLocator = new DefaultResourceLocator();
	public static float TOLERATION=0.0000005f;

	public static void linearInterpolate(Vector3f a,Vector3f b,Vector3f c,Vector3f p)
	{
		Vector3f ab=new Vector3f();
		ab.subtract(b,a);
		Vector3f ac=new Vector3f();
		ac.subtract(c,a);
		Vector3f temp=new Vector3f();
		temp.cross(ab,ac);
		p.z=a.z-(temp.x*(p.x-a.x)+temp.y*(p.y-a.y)/temp.z);
	}

	public static String readFile(InputStream in) throws IOException {
		final StringBuffer sBuffer = new StringBuffer();
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		final char[] buffer = new char[1024];

		int cnt;
		while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
			sBuffer.append(buffer, 0, cnt);
		}
		br.close();
		in.close();
		return sBuffer.toString();
	}

	public static URL getResource(String str) {
		URL u = getResourceLocator().getResource(str);
		return u;
	}

	public static InputStream getResourceAsStream(String str) {
		InputStream in = getResourceLocator().getResourceAsStream(str);
		return in;
	}

	public static void setResourceLocator(ResourceLocator r) {
		resourceLocator = r;
	}

	public static ResourceLocator getResourceLocator() {
		return resourceLocator;
	}
	public static Matrix4f toOrtho2D(Matrix4f m, float x, float y, float width, float height) {
		return toOrtho(m, x, x + width, y + height, y, 1, -1);
	}

	public static Matrix4f toOrtho2D(Matrix4f m, float x, float y, float width, float height, float near, float far) {
		return toOrtho(m, x, x + width, y, y + height, near, far);
	}

	public static Matrix4f toOrtho(Matrix4f m, float left, float right, float bottom, float top,
			float near, float far) {
		if (m==null)
			m = new Matrix4f();
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);
		float z_orth = -2 / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		m.m00 = x_orth;
		m.m10 = 0;
		m.m20 = 0;
		m.m30 = 0;
		m.m01 = 0;
		m.m11 = y_orth;
		m.m21 = 0;
		m.m31 = 0;
		m.m02 = 0;
		m.m12 = 0;
		m.m22 = z_orth;
		m.m32 = 0;
		m.m03 = tx;
		m.m13 = ty;
		m.m23 = tz;
		m.m33 = 1;
		return m;
	}
	public static final class DefaultResourceLocator implements ResourceLocator {

		public static final File ROOT = new File(".");

		private static File createFile(String ref) {
			File file = new File(ROOT, ref);
			if (!file.exists()) {
				file = new File(ref);
			}

			return file;
		}

		public InputStream getResourceAsStream(String ref) {
			InputStream in = Utils.class.getClassLoader().getResourceAsStream(ref);
			if (in==null) { // try file system
				try { return new FileInputStream(createFile(ref)); }
				catch (IOException e) {}
			}
			return in;
		}

		public URL getResource(String ref) {
			URL url = Utils.class.getClassLoader().getResource(ref);
			if (url==null) {
				try { 
					File f = createFile(ref);
					if (f.exists())
						return f.toURI().toURL();
				} catch (IOException e) {}
			}
			return url;
		}
	}

	public static interface ResourceLocator {
		public URL getResource(String str);
		public InputStream getResourceAsStream(String str);
	}
	/**
	 * 计算三角形的面积Sabc=1/2|AB*AC|
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static float getArea(Vector3f a,Vector3f b,Vector3f c)
	{
		Vector3f ab=new Vector3f();
		ab.subtract(b,a);
		Vector3f ac=new Vector3f();
		ac.subtract(c,a);
		Vector3f temp=new Vector3f();
		temp.cross(ab,ac);
		return temp.length()/2;
	}

}