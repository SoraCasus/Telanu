package com.soracasus.telanu.shaders.uniform;

import com.soracasus.telanu.core.RutikalEngine;
import org.lwjgl.opengl.GL20;

public class Uniform {

	private static final int NOT_FOUND = -1;

	private String name;
	private int location;

	protected Uniform(String name) {
		this.name = name;
	}

	public void storeUniformLocation(int programID) {
		 this.location = GL20.glGetUniformLocation(programID, name);
		 if(location == NOT_FOUND && RutikalEngine.DEBUG) {
		 	System.err.println("Uniform not found: " + this.toString());
		 }
	}

	public int getLocation() {
		return location;
	}

	@Override
	public String toString () {
		return "Uniform{" +
				"name='" + name + '\'' +
				", location=" + location +
				'}';
	}
}
