package com.soracasus.telanu.shaders.uniform;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector3f;

public class UniformVec3Array extends Uniform {

	private UniformVec3[] vectorUniforms;

	public UniformVec3Array (String name, int size) {
		super(name);
		this.vectorUniforms = new UniformVec3[size];
		for(int i = 0; i < size; i++) {
			vectorUniforms[i] = new UniformVec3(name + "[" + i + "]");
		}
	}

	@Override
	public void storeUniformLocation(int programID) {
		for(UniformVec3 u : vectorUniforms) {
			u.storeUniformLocation(programID);
		}
	}

	public void load(@NotNull Vector3f[] vectors) {
		if(vectors.length != vectorUniforms.length) {
			System.err.println("Invalid array size for Uniform Array!\n\n" + toString());
			return;
		}

		for(int i = 0; i < vectors.length; i++) {
			vectorUniforms[i].load(vectors[i]);
		}
	}


}
