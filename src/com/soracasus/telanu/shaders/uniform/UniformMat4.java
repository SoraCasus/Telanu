package com.soracasus.telanu.shaders.uniform;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

public class UniformMat4 extends Uniform {

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public UniformMat4(String name) {
		super(name);
	}

	public void load(@NotNull Matrix4f val) {
		matrixBuffer.clear();
		val.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(super.getLocation(), false, matrixBuffer);
	}
}
