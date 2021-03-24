package com.hyper.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hyper.Utils;
import com.hyper.io.ResourceLocation;

public class ModelHandler {
	public static final ResourceLocation MODELS_FILE = new ResourceLocation("models/models.json");

	private Map<String, Model> models = new HashMap<String, Model>();

	public ModelHandler() {

	}

	public void readModels() throws IOException {
		JSONArray array = new JSONArray(MODELS_FILE.read());
		for(Object o : array) {
			if(!(o instanceof JSONObject))
				throw new IllegalArgumentException("Invalid array in configuration JSON !");

			JSONObject obj = (JSONObject)o;
			readModel(obj);
		}
	}
	
	private void readModel(JSONObject obj) {
		if(!obj.has("name") || !obj.has("vertices")
				|| !obj.has("texture") || !obj.has("indices") || !obj.has("mode"))
			throw new IllegalStateException("Invalid model in configuration file !"); 
		String name = obj.getString("name"), mode = obj.getString("mode");
		double[] vertices = Utils.readAsDoubleArray(obj.getJSONArray("vertices")),
				texture = Utils.readAsDoubleArray(obj.getJSONArray("texture"));
		int[] indices = Utils.readAsIntArray(obj.getJSONArray("indices"));
		int GLMode = 0;
		switch(mode) {
		case "triangles":
			GLMode = GL11.GL_TRIANGLES;
			break;
			default:
				throw new IllegalStateException("Invalid model type in configuration file !"); 
		}
		Model result = new Model(vertices, texture, indices, GLMode);
		models.put(name, result);
	}

	public void addModel(String name, Model model) {
		models.put(name, model);
	}

	public Model getModel(String name) {
		return models.get(name);
	}
}
