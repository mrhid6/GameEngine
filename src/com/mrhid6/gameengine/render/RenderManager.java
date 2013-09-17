package com.mrhid6.gameengine.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mrhid6.gameengine.Settings;
import com.mrhid6.gameengine.camera.CameraManager;

public class RenderManager {

	private ArrayList<RenderingObject> render3D = new ArrayList<RenderingObject>();
	private ArrayList<RenderingObject> render2D = new ArrayList<RenderingObject>();

	private static RenderParser parser;

	public RenderManager() throws Exception {
		parser = new RenderParser();
	}

	public static void addToManager(Object obj){
		try {
			parser.parse(obj);
			System.out.println("added - "+obj.getClass().getSimpleName()+" - to render manager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static RenderManager newInstance() throws Exception{
		return new RenderManager();
	}

	public void render() throws Exception{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		CameraManager.getCamera().applyTranslations();
		glDisable(GL_LIGHTING);
		render3D();
		render2D();
	}

	public void render3D() throws Exception{

			
			for(RenderingObject r : render3D){
				Method m = r.getM();
				
				m.invoke(r.getObject());
			}
		
	}

	public void render2D() throws Exception{
		set2DPerpective();

		glPushMatrix();{
			glLoadIdentity();
			//glDisable(GL_LIGHTING);

			for(RenderingObject r : render2D){
				Method m = r.getM();

				m.invoke(r.getObject());
			}

			//glEnable(GL_LIGHTING);
		}glPopMatrix();
		set3DPerpective();
	}

	public void set2DPerpective(){
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(Settings.orthoProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}

	public void set3DPerpective(){
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(Settings.perProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}

	class RenderParser {
		public void parse(Object obj) throws Exception {
			Method[] methods = obj.getClass().getMethods();


			for (Method method : methods) {
				if (method.isAnnotationPresent(Render3D.class)) {
					Render3D test = method.getAnnotation(Render3D.class);
					RenderPriority priority = test.priority();

					render3D.add(new RenderingObject(obj, method, priority));
					Collections.sort(render3D, new RenderComparator());
				}
				
				if (method.isAnnotationPresent(Render2D.class)) {
					Render2D test = method.getAnnotation(Render2D.class);
					RenderPriority priority = test.priority();
					
					render2D.add(new RenderingObject(obj, method, priority));
					Collections.sort(render2D, new RenderComparator());
				}
			}
		}


	}
	
	class RenderComparator implements Comparator<RenderingObject> {
	    public int compare(RenderingObject chair1, RenderingObject chair2) {
	        return chair1.getPriority().getSlot() - chair2.getPriority().getSlot();
	    }
	}

}
