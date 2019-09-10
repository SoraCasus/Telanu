package com.soracasus.telanu.shaders;

import com.soracasus.telanu.core.REFile;
import com.soracasus.telanu.shaders.uniform.Uniform;
import com.soracasus.telanu.shaders.uniform.UniformBoolean;
import com.soracasus.telanu.shaders.uniform.UniformFloat;
import com.soracasus.telanu.shaders.uniform.UniformMat4;
import com.soracasus.telanu.shaders.uniform.UniformSampler;
import com.soracasus.telanu.shaders.uniform.UniformVec2;
import com.soracasus.telanu.shaders.uniform.UniformVec3;
import com.soracasus.telanu.shaders.uniform.UniformVec3Array;

public class BasicShader extends ShaderProgram {

	private static final REFile SHADER_FILE = new REFile("shaders/basic.res");

	public UniformMat4 tfMat = new UniformMat4("tfMat");
	public UniformMat4 projMat = new UniformMat4("projMat");
	public UniformMat4 viewMat = new UniformMat4("viewMat");
	public UniformVec3Array lightPosition = new UniformVec3Array("lightPosition", 4);
	public UniformBoolean useFakeLighting = new UniformBoolean("useFakeLighting");
	public UniformFloat numberOfRows = new UniformFloat("numberOfRows");
	public UniformVec2 offset = new UniformVec2("offset");
	public UniformSampler modelTexture = new UniformSampler("modelTexture");
	public UniformVec3Array lightColour = new UniformVec3Array("lightColour", 4);
	public UniformVec3Array attenuation = new UniformVec3Array("attenuation", 4);
	public UniformFloat shineDamper = new UniformFloat("shineDamper");
	public UniformFloat reflectivity = new UniformFloat("reflectivity");
	public UniformVec3 skyColour = new UniformVec3("skyColour");

	public BasicShader () {
		super(SHADER_FILE, "in_positions", "in_texCoord", "in_normal");
		super.storeUniformLocations(tfMat, projMat, viewMat, lightPosition, useFakeLighting, numberOfRows, offset, modelTexture, lightColour, attenuation,
				shineDamper, reflectivity, skyColour);
	}


}
