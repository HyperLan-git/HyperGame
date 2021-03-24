package com.hyper.render;

import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

import com.hyper.io.ResourceLocation;

public class Shader {
	private int program, /*Vertex Shader*/vs, /*Fragment shader*/fs;

	public Shader(File vertexShader, File fragmentShader) throws IOException {
		program = glCreateProgram();
		vs = glCreateShader(GL_VERTEX_SHADER);
		fs = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(vs, ResourceLocation.readFile(vertexShader));
		glShaderSource(fs, ResourceLocation.readFile(fragmentShader));

		glCompileShader(vs);
		glCompileShader(fs);

		if(glGetShaderi(vs, GL_COMPILE_STATUS) != 1) 
			throw new IllegalStateException("Shader not compiled ! Reason : " + glGetShaderInfoLog(vs));

		if(glGetShaderi(fs, GL_COMPILE_STATUS) != 1) 
			throw new IllegalStateException("Shader not compiled ! Reason : " + glGetShaderInfoLog(fs));

		glAttachShader(program, vs);
		glAttachShader(program, fs);

		glBindAttribLocation(program, 0, "teh_ultim8_vertices_brah");
		glBindAttribLocation(program, 1, "teh_bootiful_textures");

		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) != 1)
			throw new IllegalStateException("Shader not linked ! Reason : " + glGetProgramInfoLog(program));

		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1)
			throw new IllegalStateException("Shader not linked ! Reason : " + glGetProgramInfoLog(program));
	}

	public Shader(String vertexShader, String fragmentShader) throws IOException {
		this(new ResourceLocation(vertexShader), new ResourceLocation(fragmentShader));
	}

	public Shader(ResourceLocation vertexShader, ResourceLocation fragmentShader) throws IOException {
		program = glCreateProgram();
		vs = glCreateShader(GL_VERTEX_SHADER);
		fs = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(vs, vertexShader.read());
		glShaderSource(fs, fragmentShader.read());

		glCompileShader(vs);
		glCompileShader(fs);

		if(glGetShaderi(vs, GL_COMPILE_STATUS) != 1) 
			throw new IllegalStateException("Shader not compiled ! Reason : " + glGetShaderInfoLog(vs));

		if(glGetShaderi(fs, GL_COMPILE_STATUS) != 1) 
			throw new IllegalStateException("Shader not compiled ! Reason : " + glGetShaderInfoLog(fs));

		glAttachShader(program, vs);
		glAttachShader(program, fs);

		glBindAttribLocation(program, 0, "teh_ultim8_vertices_brah");
		glBindAttribLocation(program, 1, "teh_bootiful_textures");

		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) != 1)
			throw new IllegalStateException("Shader not linked ! Reason : " + glGetProgramInfoLog(program));

		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1)
			throw new IllegalStateException("Shader not linked ! Reason : " + glGetProgramInfoLog(program));
	}

	public void bind() {
		glUseProgram(program);
	}

	public void setUniform(String name, int value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform1i(location, value);
	}

	public void setUniform(String name, float value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform1f(location, value);
	}

	public void setUniform(String name, Vector2fc value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2f(location, value.x(), value.y());
	}

	public void setUniform(String name, Vector4fc value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform4f(location, value.x(), value.y(), value.z(), value.w());
	}

	public void setUniform(String name, Matrix4f value) {
		int location = glGetUniformLocation(program, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		if(location != -1)
			glUniformMatrix4fv(location, false, buffer);
	}

	public void setUniform(String name, Color value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform4f(location, value.getRed()/256.0f, value.getGreen()/256.0f, value.getBlue()/256.0f, value.getAlpha()/256.0f);
	}
}
