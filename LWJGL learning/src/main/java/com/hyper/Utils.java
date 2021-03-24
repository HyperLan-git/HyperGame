package com.hyper;

import org.joml.Vector2f;
import org.json.JSONArray;

public abstract class Utils {
	public static double getTimeSeconds() {
		return (double)System.nanoTime() / 1000000000;
	}

	public static final byte[] toBytes(int integer) {
		byte[] result = new byte[Integer.BYTES];
		for(int i = 0; i < result.length; i++)
			result[result.length-1-i] = (byte) ((integer >> i*Byte.SIZE)-((integer >> (i+1)*Byte.SIZE) << Byte.SIZE));
		return result;
	}

	public static final float getDistanceFromPointToLine(Vector2f point, Vector2f lineStart, Vector2f lineVec) {
		Vector2f am = new Vector2f(point).sub(lineStart),
				lineNormal = new Vector2f(lineVec).normalize();
		float t = lineNormal.dot(am);

		// Check to see if the point is on the line
		// if not then return the endpoint
		if(t < 0) return new Vector2f(point).sub(lineStart).length();

		// get the distance to move from point a
		Vector2f pos = new Vector2f(lineNormal).mul(t);

		// move from point a to the nearest point on the segment
		return new Vector2f(lineStart).add(pos).sub(point).length();
	}
	
	public static final double[] readAsDoubleArray(JSONArray arr) {
		double[] result = new double[arr.length()];
		for(int i = 0; i < result.length; i++)
			result[i] = arr.getDouble(i);
		return result;
	}
	
	public static final int[] readAsIntArray(JSONArray arr) {
		int[] result = new int[arr.length()];
		for(int i = 0; i < result.length; i++)
			result[i] = arr.getInt(i);
		return result;
	}
}
