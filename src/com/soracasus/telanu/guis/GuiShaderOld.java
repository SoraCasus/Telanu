package com.soracasus.telanu.guis;

import org.lwjgl.util.vector.Matrix4f;

import com.soracasus.telanu.shaders.ShaderProgram_Old;

public class GuiShaderOld extends ShaderProgram_Old {
	
	private static final String VERTEX_FILE = "src/com/soracasus/telanu/guis/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/soracasus/telanu/guis/guiFragmentShader.txt";
	
	private int location_transformationMatrix;

	public GuiShaderOld () {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}