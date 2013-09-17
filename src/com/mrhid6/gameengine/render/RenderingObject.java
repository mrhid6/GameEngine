package com.mrhid6.gameengine.render;

import java.lang.reflect.Method;

public class RenderingObject {
	
	private Object object;
	private Method m;
	
	private RenderPriority priority;
	
	public RenderingObject(Object object, Method m, RenderPriority priority) {
		this.object = object;
		this.m = m;
		this.priority = priority;
	}

	public RenderPriority getPriority() {
		return priority;
	}
	
	public Object getObject() {
		return object;
	}
	
	public Method getM() {
		return m;
	}
	
}
