package com.soracasus.telanu.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {

	public float current;
	public boolean used = false;

	public UniformFloat(String name) {
		super(name);
	}

	public void load(float val) {
		if(!used || current != val) {
			GL20.glUniform1f(super.getLocation(), val);
			used = true;
			current = val;
		}
	}

}
