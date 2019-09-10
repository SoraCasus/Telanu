package com.soracasus.telanu.shaders.uniform;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Matrix4f;

public class UniformMat4Array extends Uniform {

	private UniformMat4[] matrixUniforms;

	public UniformMat4Array(String name, int size) {
		super(name);
		this.matrixUniforms = new UniformMat4[size];
		for(int i = 0; i < size; i++) {
			matrixUniforms[i] = new UniformMat4(name + "[" + i + "]");
		}
	}

	@Override
	public void storeUniformLocation(int programID) {
		for(UniformMat4 u : matrixUniforms)
			u.storeUniformLocation(programID);
	}

	public void load(@NotNull Matrix4f[] matrices) {
		if(matrices.length != matrixUniforms.length) {
			System.err.println("Invalid Matrix array size!\n\n" + toString());
			return;
		}
		for(int i = 0; i < matrices.length; i++) {
			matrixUniforms[i].load(matrices[i]);
		}
	}

}
