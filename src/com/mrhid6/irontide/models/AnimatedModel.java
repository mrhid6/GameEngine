package com.mrhid6.irontide.models;

import org.lwjgl.util.vector.Matrix4f;

import com.mrhid6.irontide.render.armature.Armature;
import com.mrhid6.irontide.textures.ModelTexture;

public class AnimatedModel extends TexturedModel {
	
	private Matrix4f bindShapeMatrix;

	public AnimatedModel(RawModel rawModel, ModelTexture texture, Armature armature) {
		super(rawModel, texture);
		this.bindShapeMatrix = armature.getBindShapeMatrix();
	}
	
	public AnimatedModel(RawModel rawModel, ModelTexture texture, Matrix4f bindShapeMatrix) {
		super(rawModel, texture);
		this.bindShapeMatrix = bindShapeMatrix;
	}
	public Matrix4f getBindShapeMatrix() {
		return bindShapeMatrix;
	}
}
