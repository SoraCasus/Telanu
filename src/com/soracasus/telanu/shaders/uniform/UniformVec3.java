package com.soracasus.telanu.shaders.uniform;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

public class UniformVec3 extends Uniform {

	private boolean used = false;
	private Vector3f vec;

	public UniformVec3 (String name) {
		super(name);
	}

	public void load (@NotNull Vector3f vec) {
		if (!used || !vec.equals(this.vec)) {
			GL20.glUniform3f(super.getLocation(), vec.x, vec.y, vec.z);
			used = true;
			this.vec = vec;
		}
	}
}
