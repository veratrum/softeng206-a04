package namesayer;

import java.lang.reflect.Array;

public class Utils {

	/**
	 * modified from
	 * https://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java
	 * 
	 * doesn't work if `s` contains spaces though
	 */
	public static String padRight(String s, int n, String padCharacter) {
		return String.format("%1$-" + n + "s", s).replace(" ", padCharacter);
	}

	public static String padLeft(String s, int n, String padCharacter) {
		return String.format("%1$" + n + "s", s).replace(" ", padCharacter);  
	}
	
	/**
	 * from https://stackoverflow.com/a/80503
	 */
	public static <T> T[] concatenate(T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
}
