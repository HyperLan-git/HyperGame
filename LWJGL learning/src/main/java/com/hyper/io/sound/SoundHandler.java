package com.hyper.io.sound;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.alListenerfv;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import com.hyper.render.camera.ICamera;

public class SoundHandler {
	private static final SoundHandler instance = new SoundHandler();

	public static SoundHandler getInstance() {
		return instance;
	}

	private long device, context;

	private Vector3f listenerVelocity = new Vector3f();

	private final ArrayList<SoundSource> soundSources = new ArrayList<>();

	private final HashMap<String, SoundBuffer> sounds = new HashMap<>();

	private SoundHandler() {

	}

	public void init() {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		SoundBuffer soundBuffer;
		try {
			soundBuffer = new SoundBuffer("./src/main/resources/assets/hyper/sound/Resurrection_Spell.ogg");
			this.sounds.put("AH", soundBuffer);
			SoundSource s = new SoundSource(true, true);
			s.setBuffer(soundBuffer.getBufferId());
			this.soundSources.add(s);
			s.setGain(0.1f);
			s.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(ICamera camera) {
		Matrix4f invCam = new Matrix4f(camera.getProjectionMatrix()).invert();
		Vector3f at = new Vector3f(0, 0, -1);
		invCam.transformDirection(at);
		Vector3f up = new Vector3f(0, 1, 0);
		invCam.transformDirection(up);
		alListener3f(AL_POSITION, camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		alListener3f(AL_VELOCITY, listenerVelocity.x, listenerVelocity.y, listenerVelocity.z);
		alListenerfv(AL_ORIENTATION, new float[] {
				at.x, at.y, at.z,
				up.x, up.y, up.z
		});
		for(int i = soundSources.size()-1; i > 0; i--) {
			SoundSource s = soundSources.get(i);
			if(!s.isPlaying()) {
				s.cleanup();
				soundSources.remove(i);
			}
		}
	}

	public void stop() {
		for(SoundBuffer b : sounds.values())
			b.cleanup();
		for(SoundSource s : soundSources)
			s.cleanup();
		alcCloseDevice(device);
	}
	
	public void playSound(String name, Vector2fc position, Vector2fc vel, boolean looping, boolean relative) {
		
	}
}
