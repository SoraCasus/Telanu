package com.soracasus.telanu.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import com.soracasus.telanu.core.REFile;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram {

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(REFile shaderFile) {
		ShaderIDs ids = parseShader(shaderFile);
	}

	public ShaderProgram(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanUp() {

		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void load2DVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;

		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}

	private static int loadShader(String file, int type) {

		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader " + file);
			System.exit(-1);
		}
		return shaderID;
	}

	private ShaderIDs parseShader(REFile shaderFile) {
		StringBuilder vertSrc = new StringBuilder();
		StringBuilder fragSrc = new StringBuilder();
		StringBuilder geomSrc = new StringBuilder();
		StringBuilder current = null;
		try {
			BufferedReader reader = shaderFile.getReader();
			String s;
			while((s = reader.readLine()) != null) {
				if(s.startsWith("#shader")) {
					String _s = s.toLowerCase();
					if(_s.contains("vertex")) {
						current = vertSrc;
					} else if (_s.contains("fragment")) {
						current = fragSrc;
					} else if (_s.contains("geometry")) {
						current = geomSrc;
					} else {
						System.err.println("Shader preprocessor not recognized: " + s);
					}
				} else {
					if(current == null) {
						// If the shader is not correctly formatted, inform user and default to the
						// fallback shader with bare minimum
						System.err.println("Shader: " + shaderFile.toString() + "\n\n Not correctly formatted!");
						return parseShader(new REFile("shaders/fallback.rsh"));
					} else {
						current.append(s);
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println("Shader: " + shaderFile.toString() + " Could not be read");
			return parseShader(new REFile("shaders/fallback.rsh"));
		}




	}

	private class ShaderIDs {
		public int vertID;
		public int fragID;
		public int geomID;
		// Note(Sora): OpenGL 4.5 is not supported by LWJGL 2
		// Todo(Sora): Migrate to LWJGL and add tesselation support
		// public int tessCtrlID;
		// public int tessEvalID;
	}

}
