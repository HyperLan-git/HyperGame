package com.hyper.io.sound;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class SoundBuffer {
	protected final int bufferId = alGenBuffers();
	
	protected SoundBuffer() {}

	public SoundBuffer(String file) throws Exception {
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer data = readVorbis(file, 32 * 1024, info);
			alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, data, info.sample_rate());
			MemoryUtil.memFree(data);
		}
	}

	public SoundBuffer(ByteBuffer vorbisData) {
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, vorbisData, info.sample_rate());
			MemoryUtil.memFree(vorbisData);
		}
	}

	public SoundBuffer(ByteBuffer data, int srate) {
		alBufferData(bufferId, AL_FORMAT_MONO8, data, srate);
	}

	public int getBufferId() {
		return this.bufferId;
	}

	public void cleanup() {
		alDeleteBuffers(this.bufferId);
	}

	public static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
		ShortBuffer data = null;
		ByteBuffer vorbis = null;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			vorbis = Utils.ioResourceToByteBuffer(resource, bufferSize);
			IntBuffer error = stack.mallocInt(1);
			long decoder = stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == NULL)
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));

			stb_vorbis_get_info(decoder, info);

			int channels = info.channels();

			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

			data = MemoryUtil.memAllocShort(lengthSamples);

			data.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, data) * channels);
			stb_vorbis_close(decoder);

			return data;
		}
	}
}
