package com.hyper.io.sound;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;

import java.nio.ShortBuffer;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;
/**
 * this class is cursed and should never again be interacted with
 * DO NOT TOUCH
 */

public class AlteredSoundBuffer extends SoundBuffer {

	public AlteredSoundBuffer(String file, float newSpeed) throws Exception {
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer data = readVorbis(file, 64 * 1024, info);
			int sz = (int) (data.capacity()/newSpeed);
			sz += (info.sample_rate()-sz%info.sample_rate());
			ShortBuffer newData = MemoryUtil.memAllocShort(sz);
			for(int i = 0; i < newData.capacity() && i*newSpeed < data.capacity(); i++) {
				int corresponding = (int) (i*newSpeed);
				newData.put(data.get(corresponding));
			}
			newData.rewind();
			alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, newData, info.sample_rate());
			MemoryUtil.memFree(data);
			MemoryUtil.memFree(newData);
		}
	}
}
