package com.soracasus.telanu.renderEngine;

import java.util.List;
import java.util.Map;

import com.soracasus.telanu.shaders.BasicShader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.soracasus.telanu.entities.Entity;
import com.soracasus.telanu.models.RawModel;
import com.soracasus.telanu.models.TexturedModel;
import com.soracasus.telanu.shaders.StaticShaderOld;
import com.soracasus.telanu.textures.ModelTexture;
import com.soracasus.telanu.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

public class EntityRenderer {

	private StaticShaderOld shaderOld;

	private BasicShader shader;

	public EntityRenderer(@NotNull BasicShader shader, Matrix4f projMat) {
		this.shader = shader;

		shader.start();
		shader.projMat.load(projMat);
		shader.stop();
	}
	
	public void render(@NotNull Map<TexturedModel, List<Entity>> entities){
		for(TexturedModel model:entities.keySet()){
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(@NotNull TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0); // position
		GL20.glEnableVertexAttribArray(1); // textureCoordinates
		GL20.glEnableVertexAttribArray(2); // normal
		ModelTexture texture = model.getTexture();
		if(texture.isHasTransparency()){
			MasterRenderer.disableCulling();			
		}
		shader.numberOfRows.load(texture.getNumberOfRows());
		shader.useFakeLighting.load(texture.isUseFakeLighting());
		shader.shineDamper.load(texture.getShineDamper());
		shader.reflectivity.load(texture.getShineDamper());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(@NotNull Entity entity){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.tfMat.load(transformationMatrix);
		shader.offset.load(new Vector2f(entity.getTextureXOffset(), entity.getTextureYOffset()));
	}
}
