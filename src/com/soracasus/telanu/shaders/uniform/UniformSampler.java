package com.soracasus.telanu.shaders.uniform;

import org.lwjgl.opengl.GL20;

public class UniformSampler extends Uniform {

	private boolean used = false;
	private int current;

	public UniformSampler(String name) {
		super(name);
	}

	public void load(int unit) {
		if(!used || unit != current) {
			GL20.glUniform1i(super.getLocation(), unit);
			used = true;
			current = unit;
		}
	}

}
