package com.soracasus.telanu.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformBoolean extends Uniform {

	private boolean used = false;
	private boolean current;

	public UniformBoolean(String name) {
		super(name);
	}

	public void load(boolean val) {
		if(!used || current != val) {
			GL20.glUniform1f(super.getLocation(), val ? 1F : 0F);
			used = true;
			current = val;
		}
	}

}
