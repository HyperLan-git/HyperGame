package com.hyper.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLocation {
	public static final String RESOURCE_FILE = "./src/main/resources/assets/hyper/";
	
	private String path;
	
	public ResourceLocation(String path) {
		this.path = RESOURCE_FILE + path;
	}
	
	public String getPath() {
		return path;
	}
	
	public InputStream getAsStream() {
		return ResourceLocation.class.getResourceAsStream(path);
	}
	
	public String read() throws IOException {
		return readFile(new File(this.getPath()));
	}

	public static final String readFile(File file) throws IOException {
		String result = "";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null)
			result += line + '\n';
		br.close();
		return result;
	}
}
