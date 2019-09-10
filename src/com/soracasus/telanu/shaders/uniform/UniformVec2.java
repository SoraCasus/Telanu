package com.soracasus.telanu.shaders.uniform;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;

public class UniformVec2 extends Uniform {

	private boolean used = false;
	private Vector2f vec;

	public UniformVec2 (String name) {
		super(name);
	}

	public void load(@NotNull Vector2f vec) {
		if(!used || !vec.equals(this.vec)) {
			GL20.glUniform2f(super.getLocation(), vec.x, vec.y);
			used = true;
			this.vec = vec;
		}
	}

}
