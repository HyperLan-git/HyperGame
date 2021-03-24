package com.hyper.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.hyper.io.sound.SoundBuffer;
import com.hyper.render.Shader;
import com.hyper.render.Texture;

public class ResourceHandler {
	private static final List<Class<?>> CLASSES = new ArrayList<>();
	private static final List<Class<?>> LOADERS = new ArrayList<>();

	public static final void loadAll() throws IOException {
		ArrayList<Field> fields = new ArrayList<>();
		for(Class<?> c : CLASSES) for(Field f : c.getFields()) {
			int modifiers = f.getModifiers();
			if(Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) &&
					f.isAnnotationPresent(Resource.class))
				fields.add(f);
		}

		for(Field f : fields) {
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				f.setAccessible(true);
				modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

				Resource annotation = f.getAnnotation(Resource.class);
				if(f.getType().equals(Texture.class) || f.getType().equals(SoundBuffer.class))
					f.set(null, f.getType().getConstructor(String.class).newInstance(annotation.path()[0]));
				else if(f.getType().equals(Shader.class))
					f.set(null, f.getType().getConstructor(String.class, String.class).newInstance(annotation.path()[0], annotation.path()[1]));
			} catch(Exception e) {
				e.printStackTrace();
				throw new IOException("Could not load resources for field : " + f + " !", e);
			}
		}
		
		for(Class<?> c : LOADERS) {for(Method m : c.getMethods())
			try {
				int mods = m.getModifiers();
				if(Modifier.isPublic(mods) && Modifier.isFinal(mods) && Modifier.isStatic(mods) &&
						m.isAnnotationPresent(Loader.class) && m.getParameters().length == 0) {
					m.invoke(null);
				}
			} catch(Exception e) {
				e.printStackTrace();
				throw new IOException("Could not load resources for class : " + c + "!", e);
			}
		}
	}

	public static final void registerResources(Class<?> c) {
		CLASSES.add(c);
	}
	
	public static final void registerAllResources(Class<?>... c) {
		for(Class<?> e : c) registerResources(e);
	}

	public static final void registerLoader(Class<?> c) {
		LOADERS.add(c);
	}

	public static final void registerAllLoaders(Class<?>... c) {
		for(Class<?> e : c) registerLoader(e);
	}
}
