package com.hyper;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.hyper.entity.Player;
import com.hyper.entity.bullet.Bullet;
import com.hyper.entity.bullet.Laser;
import com.hyper.entity.player.PlayerCB;
import com.hyper.io.InputHandler;
import com.hyper.io.KeyBinding;
import com.hyper.io.KeyBinding.InputSource;
import com.hyper.io.Resource;
import com.hyper.io.ResourceHandler;
import com.hyper.io.Window;
import com.hyper.io.sound.SoundHandler;
import com.hyper.particle.LaserChargeParticle;
import com.hyper.render.ModelHandler;
import com.hyper.render.Shader;
import com.hyper.render.TimedAnimation;
import com.hyper.render.camera.ICamera;
import com.hyper.render.camera.SplitScreenCamera;
import com.hyper.render.gui.GuiRenderer;
import com.hyper.weapon.GrapplePistol;
import com.hyper.weapon.Pistol;
import com.hyper.world.Tile;
import com.hyper.world.TileRenderer;
import com.hyper.world.World;

public class Game extends AbstractGame {
	private static Game clientInstance;

	@Resource(path={"shader/vertexShader.txt", "shader/fragmentShader.txt"})
	public static final Shader SHADER = null;
	@Resource(path={"shader/living/vertexShader.txt", "shader/living/fragmentShader.txt"})
	public static final Shader LIVING_SHADER = null;
	@Resource(path={"shader/bullet/vertexShader.txt", "shader/bullet/fragmentShader.txt"})
	public static final Shader BULLET_SHADER = null;
	@Resource(path={"shader/gui/vertexShader.txt", "shader/gui/fragmentShader.txt"})
	public static final Shader GUI_SHADER = null;

	public static final Game getClientInstance() {
		return clientInstance;
	}

	public InputSource[] playerShootInputs = new InputSource[] {
			InputSource.MOUSE, InputSource.CONTROLLER_JOYSTICKS, InputSource.CONTROLLER_JOYSTICKS, InputSource.CONTROLLER_JOYSTICKS
	};

	public ICamera camera;
	public Window window;
	public TileRenderer tileRenderer;
	public GuiRenderer guiRenderer;
	public Shader shader, guiShader, livingShader, bulletShader;

	public ModelHandler modelHandler;

	public InputHandler handler;

	private boolean mouseLocked = false;

	public Game(World world, int fps) {
		super(world, fps);
		ResourceHandler.registerAllResources(this.getClass(), LaserChargeParticle.class, Laser.class, Bullet.class, Pistol.class, PlayerCB.class);
		ResourceHandler.registerAllLoaders(Player.class);
		clientInstance = this;
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetMonitors().get(0));
		window = new Window(videoMode.width(), videoMode.height(), "testwindow", true, glfwGetMonitors().get(0));
		handler = new InputHandler(this);
		modelHandler = new ModelHandler();
		camera = new SplitScreenCamera(window.getWidth(), window.getHeight());
		GL.createCapabilities();
		guiRenderer = new GuiRenderer();

		try {
			modelHandler.readModels();
			ResourceHandler.loadAll();
			shader = SHADER;
			livingShader = LIVING_SHADER;
			bulletShader = BULLET_SHADER;
			guiShader = GUI_SHADER;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		tileRenderer = new TileRenderer();

		theWorld.setTile(Tile.checker, 0, 0);
		theWorld.setTile(Tile.checker, theWorld.getWidth()-1, theWorld.getHeight()-1);


		for(int i = 0; i < 20; i++)
			for(int y = 2; y < 20; y++)
				theWorld.setTile(Tile.ice, i, y);
		theWorld.setTile(Tile.rock, 20, 20);
		theWorld.setTile(Tile.rock, 20, 21);
		theWorld.setTile(Tile.rock, 20, 22);
		theWorld.setTile(Tile.rock, 21, 20);
		theWorld.setTile(Tile.rock, 22, 20);

		theWorld.calculateView(window);

		thePlayers[0] = theWorld.addEntity(new PlayerCB());
		thePlayers[0].setWeapon(new GrapplePistol());
		thePlayers[1] = theWorld.addEntity(new PlayerCB());

		lockMouse();
	}

	@Override
	public void updateLogic() {
		window.update();

		camera.update();

		calculateAim();

		super.updateLogic();

		camera.update();

		SoundHandler.getInstance().update(camera);
		for(int i = 0; i < 4; i++)
			if(thePlayers[i] != null && thePlayers[i].dead()) thePlayers[i] = null;		
	}

	public void renderGame() {
		if(window == null) return;
		if(window.hasResized())
			theWorld.calculateView(window);
		window.handle();
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT);

		TimedAnimation.Timer.getInstance().update();

		if(camera instanceof SplitScreenCamera) {
			for(int i = 0; i < 4; i++) if(thePlayers[i] != null) {
				((SplitScreenCamera) camera).setRenderPhase(i);
				theWorld.render(tileRenderer, shader, camera, window);
			}
		} else
			theWorld.render(tileRenderer, shader, camera, window);
		guiRenderer.render();
	}

	private void calculateAim() {
		for(int i = 0; i < 4; i++) {
			Player p = thePlayers[i];
			if(p != null)
				switch(playerShootInputs[i]) {
				case MOUSE:
					if(camera instanceof SplitScreenCamera)
						((SplitScreenCamera)camera).setRenderPhase(i);
					Vector2f pos = getInGameCursorPos();
					p.setAiming(pos.sub(p.getPosition()));
					break;
				case CONTROLLER_JOYSTICKS:
					KeyBinding up = window.getBinding("PLAYER_" + i + "_AIM_UP"),
					down = window.getBinding("PLAYER_" + i + "_AIM_DOWN"),
					left = window.getBinding("PLAYER_" + i + "_AIM_LEFT"),
					right = window.getBinding("PLAYER_" + i + "_AIM_RIGHT");
					Vector2f aim = new Vector2f(right.howMuchDown()-left.howMuchDown(), up.howMuchDown()-down.howMuchDown());
					if(aim.lengthSquared() != 0)
						p.setAiming(aim.normalize());
					break;
				default:
				}
		}
	}

	public Vector2f getInGameCursorPos() {
		for(int i = 0; i < 4; i++) {
			Player p = thePlayers[i];
			if(p != null && playerShootInputs[i] == InputSource.MOUSE) {
				if(camera instanceof SplitScreenCamera)
					((SplitScreenCamera)camera).setRenderPhase(i);
				Vector4f v = new Vector4f(window.getMousePos().mul(2.0f/window.getWidth(), -2.0f/window.getHeight()).sub(1, -1), 0, 1)
						.mul(camera.getProjectionMatrix().invert());
				//Vector4f v = new Vector4f(window.getMousePos().mul(2.0f/window.getWidth(), -2.0f/window.getHeight()), 0, 1).add(camera.getPosition().x, camera.getPosition().y, 0, 0).mul(camera.getProjectionMatrix().invert());
				Vector2f inGameCursorPos = new Vector2f(v.x, v.y).mul(1.0f/theWorld.getScale());
				return inGameCursorPos;
			}
		}
		return null;
	}

	public boolean isMouseLocked() {
		return mouseLocked;
	}

	public void lockMouse() {
		mouseLocked = true;
		glfwSetInputMode(window.getID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	public void unlockMouse() {
		mouseLocked = false;
		glfwSetInputMode(window.getID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
}
