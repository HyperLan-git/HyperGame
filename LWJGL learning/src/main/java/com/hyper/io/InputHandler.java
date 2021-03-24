package com.hyper.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

import com.hyper.Game;
import com.hyper.entity.Direction;
import com.hyper.entity.Player;
import com.hyper.io.KeyBinding.InputSource;
import com.hyper.weapon.Weapon;

public final class InputHandler implements BindingListener {
	public static final ResourceLocation CONFIG_FILE = new ResourceLocation("controls.json");
	private Game game;

	public InputHandler(Game game) {
		this.game = game;
		this.initKeyBindings();
	}

	private void initKeyBindings() {
		try {
			readBindings(new JSONArray(CONFIG_FILE.read()), "");
		} catch (JSONException | IOException e) {
			throw new IllegalStateException("Error while reading key bindings !", e);
		}
	}

	private void readBindings(JSONArray array, String bindingsPrefix) throws IllegalArgumentException {
		for(Object o : array) {
			if(!(o instanceof JSONObject))
				throw new IllegalArgumentException("Invalid array in configuration JSON !");
			JSONObject json = (JSONObject)o;
			readBinding(json, bindingsPrefix.isEmpty()?"":bindingsPrefix + "_");
		}
	}

	private void readBinding(JSONObject bindingObject, String bindingPrefix) throws IllegalArgumentException {
		if(!bindingObject.has("type"))
			throw new IllegalArgumentException("JSON binding does not have an input type !");
		if(!bindingObject.has("name"))
			throw new IllegalArgumentException("JSON binding does not have a name !");
		KeyBinding newBinding = null;
		//FIXME ABSOLUTELY DISGUSTING should use a variable to save input source type
		switch(bindingObject.optString("type")) {
		case "SUB":
			JSONArray array = bindingObject.optJSONArray("sub");
			if(array == null)
				throw new IllegalArgumentException("Invalid structure !");
			readBindings(array, bindingPrefix.isEmpty()?bindingObject.getString("name"):bindingPrefix + bindingObject.getString("name"));
			return;
		case "JOYSTICK":
			if(!bindingObject.has("value"))
				throw new IllegalArgumentException("JSON binding does not have a value !");
			newBinding = new KeyBinding(bindingPrefix + bindingObject.getString("name"), game.window, InputSource.CONTROLLER_JOYSTICKS, bindingObject.getInt("value"));
			break;
		case "BUTTON":
			if(!bindingObject.has("value"))
				throw new IllegalArgumentException("JSON binding does not have a value !");
			newBinding = new KeyBinding(bindingPrefix + bindingObject.getString("name"), game.window, InputSource.CONTROLLER_BUTTONS, bindingObject.getInt("value"));
			break;
		case "KEYBOARD":
			if(!bindingObject.has("value"))
				throw new IllegalArgumentException("JSON binding does not have a value !");
			newBinding = new KeyBinding(bindingPrefix + bindingObject.getString("name"), game.window, InputSource.KEYBOARD, bindingObject.getInt("value"));
			break;
		case "MOUSE":
			if(!bindingObject.has("value"))
				throw new IllegalArgumentException("JSON binding does not have a value !");
			newBinding = new KeyBinding(bindingPrefix + bindingObject.getString("name"), game.window, InputSource.MOUSE, bindingObject.getInt("value"));
			break;
		default:
			throw new IllegalArgumentException("JSON binding does not have a valid input type !");
		}
		newBinding.setSensibility(bindingObject.getFloat("sensibility"));
		game.window.registerKeyBinding(newBinding);
		game.window.addBindingListener(bindingPrefix + bindingObject.getString("name"), this);
	}

	public void serializeKeyBindings() {
		ArrayList<KeyBindingGroup> groups = new ArrayList<KeyBindingGroup>();
		for(KeyBinding binding : game.window.getKeyBindings())
			getGroups(binding, groups);
		JSONArray root = new JSONArray(),
				current = root;
		for(KeyBindingGroup group : groups) {
			String[] path = group.getFullPath().split("_");
			for(int i = 0; i < path.length; i++) {
				String subPath = path[i];
				JSONObject obj = getGroup(current, subPath);
				boolean add = obj == null;
				JSONArray array = null;
				if(add) {
					obj = new JSONObject();
					array = new JSONArray();
					obj.put("name", subPath).put("sub", array).put("type", "SUB");
					current.put(obj);
				} else
					array = obj.getJSONArray("sub");
				current = array;
			}
			current = root;
		}
		for(KeyBinding binding : game.window.getKeyBindings())
			serialize(binding, root);
		try {
			File file = new File(CONFIG_FILE.getPath());
			file.delete();
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(root.toString(3));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error while saving key bindings !", e);
		}
	}

	private void serialize(KeyBinding binding, JSONArray root) {
		String[] path = binding.getName().split("_");
		JSONArray array = root;
		String finalName = null;
		for(int i = 0; i < path.length; i++) {
			JSONArray found = null;
			for(Object obj : array) {
				if(!(obj instanceof JSONObject))
					continue;
				JSONObject json = (JSONObject)obj;
				if(path[i].contentEquals(json.optString("name")))
					found = json.optJSONArray("sub");
			}
			finalName = "";
			for(int j = i; j < path.length; j++)
				finalName += path[j];
			if(found == null)
				break;
			array = found;
		}
		serialize(binding, finalName, array);
	}

	private void serialize(KeyBinding binding, String finalName, JSONArray array) {
		String type = "";
		switch(binding.getInputType()) {
		case CONTROLLER_BUTTONS:
			type = "BUTTON";
			break;
		case CONTROLLER_JOYSTICKS:
			type = "JOYSTICK";
			break;
		case KEYBOARD:
			type = "KEYBOARD";
			break;
		case MOUSE:
			type = "MOUSE";
		default:
		}
		array.put(new JSONObject().put("type", type).put("value", binding.getKeyID()).put("name", finalName).put("sensibility", binding.getSensibility()));
	}

	private JSONObject getGroup(JSONArray array, String groupName) {
		for(Object obj : array) {
			if(!(obj instanceof JSONObject))
				continue;
			JSONObject json = (JSONObject)obj;
			if(groupName.contentEquals(json.optString("name")))
				return json;
		}
		return null;
	}

	private void getGroups(KeyBinding binding, ArrayList<KeyBindingGroup> currentGroups) {
		String[] path = binding.getName().split("_");
		if(path.length == 1)
			return;
		KeyBindingGroup last = null;
		for(int i = 0; i < path.length-1; i++) {
			last = new KeyBindingGroup(path[i], last);
			if(!currentGroups.contains(last))
				currentGroups.add(last);
		}
	}

	@Override
	public void onPressed(KeyBinding binding) {
		checkPlayerControls(binding);
		switch(binding.getName()) {
		case "EXIT":
			GLFW.glfwSetWindowShouldClose(game.window.getID(), true);
			break;
		default:
		}
	}

	@Override
	public void onDown(KeyBinding binding) {
		checkPlayerControls(binding);
	}

	@Override
	public void onReleased(KeyBinding binding) {

	}

	private void checkPlayerControls(KeyBinding binding) {
		for(int i = 0; i < 4; i++) {
			if(game.thePlayers[i] != null && binding.getName().startsWith("PLAYER_" + i + "_")) {
				Player p = game.thePlayers[i];
				if(p.hitstunTimer > 0) continue;
				switch(binding.getName().replaceFirst("PLAYER_" + i + "_", "")) {
				case "MOVE_UP":
					p.move(Direction.UP, binding.howMuchDown());
					break;
				case "MOVE_DOWN":
					p.move(Direction.DOWN, binding.howMuchDown());
					break;
				case "MOVE_LEFT":
					p.move(Direction.LEFT, binding.howMuchDown());
					break;
				case "MOVE_RIGHT":
					p.move(Direction.RIGHT, binding.howMuchDown());
					break;
				case "SHOOT":
					Weapon w = p.getWeapon();
					if(w.isAutomatic() || binding.isPressed())
						p.shoot();
					break;
				case "DEFEND":
					p.defend();
					break;
				case "RELOAD":
					p.getWeapon().reload();
					break;
				default:
				}
			}
		}
	}
}
