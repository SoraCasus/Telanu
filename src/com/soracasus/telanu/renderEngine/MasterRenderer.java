package com.soracasus.telanu.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soracasus.telanu.shaders.BasicShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.soracasus.telanu.entities.Camera;
import com.soracasus.telanu.entities.Entity;
import com.soracasus.telanu.entities.Light;
import com.soracasus.telanu.guis.GuiRenderer;
import com.soracasus.telanu.guis.GuiTexture;
import com.soracasus.telanu.models.TexturedModel;
import com.soracasus.telanu.shaders.StaticShaderOld;
import com.soracasus.telanu.shaders.TerrainShaderOld;
import com.soracasus.telanu.skybox.SkyboxRenderer;
import com.soracasus.telanu.terrains.Terrain;
import com.soracasus.telanu.toolbox.Maths;
import org.lwjgl.util.vector.Vector3f;

public class MasterRenderer {

	
	private static final float RED = 0.5444f;
	private static final float GREEN = 0.62f;
	private static final float BLUE = 0.69f;

	private Matrix4f projectionMatrix;

	private BasicShader shader = new BasicShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShaderOld terrainShader = new TerrainShaderOld();
	
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	List<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private SkyboxRenderer skyboxRenderer;
	private GuiRenderer guiRenderer;
	
	public MasterRenderer(Loader loader, float timeDelay)
	{
		enableCulling();
		projectionMatrix = Maths.createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, timeDelay);
		guiRenderer = new GuiRenderer(loader);

		GuiTexture gui = new GuiTexture(loader.loadTexture("textures/socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		GuiTexture gui2 = new GuiTexture(loader.loadTexture("textures/thinmatrix"), new Vector2f(0.30f, 0.58f), new Vector2f(0.4f, 0.4f));
		GuiTexture gui3 = new GuiTexture(loader.loadTexture("textures/health"), new Vector2f(-0.74f, 0.925f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		guis.add(gui2);
		guis.add(gui3);
	}
	
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
	
	public static void enableCulling(){
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(List<Light> lights, Camera camera){
		prepare();

		shader.start();
		shader.skyColour.load(new Vector3f(RED, GREEN, BLUE));
		Vector3f[] lightPos = new Vector3f[lights.size()];
		Vector3f[] lightColour = new Vector3f[lights.size()];
		Vector3f[] attenuation = new Vector3f[lights.size()];
		for(int i = 0; i < lightPos.length; i++) {
			Light l = lights.get(i);
			lightPos[i] = l.getPosition();
			lightColour[i] = l.getColour();
			attenuation[i] = l.getAttenuation();
		}
		shader.lightPosition.load(lightPos);
		shader.lightColour.load(lightColour);
		shader.attenuation.load(attenuation);

		renderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		//guiRenderer.render(com.soracasus.telanu.guis);
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch!=null){
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp(){
		terrainShader.cleanUp();
		guiRenderer.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	//	GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	//	GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	

}