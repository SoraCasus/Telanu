package com.soracasus.telanu.shaders;

import com.soracasus.telanu.core.REFile;
import com.soracasus.telanu.shaders.uniform.Uniform;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.BufferedReader;
import java.io.IOException;

public class ShaderProgram {

	private int programID;

	public ShaderProgram (REFile shader, String... inVars) {
		ShaderIDs ids = loadShader(shader);
		this.programID = GL20.glCreateProgram();

		GL20.glUseProgram(programID);

		if (ids.vertID != -1)
			GL20.glAttachShader(programID, ids.vertID);
		if (ids.fragID != -1)
			GL20.glAttachShader(programID, ids.fragID);
		if (ids.geomID != -1)
			GL20.glAttachShader(programID, ids.geomID);

		// Bind Attributes

		GL20.glLinkProgram(programID);
		if (checkError(programID, true, GL20.GL_LINK_STATUS)) {
			System.err.println("Could not link program!");
		}

		if (ids.vertID != -1) {
			GL20.glDetachShader(programID, ids.vertID);
			GL20.glDeleteShader(ids.vertID);
		}

		if (ids.fragID != -1) {
			GL20.glDetachShader(programID, ids.fragID);
			GL20.glDeleteShader(ids.fragID);
		}

		if (ids.geomID != -1) {
			GL20.glDetachShader(programID, ids.geomID);
			GL20.glDeleteShader(ids.geomID);
		}

	}

	protected void storeUniformLocations (@NotNull Uniform... uniforms) {
		for (Uniform u : uniforms) {
			u.storeUniformLocation(programID);
		}
		GL20.glValidateProgram(programID);
		checkError(programID, true, GL20.GL_VALIDATE_STATUS);
	}

	public void start () {
		GL20.glUseProgram(programID);
	}

	public void stop () {
		GL20.glUseProgram(0);
	}

	private void bindAttributes (@NotNull String[] inVars) {
		for (int i = 0; i < inVars.length; i++)
			GL20.glBindAttribLocation(programID, i, inVars[i]);
	}

	private void loadInclude (@NotNull StringBuilder curr, @NotNull String line) {
		String name = line.substring(9);
		REFile include = new REFile("shaders/include/" + name);

		try (BufferedReader reader = include.getReader()) {
			String l;
			while ((l = reader.readLine()) != null) {
				if (l.startsWith("#include")) {
					loadInclude(curr, l);
					continue;
				}
				curr.append(l).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ShaderIDs loadShader (REFile shader) {
		ShaderIDs ids = new ShaderIDs();

		StringBuilder vertSrc = new StringBuilder();
		StringBuilder fragSrc = new StringBuilder();
		StringBuilder geomSrc = new StringBuilder();
		/*
		LWJGL2 Does not support OpenGL 4.3+
		Todo(Sora): Migrate to LWJGL3
		StringBuilder tessCtrlSrc = new StringBuilder();
		StringBuilder tessEvalSrc = new StringBuilder();
		 */

		StringBuilder current = null;

		try (BufferedReader reader = shader.getReader()) {
			String line;
			while ((line = reader.readLine()) != null) {
				// Check for the shader type
				if (line.startsWith("#shader")) {
					if (line.startsWith("#shader vertex")) {
						current = vertSrc;
					} else if (line.startsWith("#shader fragment")) {
						current = fragSrc;
					} else if (line.startsWith("#shader geometry")) {
						current = geomSrc;
					} else {
						System.err.println("Shader Error: Unrecognized preprocessor: " + line);
						return loadShader(new REFile("shaders/default.res"));
					}
					continue;
				} else if (current == null) {
					System.err.println("Shader Error: Improper shader formatting! " +
							"\n No shader is selected, make sure #shader preprocessor is defined");
					return loadShader(new REFile("shaders/default.res"));
				} else if (line.startsWith("#include")) {
					loadInclude(current, line);
					continue;
				}
				current.append(line).append("\n");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		ids.vertID = createShader(vertSrc, GL20.GL_VERTEX_SHADER);
		ids.fragID = createShader(fragSrc, GL20.GL_FRAGMENT_SHADER);
		ids.geomID = createShader(geomSrc, GL32.GL_GEOMETRY_SHADER);

		return ids;
	}

	private int createShader (@NotNull StringBuilder source, int type) {
		if (source.length() == 0) return -1;
		int id = GL20.glCreateShader(type);

		GL20.glShaderSource(id, source.toString());
		GL20.glCompileShader(id);
		if (checkError(id, false, GL20.GL_COMPILE_STATUS)) {
			return -1;
		}
		return id;
	}

	/**
	 * Checks whether an error has occurred on the given flag
	 *
	 * @param id
	 * 		The ID of the object to check the error on
	 * @param isProgram
	 * 		Whether the object is a program or not
	 * @param flag
	 * 		The flag to be checked
	 *
	 * @return True if an error has occurred, false otherwise
	 */
	private boolean checkError (int id, boolean isProgram, int flag) {
		if (isProgram) {
			int res = GL20.glGetProgram(id, flag);
			if (res != GL11.GL_TRUE) {
				int length = GL20.glGetProgram(id, GL20.GL_INFO_LOG_LENGTH);
				String log = GL20.glGetProgramInfoLog(id, length);
				System.err.println(log);
				return true;
			}
		} else {
			int res = GL20.glGetShader(id, flag);
			if (res != GL11.GL_TRUE) {
				int length = GL20.glGetShader(id, GL20.GL_INFO_LOG_LENGTH);
				String log = GL20.glGetShaderInfoLog(id, length);
				System.err.println(log);
				return true;
			}
		}
		return false;
	}


	private static class ShaderIDs {
		int vertID = -1;
		int fragID = -1;
		int geomID = -1;
	}

}
