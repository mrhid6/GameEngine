package com.mrhid6.gameengine.render;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Render3D {
	
	RenderPriority priority() default RenderPriority.LOWEST;
	
}
